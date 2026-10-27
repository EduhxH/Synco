package com.kult.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

/**
 * The prototype uses Inter. Until the font file is added to res/font, the system sans-serif is used;
 * switching it here updates every text in the app.
 */
val SyncoFontFamily: FontFamily = FontFamily.Default

/**
 * Text style from the prototype's CSS values: [size] and [lineHeight] in px (as sp), [letterSpacing] in px.
 * When [lineHeight] is omitted it is 1.5 × size, the prototype's default.
 */
fun syncoText(
    size: Float,
    weight: Int = 400,
    lineHeight: Float = size * 1.5f,
    letterSpacing: Float = 0f,
): TextStyle = TextStyle(
    fontFamily = SyncoFontFamily,
    fontSize = size.sp,
    fontWeight = FontWeight(weight),
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)
