/* MalaysianOSINT — Clean Theme (no purple) */
package com.osint.malaysia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

object Colors {
    val Primary = Color(0xFFE85D75)
    val PrimaryL = Color(0xFFFFE0E6)
    val PrimaryD = Color(0xFFC94A60)
    val Secondary = Color(0xFFE85D75)
    val SecondaryL = Color(0xFFFFE0E6)
    val Surface = Color(0xFFFFF5F6)
    val Slate = Color(0xFF4A5568)
    val SlateL = Color(0xFFA0AEC0)
    val Red = Color(0xFFE53E3E)
    val Green = Color(0xFFE85D75)
    val Orange = Color(0xFFDD6B20)
}

private val LightScheme = lightColorScheme(
    primary = Colors.Primary, onPrimary = Color.White,
    primaryContainer = Colors.PrimaryL, onPrimaryContainer = Colors.PrimaryD,
    secondary = Colors.Secondary, onSecondary = Color.White,
    secondaryContainer = Colors.SecondaryL, onSecondaryContainer = Colors.Secondary,
    tertiary = Colors.Secondary, onTertiary = Color.White,
    background = Color(0xFFFAFAFC), onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFFFF0F2), onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFFFE8EC), onSurfaceVariant = Color(0xFF6B5E68),
    surfaceContainerLow = Color(0xFFFFF8F9), surfaceContainer = Color(0xFFFFF0F2),
    surfaceContainerHigh = Color(0xFFFFE4E9),
    error = Colors.Red, onError = Color.White,
    errorContainer = Color(0xFFFFE5E5), onErrorContainer = Colors.Red,
    outline = Color(0xFFDDD5DA), outlineVariant = Color(0xFFEBE3E8),
)

private val DarkScheme = darkColorScheme(
    primary = Colors.Primary, onPrimary = Color(0xFF1A0A0E),
    primaryContainer = Colors.PrimaryD.copy(alpha = 0.3f), onPrimaryContainer = Colors.PrimaryL,
    secondary = Colors.Secondary, onSecondary = Color(0xFF0A1A14),
    secondaryContainer = Colors.Secondary.copy(alpha = 0.3f), onSecondaryContainer = Colors.SecondaryL,
    tertiary = Colors.Secondary, onTertiary = Color(0xFF0A1A14),
    background = Color(0xFF121018), onBackground = Color(0xFFF0ECF0),
    surface = Color(0xFF1C1A22), onSurface = Color(0xFFF0ECF0),
    surfaceVariant = Color(0xFF282430), onSurfaceVariant = Color(0xFFC8BED0),
    surfaceContainerLow = Color(0xFF0E0C14), surfaceContainer = Color(0xFF1C1A22),
    surfaceContainerHigh = Color(0xFF24202C),
    error = Color(0xFFFF6B6B), onError = Color(0xFF1A0A0A),
    errorContainer = Color(0xFFFF6B6B).copy(alpha = 0.2f), onErrorContainer = Color(0xFFFFCCCC),
    outline = Color(0xFF3A2E40), outlineVariant = Color(0xFF282034),
)

object AppTypography {
    val Display = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp)
    val Title = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp)
    val Subtitle = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 22.sp)
    val Body = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp)
    val Caption = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp)
    val Mono = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp)
}

object AppShapes { val S = RoundedCornerShape(12.dp); val M = RoundedCornerShape(16.dp); val L = RoundedCornerShape(20.dp) }

@Composable
fun MalaysianOSINTTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val cs = if (dark) DarkScheme else LightScheme
    val v = LocalView.current
    if (!v.isInEditMode) SideEffect {
        val w = (v.context as Activity).window
        w.statusBarColor = android.graphics.Color.TRANSPARENT
        w.navigationBarColor = cs.surface.toArgb()
        WindowCompat.getInsetsController(w, v).apply { isAppearanceLightStatusBars = !dark; isAppearanceLightNavigationBars = !dark }
    }
    MaterialTheme(colorScheme = cs, typography = Typography(displayMedium = AppTypography.Display, headlineMedium = AppTypography.Title, titleMedium = AppTypography.Subtitle, bodyLarge = AppTypography.Body, bodyMedium = AppTypography.Caption), content = content)
}
