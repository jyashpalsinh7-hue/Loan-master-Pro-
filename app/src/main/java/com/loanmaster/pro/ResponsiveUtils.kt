package com.loanmaster.pro

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

object ResponsiveUtils {
    // Fintech Dark Theme Colors for easy access inside components
    val BgColor = Color(0xFF020B1F)
    val SurfaceColor = Color(0xFF061633)
    val PrimaryAccent = Color(0xFFFFC328)
    val SecondaryAccent = Color(0xFF2D7DFF)
    val CardStroke = Color(0xFF183C8A)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA8B3D1)
    
    val CardShape = RoundedCornerShape(16.dp)
    val ButtonShape = RoundedCornerShape(12.dp)

    // RESPONSIVE: Capping content width for tablet readability
    fun Modifier.optimalContentWidth(): Modifier {
        return this.widthIn(max = 840.dp)
    }
}
