package me.rerere.rikkahub.ui.theme.presets

import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.theme.PresetTheme

/**
 * miuix（HyperOS）风格：浅灰页底 + 纯白卡片 + 蓝色主色，中性灰层级替代阴影。
 */
val MiuixThemePreset by lazy {
    PresetTheme(
        id = "miuix",
        name = {
            Text(stringResource(id = R.string.theme_name_miuix))
        },
        standardLight = lightScheme,
        standardDark = darkScheme,
    )
}

private val primaryLight = Color(0xFF3482FF)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFE8F0FF)
private val onPrimaryContainerLight = Color(0xFF1B4FCC)
private val secondaryLight = Color(0xFF5C5F66)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFF0F1F3)
private val onSecondaryContainerLight = Color(0xFF3B3E44)
private val tertiaryLight = Color(0xFF0E9F6E)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFE3F7EF)
private val onTertiaryContainerLight = Color(0xFF075E42)
private val errorLight = Color(0xFFE5443B)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFDE8E7)
private val onErrorContainerLight = Color(0xFFA31710)
private val backgroundLight = Color(0xFFF5F5F6)
private val onBackgroundLight = Color(0xFF16171A)
private val surfaceLight = Color(0xFFFFFFFF)
private val onSurfaceLight = Color(0xFF16171A)
private val surfaceVariantLight = Color(0xFFF0F0F2)
private val onSurfaceVariantLight = Color(0xFF6C7079)
private val outlineLight = Color(0xFFB7BAC0)
private val outlineVariantLight = Color(0xFFEBEBED)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2A2C31)
private val inverseOnSurfaceLight = Color(0xFFF3F4F6)
private val inversePrimaryLight = Color(0xFFAAC7FF)
private val surfaceDimLight = Color(0xFFEDEDEF)
private val surfaceBrightLight = Color(0xFFFFFFFF)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFFFFFFF)
private val surfaceContainerLight = Color(0xFFF8F8FA)
private val surfaceContainerHighLight = Color(0xFFF0F0F2)
private val surfaceContainerHighestLight = Color(0xFFE8E8EB)

private val primaryDark = Color(0xFF5B9BFF)
private val onPrimaryDark = Color(0xFF00276B)
private val primaryContainerDark = Color(0xFF12315F)
private val onPrimaryContainerDark = Color(0xFFD6E3FF)
private val secondaryDark = Color(0xFFC4C6CC)
private val onSecondaryDark = Color(0xFF2C2F34)
private val secondaryContainerDark = Color(0xFF383B41)
private val onSecondaryContainerDark = Color(0xFFE2E4E8)
private val tertiaryDark = Color(0xFF56D6A5)
private val onTertiaryDark = Color(0xFF00382A)
private val tertiaryContainerDark = Color(0xFF005141)
private val onTertiaryContainerDark = Color(0xFF8DF5CB)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF000000)
private val onBackgroundDark = Color(0xFFE8E9EB)
private val surfaceDark = Color(0xFF000000)
private val onSurfaceDark = Color(0xFFE8E9EB)
private val surfaceVariantDark = Color(0xFF232529)
private val onSurfaceVariantDark = Color(0xFF9EA2AA)
private val outlineDark = Color(0xFF4A4D53)
private val outlineVariantDark = Color(0xFF26282C)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE8E9EB)
private val inverseOnSurfaceDark = Color(0xFF2A2C31)
private val inversePrimaryDark = Color(0xFF3482FF)
private val surfaceDimDark = Color(0xFF000000)
private val surfaceBrightDark = Color(0xFF2E3035)
private val surfaceContainerLowestDark = Color(0xFF000000)
private val surfaceContainerLowDark = Color(0xFF121316)
private val surfaceContainerDark = Color(0xFF1A1A1D)
private val surfaceContainerHighDark = Color(0xFF212226)
private val surfaceContainerHighestDark = Color(0xFF2A2B30)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
