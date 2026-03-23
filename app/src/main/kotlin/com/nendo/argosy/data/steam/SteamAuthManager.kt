package com.nendo.argosy.data.steam

import android.util.Log
import com.nendo.argosy.data.local.dao.SteamAccountDao
import com.nendo.argosy.data.local.entity.SteamAccountEntity
import `in`.dragonbra.javasteam.enums.EResult
import `in`.dragonbra.javasteam.steam.authentication.AuthPollResult
import `in`.dragonbra.javasteam.steam.authentication.AuthSessionDetails
import `in`.dragonbra.javasteam.steam.authentication.QrAuthSession
import `in`.dragonbra.javasteam.steam.steamclient.SteamClient
import `in`.dragonbra.javasteam.steam.handlers.steamuser.SteamUser
import `in`.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails
import `in`.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback
import `in`.dragonbra.javasteam.types.SteamID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SteamAuthManager"

sealed class QrAuthState {
    data object Idle : QrAuthState()
    data object Starting : QrAuthState()
    data class WaitingForScan(val challengeUrl: String) : QrAuthState()
    data object Polling : QrAuthState()
    data class Success(val username: String, val steamId: Long) : QrAuthState()
    data class Error(val message: String) : QrAuthState()
}

sealed class SteamAuthEvent {
    data class LoggedIn(val steamId: Long, val username: String) : SteamAuthEvent()
    data object LoggedOut : SteamAuthEvent()
    data class LoginFailed(val reason: String) : SteamAuthEvent()
}

@Singleton
class SteamAuthManager @Inject constructor(
    private val steamAccountDao: SteamAccountDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var steamClient: SteamClient? = null
    private var steamUser: SteamUser? = null
    private var qrAuthSession: QrAuthSession? = null
    private var authPollJob: Job? = null

    private val _qrAuthState = MutableStateFlow<QrAuthState>(QrAuthState.Idle)
    val qrAuthState: StateFlow<QrAuthState> = _qrAuthState.asStateFlow()

    private val _authEvents = MutableSharedFlow<SteamAuthEvent>()
    val authEvents: SharedFlow<SteamAuthEvent> = _authEvents.asSharedFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private var pendingAuthResult: AuthPollResult? = null

    fun onConnected(client: SteamClient, user: SteamUser) {
        steamClient = client
        steamUser = user
        Log.d(TAG, "Steam client connected, ready for auth")

        scope.launch {
            val savedAccount = steamAccountDao.getActiveAccount()
            if (savedAccount != null) {
                Log.d(TAG, "Found saved account, attempting auto-login")
                loginWithRefreshToken(savedAccount)
            }
        }
    }

    fun onDisconnected() {
        _isLoggedIn.value = false
        cancelQrAuth()
    }

    fun onLoggedOn(callback: LoggedOnCallback) {
        val steamId = callback.clientSteamID ?: return
        val result = pendingAuthResult
        pendingAuthResult = null

        _isLoggedIn.value = true
        Log.d(TAG, "Set isLoggedIn = true")

        if (result != null) {
            scope.launch {
                saveAccount(steamId, result)
                _authEvents.emit(
                    SteamAuthEvent.LoggedIn(
                        steamId.convertToUInt64(),
                        result.accountName
                    )
                )
            }
        }

        _qrAuthState.value = QrAuthState.Success(
            username = result?.accountName ?: "Unknown",
            steamId = steamId.convertToUInt64()
        )
    }

    fun onLoginFailed(result: EResult) {
        scope.launch {
            _authEvents.emit(SteamAuthEvent.LoginFailed(result.name))
            if (result == EResult.AccessDenied || result == EResult.Expired ||
                result == EResult.InvalidLoginAuthCode || result == EResult.AccountLogonDenied) {
                Log.w(TAG, "Token rejected ($result), clearing saved account")
                steamAccountDao.deactivateAll()
            }
        }
        _qrAuthState.value = QrAuthState.Error("Login failed: ${result.name}")
    }

    fun startQrAuth() {
        val client = steamClient ?: run {
            _qrAuthState.value = QrAuthState.Error("Not connected to Steam")
            return
        }

        cancelQrAuth()
        _qrAuthState.value = QrAuthState.Starting

        scope.launch {
            try {
                Log.d(TAG, "Starting QR auth session")
                val authDetails = AuthSessionDetails()
                authDetails.deviceFriendlyName = "Argosy Launcher"

                val session = client.authentication.beginAuthSessionViaQR(authDetails).await()
                qrAuthSession = session

                _qrAuthState.value = QrAuthState.WaitingForScan(session.challengeUrl)
                Log.d(TAG, "QR challenge URL: ${session.challengeUrl}")

                startAuthPolling(session)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start QR auth", e)
                _qrAuthState.value = QrAuthState.Error(e.message ?: "Failed to start QR auth")
            }
        }
    }

    private fun startAuthPolling(session: QrAuthSession) {
        authPollJob?.cancel()
        authPollJob = scope.launch {
            Log.d(TAG, "Starting auth poll loop")
            while (isActive) {
                try {
                    val result = session.pollAuthSessionStatus().await()
                    if (result != null) {
                        Log.d(TAG, "Auth poll success: ${result.accountName}")
                        _qrAuthState.value = QrAuthState.Polling
                        pendingAuthResult = result
                        loginWithAuthResult(result)
                        break
                    }

                    val currentState = _qrAuthState.value
                    if (currentState is QrAuthState.WaitingForScan &&
                        session.challengeUrl != currentState.challengeUrl
                    ) {
                        Log.d(TAG, "QR challenge URL refreshed")
                        _qrAuthState.value = QrAuthState.WaitingForScan(session.challengeUrl)
                    }

                    delay(session.pollingInterval.toLong() * 1000)
                } catch (e: Exception) {
                    Log.e(TAG, "Auth poll error", e)
                    _qrAuthState.value = QrAuthState.Error(e.message ?: "Polling failed")
                    break
                }
            }
        }
    }

    private fun loginWithAuthResult(result: AuthPollResult) {
        val user = steamUser ?: run {
            _qrAuthState.value = QrAuthState.Error("Steam user handler not available")
            return
        }

        Log.d(TAG, "Logging in with auth result for ${result.accountName}")
        val logonDetails = LogOnDetails()
        logonDetails.username = result.accountName
        logonDetails.accessToken = result.refreshToken

        user.logOn(logonDetails)
    }

    private fun loginWithRefreshToken(account: SteamAccountEntity) {
        val user = steamUser ?: run {
            Log.e(TAG, "Steam user handler not available for auto-login")
            return
        }
        val client = steamClient ?: run {
            Log.e(TAG, "Steam client not available for auto-login")
            return
        }

        if (!client.isConnected) {
            Log.w(TAG, "Steam client not fully connected yet, deferring auto-login")
            return
        }

        Log.d(TAG, "Auto-login with saved token for ${account.username}")
        val logonDetails = LogOnDetails()
        logonDetails.username = account.username
        logonDetails.accessToken = account.refreshToken

        user.logOn(logonDetails)
    }

    private suspend fun saveAccount(steamId: SteamID, result: AuthPollResult) {
        steamAccountDao.deactivateAll()

        val existing = steamAccountDao.getBySteamId(steamId.convertToUInt64())
        if (existing != null) {
            steamAccountDao.update(
                existing.copy(
                    username = result.accountName,
                    refreshToken = result.refreshToken,
                    isActive = true,
                    lastLoginAt = Instant.now()
                )
            )
            Log.d(TAG, "Updated existing account: ${result.accountName}")
        } else {
            steamAccountDao.insert(
                SteamAccountEntity(
                    steamId = steamId.convertToUInt64(),
                    username = result.accountName,
                    refreshToken = result.refreshToken,
                    isActive = true,
                    lastLoginAt = Instant.now()
                )
            )
            Log.d(TAG, "Saved new account: ${result.accountName}")
        }
    }

    fun cancelQrAuth() {
        authPollJob?.cancel()
        authPollJob = null
        qrAuthSession = null
        _qrAuthState.value = QrAuthState.Idle
    }

    fun logout() {
        steamUser?.logOff()
        scope.launch {
            steamAccountDao.deactivateAll()
            _authEvents.emit(SteamAuthEvent.LoggedOut)
        }
        _qrAuthState.value = QrAuthState.Idle
    }

    suspend fun getActiveAccount(): SteamAccountEntity? {
        return steamAccountDao.getActiveAccount()
    }

    suspend fun deleteAccount(accountId: Long) {
        steamAccountDao.delete(accountId)
    }
}
