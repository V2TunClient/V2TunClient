package com.aras.client.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import com.aras.client.R
import androidx.compose.material3.Typography
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.aras.client.AppConfig
import com.aras.client.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFF7E22CE),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF0D7FF),
    onPrimaryContainer = Color(0xFF2A0A45),
    secondary = Color(0xFFC026D3),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF5D0FE),
    onSecondaryContainer = Color(0xFF4A044E),
    tertiary = Color(0xFF9333EA),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE9D5FF),
    onTertiaryContainer = Color(0xFF3B0764),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFBF7FF),
    onBackground = Color(0xFF211A24),
    surface = Color(0xFFFFFAFF),
    onSurface = Color(0xFF211A24),
    surfaceVariant = Color(0xFFEDE3F2),
    onSurfaceVariant = Color(0xFF514457),
    outline = Color(0xFF806F86),
    outlineVariant = Color(0xFFD8C9DC),
    inverseSurface = Color(0xFF362F38),
    inverseOnSurface = Color(0xFFF9EDF9),
    inversePrimary = Color(0xFFD0BCFF),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF7C3AED),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF9F3FB),
    surfaceContainer = Color(0xFFF3ECF5),
    surfaceContainerHigh = Color(0xFFEDE5EF),
    surfaceContainerHighest = Color(0xFFE7DEE9),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFE879F9),
    onPrimary = Color(0xFF33004A),
    primaryContainer = Color(0xFF6B21A8),
    onPrimaryContainer = Color(0xFFF7D9FF),
    secondary = Color(0xFFF0ABFC),
    onSecondary = Color(0xFF4A044E),
    secondaryContainer = Color(0xFF86198F),
    onSecondaryContainer = Color(0xFFF5D0FE),
    tertiary = Color(0xFFC084FC),
    onTertiary = Color(0xFF3B0764),
    tertiaryContainer = Color(0xFF581C87),
    onTertiaryContainer = Color(0xFFE9D5FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0B0610),
    onBackground = Color(0xFFF7EAF8),
    surface = Color(0xFF120A18),
    onSurface = Color(0xFFF7EAF8),
    surfaceVariant = Color(0xFF2B1935),
    onSurfaceVariant = Color(0xFFD9C5DE),
    outline = Color(0xFFA58CAA),
    outlineVariant = Color(0xFF4B2E57),
    inverseSurface = Color(0xFFEFE4F0),
    inverseOnSurface = Color(0xFF362F38),
    inversePrimary = Color(0xFF7E22CE),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFE879F9),
    surfaceContainerLowest = Color(0xFF08040C),
    surfaceContainerLow = Color(0xFF160C1E),
    surfaceContainer = Color(0xFF1D1026),
    surfaceContainerHigh = Color(0xFF25132F),
    surfaceContainerHighest = Color(0xFF321A3D),
)

// Semantic Colors
val colorPing = Color(0xFF34A853) // Green
val colorPingRed = Color(0xFFE1554F) // Soft Red
val colorConfigType = Color(0xFFA855F7) // Violet (protocol badges)
val colorFabActive = Color(0xFF9333EA) // Violet
val colorFabInactiveLight = Color(0xFFB9AC9C) // Warm Gray
val colorFabInactiveDark = Color(0xFF4A4A52) // Dark Gray
val dividerColorLight = Color(0xFFD8CFDA) // Light Lavender
val dividerColorDark = Color(0xFF493F4C) // Dark Lavender
val colorSelectedGlow = Color(0xFFC026D3) // Violet glow for selected card

// Toast Colors 70%
val toastNormalBgLight = Color(0xB3353A3E) // Dark Gray
val toastNormalBgDark = Color(0xB34A4F54) // Darker Gray
val toastSuccessBg = Color(0xB3388E3C) // Green
val toastErrorBg = Color(0xB3D50000) // Red
val toastInfoBg = Color(0xB33F51B5) // Indigo Blue
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    }
}

private val AppFontFamily = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
)

private val defaultTypography = Typography()

val AppTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFontFamily),
)

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
