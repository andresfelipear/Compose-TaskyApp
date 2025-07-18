package com.aarevalo.tasky.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val default: Dp = 0.dp,
    val spaceExtraSmall: Dp = 6.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceExtraMedium: Dp = 20.dp,
    val spaceLarge: Dp = 28.dp,
    val spaceExtraLarge: Dp = 36.dp,
)

val LocalSpacing = compositionLocalOf { Dimensions() }