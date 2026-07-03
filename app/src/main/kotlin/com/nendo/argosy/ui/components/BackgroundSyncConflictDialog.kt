package com.nendo.argosy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nendo.argosy.data.sync.ConflictInfo
import com.nendo.argosy.ui.primitives.FocusIndicators
import com.nendo.argosy.ui.primitives.argosyFocusIndicators
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme
import com.nendo.argosy.ui.util.clickableNoFocus
import java.time.Duration
import java.time.Instant

@Composable
fun BackgroundSyncConflictDialog(
    conflictInfo: ConflictInfo,
    focusIndex: Int,
    onKeepLocal: () -> Unit,
    onKeepServer: () -> Unit,
    onSkip: () -> Unit
) {
    val localTimeStr = conflictInfo.localTimestamp.toRelativeString()
    val serverTimeStr = conflictInfo.serverTimestamp.toRelativeString()
    val localIsNewer = conflictInfo.localTimestamp.isAfter(conflictInfo.serverTimestamp)

    Modal(
        title = "Save Sync Conflict",
        baseWidth = 400.dp,
        onDismiss = onSkip,
        titleContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                GameTitle(
                    title = conflictInfo.gameName,
                    titleStyle = MaterialTheme.typography.titleMedium,
                    titleColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(Dimens.spacingMd))
                Text(
                    text = conflictInfo.channelName ?: "Default Save",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    ) {
        Text(
            text = if (conflictInfo.isHashConflict)
                "Your local save has changed since the last sync. Pick which save wins."
            else
                "A newer save exists on the server. Pick which save wins.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Dimens.spacingMd))

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)) {
            ConflictChoiceRow(
                icon = Icons.Default.PhoneAndroid,
                label = "Local",
                timestamp = localTimeStr,
                isNewer = localIsNewer,
                isFocused = focusIndex == 0,
                onClick = onKeepLocal
            )
            ConflictChoiceRow(
                icon = Icons.Default.Cloud,
                label = "Server",
                subtitle = conflictInfo.serverDeviceName,
                timestamp = serverTimeStr,
                isNewer = !localIsNewer,
                isFocused = focusIndex == 1,
                onClick = onKeepServer
            )
            ConflictChoiceRow(
                label = "Skip",
                subtitle = "Keep both for now",
                isFocused = focusIndex == 2,
                onClick = onSkip
            )
        }
    }
}

@Composable
private fun ConflictChoiceRow(
    label: String,
    isFocused: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    subtitle: String? = null,
    timestamp: String? = null,
    isNewer: Boolean = false
) {
    val theme = LocalArgosyTheme.current
    val shape = RoundedCornerShape(Dimens.radiusControl)
    val labelColor = when {
        isFocused -> lerp(theme.focusAccent, Color.White, 0.45f)
        isNewer -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val metaColor = when {
        isFocused -> lerp(theme.focusAccent, Color.White, 0.45f).copy(alpha = 0.65f)
        isNewer -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .argosyFocusIndicators(
                focused = isFocused,
                indicators = FocusIndicators.NavRow,
                shape = shape
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickableNoFocus(onClick = onClick)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.size(Dimens.iconMd)
            )
            Spacer(modifier = Modifier.width(Dimens.spacingSm))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isNewer) FontWeight.Bold else FontWeight.Normal,
                color = labelColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = metaColor
                )
            }
        }
        if (timestamp != null) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = metaColor
            )
        }
    }
}

private fun Instant.toRelativeString(): String {
    val now = Instant.now()
    val duration = Duration.between(this, now)
    return when {
        duration.isNegative -> "in the future"
        duration.toMinutes() < 1 -> "just now"
        duration.toHours() < 1 -> "${duration.toMinutes()} minutes ago"
        duration.toDays() < 1 -> "${duration.toHours()} hours ago"
        duration.toDays() < 30 -> "${duration.toDays()} days ago"
        else -> "${duration.toDays() / 30} months ago"
    }
}
