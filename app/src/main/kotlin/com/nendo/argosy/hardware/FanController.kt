package com.nendo.argosy.hardware

import com.nendo.argosy.util.AppPaths
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FanController @Inject constructor() {

    private val speedFile = File(AppPaths.FAN_SPEED_PATH)
    private val stateFile = File(AppPaths.FAN_STATE_PATH)
    private val dutyFile = File(AppPaths.FAN_DUTY_PATH)

    fun isAvailable(): Boolean = speedFile.exists()

    fun isControllable(): Boolean = stateFile.exists()

    fun setSpeed(duty: Int): Boolean = runCatching {
        stateFile.writeText("1")
        dutyFile.writeText(duty.toString())
    }.isSuccess

    fun release(): Boolean = runCatching {
        stateFile.writeText("0")
    }.isSuccess
}
