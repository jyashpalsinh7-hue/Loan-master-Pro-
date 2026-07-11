
package com.loanmaster.pro.feature.loaneligibility

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.core.responsive.AdaptiveRowCol
import com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper
import com.loanmaster.pro.feature.loaneligibility.components.*
import com.loanmaster.pro.feature.loaneligibility.util.*
import com.loanmaster.pro.domain.calculator.LoanEligibilityCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanEligibilityScreen(onNavigateBack: () -> Unit = {}, viewModel: LoanEligibilityViewModel = viewModel()) {
    val intelligenceViewModel: com.loanmaster.pro.feature.loanintelligence.LoanIntelligenceViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val intelligenceState by intelligenceViewModel.state.collectAsStateWithLifecycle()
    val bgColor = BackgroundDark
    val textColor = TextPrimary

    val monthlyIncome = uiState.monthlyIncomeText
    val existingEMIs = uiState.existingEMIsText
    val isCoBorrowerEnabled = uiState.isCoBorrowerEnabled
    val coBorrowerIncome = uiState.coBorrowerIncomeText
    val coBorrowerEMIs = uiState.coBorrowerEMIsText
    val selectedLoanProfile = uiState.selectedLoanProfile
    val tenureYears = uiState.tenureYearsText
    val interestRate = uiState.interestRateText
    val isSalaried = uiState.isSalaried
    val creditScoreRange = uiState.creditScoreRange
    
    val totalIncome = uiState.totalIncome
    val totalExistingEmi = uiState.totalExistingEmi
    val foirLimit = uiState.foirLimit
    val maxAllowedEmi = uiState.maxAllowedEmi
    val availableEmi = uiState.availableEmi
    val eligibleLoanAmount = uiState.eligibleLoanAmount
    val currentFoir = uiState.currentFoir

    var isResultVisible by rememberSaveable { mutableStateOf(false) }
    var isInputExpanded by rememberSaveable { mutableStateOf(true) }
    var isCalculating by rememberSaveable { mutableStateOf(false) }
    var showEmptyError by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        ResponsiveScreenWrapper(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues),
            showDiagnostics = false
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .imePadding()
                    .animateContentSize(animationSpec = tween(500, easing = FastOutSlowInEasing)),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                item {
                    Text(
                        text = "Loan Eligibility",
                        color = textColor,
                        style = LoanMasterTheme.typography.title,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Collapsed Summary
                item {
                    SummaryCard(
                        isInputExpanded = isInputExpanded,
                        isResultVisible = isResultVisible,
                        selectedLoanProfile = selectedLoanProfile,
                        tenureYears = tenureYears,
                        interestRate = interestRate,
                        totalIncome = totalIncome,
                        totalExistingEmi = totalExistingEmi,
                        onEditClick = { isInputExpanded = true }
                    )
                }

                // Inputs
                item {
                    InputSection(
                        isInputExpanded = isInputExpanded,
                        isResultVisible = isResultVisible,
                        monthlyIncome = monthlyIncome,
                        onMonthlyIncomeChange = { viewModel.updateInputs(income = it) },
                        existingEMIs = existingEMIs,
                        onExistingEMIsChange = { viewModel.updateInputs(emi = it) },
                        isCoBorrowerEnabled = isCoBorrowerEnabled,
                        onCoBorrowerChange = { viewModel.updateInputs(isCoBorrowerEnabled = it) },
                        coBorrowerIncome = coBorrowerIncome,
                        onCoBorrowerIncomeChange = { viewModel.updateInputs(coIncome = it) },
                        coBorrowerEMIs = coBorrowerEMIs,
                        onCoBorrowerEMIsChange = { viewModel.updateInputs(coEmi = it) },
                        selectedLoanProfile = selectedLoanProfile,
                        onSelectedLoanProfileChange = { 
                            // Get defaults from LoanProfiles
                            val profile = loanProfiles.find { p -> p.name == it } ?: loanProfiles[0]
                            viewModel.updateInputs(profile = profile.name, defaultTenure = profile.defaultTenure, defaultRate = profile.defaultRate) 
                        },
                        tenureYears = tenureYears,
                        onTenureYearsChange = { viewModel.updateInputs(tenure = it) },
                        interestRate = interestRate,
                        onInterestRateChange = { viewModel.updateInputs(rate = it) },
                        isSalaried = isSalaried,
                        onSalariedChange = { viewModel.updateInputs(isSalaried = it) },
                        creditScoreRange = creditScoreRange,
                        onCreditScoreRangeChange = { viewModel.updateInputs(creditScoreRange = it) },
                        showEmptyError = showEmptyError
                    )
                }

                // Action Button (Calculate)
                item {
                    CalculateButton(
                        isInputExpanded = isInputExpanded,
                        isCalculating = isCalculating,
                        onCalculateClick = {
                            if (monthlyIncome.isBlank() && existingEMIs.isBlank()) {
                                showEmptyError = true
                            } else {
                                showEmptyError = false
                                                                if (isInputExpanded) {
                                    coroutineScope.launch {
                                        isCalculating = true
                                        isResultVisible = false
                                        isInputExpanded = false
                                        
                                        intelligenceViewModel.resetTemporaryUnlock()
                                        intelligenceViewModel.generateSuggestions(
                                            income = uiState.totalIncome,
                                            existingEmi = uiState.totalExistingEmi,
                                            loanType = uiState.selectedLoanProfile,
                                            interestRate = uiState.interestRateText.toDoubleOrNull() ?: 10.0,
                                            tenureYears = uiState.tenureYearsText.toIntOrNull() ?: 10,
                                            creditScoreRange = uiState.creditScoreRange,
                                            approvalProb = (1.0f - (uiState.currentFoir.toFloat() / (uiState.foirLimit.toFloat().coerceAtLeast(0.01f)))).coerceIn(0f, 1f),
                                            eligibleAmount = uiState.eligibleLoanAmount,
                                            foirLimit = uiState.foirLimit
                                        )
                                        
                                        delay(1200)
                                        isCalculating = false
                                        isResultVisible = true
                                    }
                                } else {
                                    isInputExpanded = true
                                }
                            }
                        }
                    )
                }

                // Skeleton Loader
                item {
                    LoadingState(isCalculating = isCalculating)
                }

                // Empty State
                item {
                    EmptyState(
                        isResultVisible = isResultVisible,
                        isCalculating = isCalculating,
                        isInputExpanded = isInputExpanded
                    )
                }

                // Results
                item {
                    ResultsSection(
                        isResultVisible = isResultVisible,
                        isInputExpanded = isInputExpanded,
                        isCalculating = isCalculating,
                        currentFoir = currentFoir,
                        foirLimit = foirLimit,
                        uiStateVerdictGrade = uiState.verdictGrade,
                        eligibleLoanAmount = eligibleLoanAmount,
                        availableEmi = availableEmi,
                        maxAllowedEmi = maxAllowedEmi,
                        totalIncome = totalIncome,
                        totalExistingEmi = totalExistingEmi,
                        tenureYears = tenureYears,
                        creditScoreRange = creditScoreRange,
                        onModifyInputsClick = { isInputExpanded = true },
                        intelligenceState = intelligenceState,
                        onWatchAdClick = { intelligenceViewModel.unlockTemporary() },
                        onPremiumClick = { intelligenceViewModel.unlockPremium() }
                    )
                }
            }
        }
    }
}
