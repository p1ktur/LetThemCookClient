package com.letthemcook.theme.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class Theme {
    LIGHT,
    DARK;

    operator fun not(): Theme {
        return when(this) {
            LIGHT -> DARK
            DARK -> LIGHT
        }
    }
}

class AppTheme(val theme: Theme) {

    val typography = getTypography(
        when (theme) {
            Theme.LIGHT -> textLight
            Theme.DARK -> textDark
        }
    )

    val screenZero: Color
        get() = when (theme) {
            Theme.LIGHT -> screenZeroLight
            Theme.DARK -> screenZeroDark
        }
    val screenOne: Color
        get() = when (theme) {
            Theme.LIGHT -> screenOneLight
            Theme.DARK -> screenOneDark
        }
    val screenOneDimmed: Color
        get() = when (theme) {
            Theme.LIGHT -> screenOneDimmedLight
            Theme.DARK -> screenOneDimmedDark
        }
    val screenTwo: Color
        get() = when (theme) {
            Theme.LIGHT -> screenTwoLight
            Theme.DARK -> screenTwoDark
        }
    val screenThree: Color
        get() = when (theme) {
            Theme.LIGHT -> screenThreeLight
            Theme.DARK -> screenThreeDark
        }
    val background: Color
        get() = when (theme) {
            Theme.LIGHT -> backgroundLight
            Theme.DARK -> backgroundDark
        }
    val divider: Color
        get() = when (theme) {
            Theme.LIGHT -> dividerLight
            Theme.DARK -> dividerDark
        }
    val text: Color
        get() = when (theme) {
            Theme.LIGHT -> textLight
            Theme.DARK -> textDark
        }
    val textInverse: Color
        get() = when (theme) {
            Theme.LIGHT -> textInverseLight
            Theme.DARK -> textInverseDark
        }
    val textDimmed: Color
        get() = when (theme) {
            Theme.LIGHT -> textDimmedLight
            Theme.DARK -> textDimmedDark
        }
    val textDimmedInverse: Color
        get() = when (theme) {
            Theme.LIGHT -> textDimmedInverseLight
            Theme.DARK -> textDimmedInverseDark
        }
    val container: Color
        get() = when (theme) {
            Theme.LIGHT -> containerLight
            Theme.DARK -> containerDark
        }
    val highlightColor: Color
        get() = when (theme) {
            Theme.LIGHT -> highlightColorLight
            Theme.DARK -> highlightColorDark
        }
    val badHighlightColor: Color
        get() = when (theme) {
            Theme.LIGHT -> badHighlightColorLight
            Theme.DARK -> badHighlightColorDark
        }
    val warningHighlightColor: Color
        get() = when (theme) {
            Theme.LIGHT -> warningHighlightColorLight
            Theme.DARK -> warningHighlightColorDark
        }
    val goodHighlightColor: Color
        get() = when (theme) {
            Theme.LIGHT -> goodHighlightColorLight
            Theme.DARK -> goodHighlightColorDark
        }
    val canvasBackground: Color
        get() = when (theme) {
            Theme.LIGHT -> canvasBackgroundLight
            Theme.DARK -> canvasBackgroundDark
        }
    val canvasGrid: Color
        get() = when (theme) {
            Theme.LIGHT -> canvasGridLight
            Theme.DARK -> canvasGridDark
        }
    val errorText: Color
        get() = when (theme) {
            Theme.LIGHT -> errorTextLight
            Theme.DARK -> errorTextDark
        }
}

val LocalAppTheme = compositionLocalOf { AppTheme(Theme.LIGHT) }

@Composable
fun LetThemCookTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val appTheme = AppTheme(theme)

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        content()
    }
}