package com.nendo.argosy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nendo.argosy.data.social.NetplayInvitePayload
import com.nendo.argosy.ui.primitives.ActionButton
import com.nendo.argosy.ui.theme.Dimens

@Composable
fun NetplayInviteModal(
    invite: NetplayInvitePayload,
    focusedButton: Int,
    onJoin: () -> Unit,
    onDismiss: () -> Unit
) {
    Modal(
        title = "Netplay Invite",
        baseWidth = 400.dp,
        onDismiss = onDismiss
    ) {
        Text(
            text = "${invite.hostUsername} invited you to play",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Dimens.spacingXs))

        Text(
            text = invite.gameTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens.spacingLg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingMd)
        ) {
            ActionButton(
                label = "Dismiss",
                onClick = onDismiss,
                focused = focusedButton == 0,
                modifier = Modifier.weight(1f)
            )

            ActionButton(
                label = "Join",
                onClick = onJoin,
                focused = focusedButton == 1,
                primary = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
