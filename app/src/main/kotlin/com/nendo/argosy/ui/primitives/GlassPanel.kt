package com.nendo.argosy.ui.primitives

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.nendo.argosy.ui.theme.Dimens
import com.nendo.argosy.ui.theme.LocalArgosyTheme

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val theme = LocalArgosyTheme.current
    val shape = RoundedCornerShape(Dimens.radiusPanel)
    Box(
        modifier = modifier
            .clip(shape)
            .background(theme.surfaceRaised.copy(alpha = 0.92f), shape)
            .border(width = Dimens.borderThin, color = theme.hairlineLow, shape = shape),
        content = content,
    )
}
