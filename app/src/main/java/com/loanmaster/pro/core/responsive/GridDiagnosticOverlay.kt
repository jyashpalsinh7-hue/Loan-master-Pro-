package com.loanmaster.pro.core.responsive

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.feature.home.*


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
                .padding(top = LoanMasterTheme.components.calculatorCardHeight, start = spacing.screenPadding, end = spacing.screenPadding)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(LoanMasterTheme.spacing.md)
        ) {
            Text("Diagnostic Info", color = Color.White, style = LoanMasterTheme.typography.title)
            Text("Columns: ${grids.calculatorColumns}", color = Color.White)
            Text("Gutter: ${spacing.gridGutter}", color = Color.White)
            Text("Screen Padding: ${spacing.screenPadding}", color = Color.White)
        }
    }
}
