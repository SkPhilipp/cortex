package com.hileco.cortex.console.graphics

import com.googlecode.lanterna.TextColor

data class ColorScheme(
        val foreground: TextColor,
        val background: TextColor,
        val foregroundHighlight: TextColor,
        val backgroundHighlight: TextColor
)
