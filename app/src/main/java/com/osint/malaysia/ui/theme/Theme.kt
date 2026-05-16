/* 马来西亚OSINT — 警蓝主题系统（深海军蓝配色） */

package com.osint.malaysia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/* ===== 警蓝（海军蓝）色板 ===== */
object NavyBlue {
    val N950 = Color(0xFF0A1628)
    val N900 = Color(0xFF0D1F3C)
    val N800 = Color(0xFF122A52)
    val N700 = Color(0xFF1A3A6E)
    val N600 = Color(0xFF244B8A)
    val N500 = Color(0xFF2E5CA6)
    val N400 = Color(0xFF3D74C9)
    val N300 = Color(0xFF6B95DD)
    val N200 = Color(0xFF9AB8EE)
    val N100 = Color(0xFFC5D6F5)
    val N50  = Color(0xFFE8EEF9)
}

/* 语义色 */
object Accent {
    val Red    = Color(0xFFE53935)
    val Green  = Color(0xFF43A047)
    val Orange = Color(0xFFFF9800)
    val Gold   = Color(0xFFFFD700)
    val Cyan   = Color(0xFF00BCD4)
}

/* ===== Material3 色彩方案 ===== */
private val DarkColorScheme = darkColorScheme(
    primary = NavyBlue.N400,
    onPrimary = Color.White,
    primaryContainer = NavyBlue.N700,
    onPrimaryContainer = NavyBlue.N100,
    secondary = NavyBlue.N300,
    onSecondary = NavyBlue.N950,
    secondaryContainer = NavyBlue.N800,
    onSecondaryContainer = NavyBlue.N100,
    tertiary = Accent.Cyan,
    onTertiary = NavyBlue.N950,
    background = NavyBlue.N950,
    onBackground = NavyBlue.N50,
    surface = NavyBlue.N900,
    onSurface = NavyBlue.N50,
    surfaceVariant = NavyBlue.N800,
    onSurfaceVariant = NavyBlue.N200,
    error = Accent.Red,
    onError = Color.White,
    outline = NavyBlue.N600
)

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue.N600,
    onPrimary = Color.White,
    primaryContainer = NavyBlue.N200,
    onPrimaryContainer = NavyBlue.N900,
    secondary = NavyBlue.N500,
    onSecondary = Color.White,
    secondaryContainer = NavyBlue.N100,
    onSecondaryContainer = NavyBlue.N900,
    tertiary = Accent.Cyan,
    onTertiary = Color.White,
    background = Color(0xFFF5F7FA),
    onBackground = NavyBlue.N900,
    surface = Color.White,
    onSurface = NavyBlue.N900,
    surfaceVariant = NavyBlue.N50,
    onSurfaceVariant = NavyBlue.N700,
    error = Accent.Red,
    onError = Color.White,
    outline = NavyBlue.N300
)

/* ===== 排版系统 ===== */
object AppTypography {
    val Display = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    val Title = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    val Subtitle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    val Body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    val Caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    val Mono = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    )
}

/* ===== 主主题 ===== */
@Composable
fun MalaysianOSINTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            displayLarge = AppTypography.Display,
            headlineMedium = AppTypography.Title,
            titleMedium = AppTypography.Subtitle,
            bodyLarge = AppTypography.Body,
            bodyMedium = AppTypography.Caption,
        ),
        content = content
    )
}
