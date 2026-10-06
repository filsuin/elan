package fr.elan.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import fr.elan.app.domain.ThemeMode
import fr.elan.app.domain.UserSettings

private val Light = lightColorScheme(
    primary = Color(0xFF42634E), onPrimary = Color.White,
    primaryContainer = Color(0xFFC4EACD), onPrimaryContainer = Color(0xFF102E1B),
    secondary = Color(0xFF526353), onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5E8D3), onSecondaryContainer = Color(0xFF102014),
    tertiary = Color(0xFF82533C), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCA), onTertiaryContainer = Color(0xFF321304),
    background = Color(0xFFF7FAF2), onBackground = Color(0xFF191D18),
    surface = Color(0xFFF7FAF2), onSurface = Color(0xFF191D18),
    surfaceVariant = Color(0xFFDFE5DB), onSurfaceVariant = Color(0xFF424940),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF1F5ED),
    surfaceContainer = Color(0xFFEBEFE7), surfaceContainerHigh = Color(0xFFE5E9E1),
    surfaceContainerHighest = Color(0xFFDFE3DB),
    outline = Color(0xFF73796F), outlineVariant = Color(0xFFC2C9BC),
)
private val Dark = darkColorScheme(
    primary = Color(0xFFA7D0AF), onPrimary = Color(0xFF133722),
    primaryContainer = Color(0xFF2A4D37), onPrimaryContainer = Color(0xFFC4EACD),
    secondary = Color(0xFFB9CCB7), onSecondary = Color(0xFF243426),
    secondaryContainer = Color(0xFF3B4B3C), onSecondaryContainer = Color(0xFFD5E8D3),
    tertiary = Color(0xFFF6B997), onTertiary = Color(0xFF4C2713),
    tertiaryContainer = Color(0xFF663C27), onTertiaryContainer = Color(0xFFFFDBCA),
    background = Color(0xFF101510), onBackground = Color(0xFFDFE4DB),
    surface = Color(0xFF101510), onSurface = Color(0xFFDFE4DB),
    surfaceVariant = Color(0xFF424940), onSurfaceVariant = Color(0xFFC2C9BC),
    surfaceContainerLowest = Color(0xFF0B100B), surfaceContainerLow = Color(0xFF191D18),
    surfaceContainer = Color(0xFF1D211C), surfaceContainerHigh = Color(0xFF282C26),
    surfaceContainerHighest = Color(0xFF323730),
    outline = Color(0xFF8C9387), outlineVariant = Color(0xFF424940),
)

@Composable
fun ElanTheme(settings: UserSettings = UserSettings(), content: @Composable () -> Unit) {
    val dark = when (settings.theme) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colors = when {
        settings.dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> Dark
        else -> Light
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 38.sp),
            headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
            titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
            bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 25.sp),
            bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
        ),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp), large = RoundedCornerShape(24.dp), extraLarge = RoundedCornerShape(32.dp),
        ),
        content = content,
    )
}
