package com.loanmaster.pro.feature.loaneligibility.util


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.core.formatter.formatMoney
import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.feature.loaneligibility.components.*
import com.loanmaster.pro.domain.model.LoanProfile


val loanProfiles = listOf(
    LoanProfile("Home Loan", 0.60, "8.5", "20"),
    LoanProfile("Personal Loan", 0.50, "12.0", "5"),
    LoanProfile("Car Loan", 0.55, "9.5", "7"),
    LoanProfile("Education Loan", 0.40, "10.5", "10")
)

