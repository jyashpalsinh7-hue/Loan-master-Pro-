package com.loanmaster.pro

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(org.robolectric.RobolectricTestRunner::class)
class GstCalculatorViewModelTest {

    private lateinit var classUnderTest: GstCalculatorViewModel

    @Before
    fun setup() {
        classUnderTest = GstCalculatorViewModel()
    }

    @Test
    fun `adding GST computes correctly`() = runBlocking {
        classUnderTest.onEvent(GstCalculatorEvent.AmountChanged("1000"))
        classUnderTest.onEvent(GstCalculatorEvent.RateChanged(18.0))
        
        val state = classUnderTest.uiState.first { it.amountText == "1000" }
        assertEquals(1000.0, state.baseAmount, 0.01)
        assertEquals(1180.0, state.totalAmount, 0.01)
        assertEquals(180.0, state.totalGst, 0.01)
        assertEquals(90.0, state.cgst, 0.01)
        assertEquals(90.0, state.sgst, 0.01)
        assertEquals(0.0, state.igst, 0.01)
    }

    @Test
    fun `removing GST computes correctly`() = runBlocking {
        classUnderTest.onEvent(GstCalculatorEvent.ModeChanged(GstMode.REMOVE))
        classUnderTest.onEvent(GstCalculatorEvent.AmountChanged("1180"))
        classUnderTest.onEvent(GstCalculatorEvent.RateChanged(18.0))
        
        val state = classUnderTest.uiState.first { it.amountText == "1180" }
        assertEquals(1000.0, state.baseAmount, 0.01)
        assertEquals(1180.0, state.totalAmount, 0.01)
        assertEquals(180.0, state.totalGst, 0.01)
    }

    @Test
    fun `intrastate flag toggles IGST vs CGST-SGST`() = runBlocking {
        classUnderTest.onEvent(GstCalculatorEvent.AmountChanged("1000"))
        classUnderTest.onEvent(GstCalculatorEvent.RateChanged(18.0))
        classUnderTest.onEvent(GstCalculatorEvent.IntrastateToggled(false))
        
        val state = classUnderTest.uiState.first { it.amountText == "1000" && !it.isIntrastate }
        assertEquals(0.0, state.cgst, 0.01)
        assertEquals(0.0, state.sgst, 0.01)
        assertEquals(180.0, state.igst, 0.01)
    }

    @Test
    fun `cess rate applies correctly`() = runBlocking {
        classUnderTest.onEvent(GstCalculatorEvent.AmountChanged("1000"))
        classUnderTest.onEvent(GstCalculatorEvent.RateChanged(18.0))
        classUnderTest.onEvent(GstCalculatorEvent.CessRateChanged("2"))
        
        val state = classUnderTest.uiState.first { it.cessRateText == "2" }
        // 1000 * 18% = 180 GST, 1000 * 2% = 20 CESS. Total = 1200
        assertEquals(1200.0, state.totalAmount, 0.01)
        assertEquals(20.0, state.totalCess, 0.01)
    }

    @Test
    fun `history initialization sets state`() = runBlocking {
        val history = CalculationHistory(
            id = 5,
            calculatorType = "GST",
            title = "Test",
            param1 = "REMOVE",
            param2 = "2000",
            param3 = "12",
            timestamp = 10L
        )
        classUnderTest.onEvent(GstCalculatorEvent.InitializeFromHistory(history))
        
        val state = classUnderTest.uiState.first { it.amountText == "2000" }
        assertEquals(GstMode.REMOVE, state.mode)
        assertEquals(12.0, state.selectedRate, 0.01)
    }
}
