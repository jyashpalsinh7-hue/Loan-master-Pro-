package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GridDiagnosticOverlay(modifier: Modifier = Modifier) {
    val spacing = LoanMasterTheme.spacing
    val grids = LoanMasterTheme.grids
    
    Box(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing.gridGutter)
        ) {
            repeat(grids.calculatorColumns) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Magenta.copy(alpha = 0.15f))
                )
            }
        }
        
        Column(
            modifier = Modifier
                .padding(top = 100.dp, start = spacing.screenPadding, end = spacing.screenPadding)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp)
        ) {
            Text("Diagnostic Info", color = Color.White, style = LoanMasterTheme.typography.title)
            Text("Columns: ${grids.calculatorColumns}", color = Color.White)
            Text("Gutter: ${spacing.gridGutter}", color = Color.White)
            Text("Screen Padding: ${spacing.screenPadding}", color = Color.White)
        }
    }
}
