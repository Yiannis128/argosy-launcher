package com.nendo.argosy.libretro.touch

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nendo.argosy.data.preferences.BuiltinEmulatorSettings
import com.swordfish.libretrodroid.GLRetroView

@Composable
fun OnScreenControlsHost(
    retroView: GLRetroView,
    platformSlug: String,
    orientation: Int,
    isGamepadConnected: Boolean,
    settings: BuiltinEmulatorSettings,
    rotationKey: Int,
    modifier: Modifier = Modifier
) {
    if (!settings.showTouchControlsWhenNoGamepad) return
    if (isGamepadConnected) return

    val spec = remember(platformSlug, settings.touchControlsGenesis6Button, settings.touchControlsColouredFaceButtons) {
        TouchLayoutRegistry.forPlatform(
            platformSlug,
            genesis6Button = settings.touchControlsGenesis6Button,
            colouredPsx = settings.touchControlsColouredFaceButtons
        )
    }
    val resolved = remember(
        spec,
        orientation,
        settings.touchControlsSwapHanded,
        settings.touchControlsSizeScale,
        settings.touchControlsMirror180,
        rotationKey
    ) {
        LayoutDefaults.forOrientation(spec, orientation)
            .applyHandedness(settings.touchControlsSwapHanded)
            .applySizeScale(settings.touchControlsSizeScale)
            .applyMirror180(settings.touchControlsMirror180, rotationKey)
    }
    val opacity = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        settings.touchControlsOpacityPortrait
    } else {
        settings.touchControlsOpacityLandscape
    }

    OnScreenControls(
        retroView = retroView,
        spec = spec,
        resolved = resolved,
        opacity = opacity,
        sizeScale = settings.touchControlsSizeScale,
        haptic = settings.touchControlsHaptic,
        modifier = modifier
    )
}
