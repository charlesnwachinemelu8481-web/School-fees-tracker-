package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FeeTrackColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    primaryContainer = DeepTealContainer,
    onPrimaryContainer = OnDeepTealContainer,
    secondary = TealAccent,
    onSecondary = Color.White,
    secondaryContainer = DeepTealContainer,
    onSecondaryContainer = OnDeepTealContainer,
    tertiary = WarningAmber,
    onTertiary = Color.White,
    tertiaryContainer = WarningAmberContainer,
    onTertiaryContainer = OnWarningAmber,
    error = WarningRed,
    onError = Color.White,
    errorContainer = WarningRedContainer,
    onErrorContainer = OnWarningRed,
    background = BackgroundSlate,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardSubtle,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate,
    outlineVariant = BorderSlate
)

@Composable
fun FeeTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FeeTrackColorScheme,
        typography = Typography,
        content = content
    )
}
