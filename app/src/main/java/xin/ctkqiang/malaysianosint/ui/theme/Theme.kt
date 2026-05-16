package xin.ctkqiang.malaysianosint.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkScheme = darkColorScheme(
    primary = PoliceBlue,
    onPrimary = TextPrimary,
    primaryContainer = PoliceBlueDark,
    onPrimaryContainer = TextPrimary,
    secondary = Gold,
    onSecondary = BackgroundDark,
    secondaryContainer = PoliceBlueSurface,
    onSecondaryContainer = GoldLight,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.15f),
    onErrorContainer = Error,
)

private val LightScheme = lightColorScheme(
    primary = PoliceBlue,
    onPrimary = TextPrimary,
    primaryContainer = PoliceBlueLight.copy(alpha = 0.15f),
    onPrimaryContainer = PoliceBlueDark,
    secondary = Gold,
    onSecondary = BackgroundDark,
    background = LightBg,
    onBackground = LightOnBg,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightBg,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
)

enum class ThemeMode { AUTO, LIGHT, DARK }

@Composable
fun MalaysianOSINTTheme(
    themeMode: ThemeMode = ThemeMode.AUTO,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.AUTO -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) darkColorScheme() else lightColorScheme()
        }
        else -> if (darkTheme) DarkScheme else LightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
