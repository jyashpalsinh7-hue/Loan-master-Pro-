package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.SipCalculator
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.pow

class SipCalculatorViewModelTest {

    private val calculator = SipCalculator()
    private val tolerance = 0.1

    @Test
    fun `calculate returns correct maturity for 5000 per month at 12 percent for 1 year no step-up`() {
        val result = calculator.calculate(amount = "5000", rate = "12", years = "1", stepUp = "0")
        assertTrue(result.isValid)
        assertTrue("Expected totalInvested 60000.00 but was ${result.totalInvested}", abs(result.totalInvested - 60000.00) < tolerance)
        assertTrue("Expected maturityValue near 64046.64 but was ${result.maturityValue}", abs(result.maturityValue - 64046.64) < tolerance)
        assertTrue("Expected totalGain near 4046.64 but was ${result.totalGain}", abs(result.totalGain - 4046.64) < tolerance)

        // Independent cross-check using the standard closed-form future-value-of-annuity-due
        // formula: FV = P * [((1+i)^n - 1) / i] * (1+i). This is a genuinely different
        // calculation method from the loop, not the same formula run twice.
        val i = 0.12 / 12
        val n = 12
        val manualFv = 5000.0 * (((1 + i).pow(n) - 1) / i) * (1 + i)
        assertTrue("Manual annuity-due formula should match loop-based calculator output", abs(result.maturityValue - manualFv) < tolerance)
    }

    @Test
    fun `calculate correctly applies annual step-up for 3000 per month at 10 percent for 2 years with 10 percent step-up`() {
        val result = calculator.calculate(amount = "3000", rate = "10", years = "2", stepUp = "10")
        assertTrue(result.isValid)
        assertTrue("Expected totalInvested 75600.00 but was ${result.totalInvested}", abs(result.totalInvested - 75600.00) < tolerance)
        assertTrue("Expected maturityValue near 83803.00 but was ${result.maturityValue}", abs(result.maturityValue - 83803.00) < tolerance)
        assertEquals("Expected exactly 2 years of yearly data", 2, result.yearlyDataList.size)
        assertTrue("Year 1 investedForYear should be 36000.00", abs(result.yearlyDataList[0].investedForYear - 36000.00) < tolerance)
        assertTrue("Year 2 investedForYear should be 39600.00 (stepped up 10 percent)", abs(result.yearlyDataList[1].investedForYear - 39600.00) < tolerance)
    }

    @Test
    fun `calculate returns correct inflationAdjustedValue using 6 percent inflation rate`() {
        val result = calculator.calculate(amount = "5000", rate = "12", years = "1", stepUp = "0")
        assertTrue(result.isValid)
        // Independent cross-check: inflationAdjustedValue = maturityValue / (1.06)^years
        val manualInflationAdjusted = result.maturityValue / (1.06).pow(1)
        assertTrue(
            "inflationAdjustedValue should equal maturityValue / 1.06^years",
            abs(result.inflationAdjustedValue - manualInflationAdjusted) < tolerance
        )
        assertTrue("Expected inflationAdjustedValue near 60421.36 but was ${result.inflationAdjustedValue}", abs(result.inflationAdjustedValue - 60421.36) < tolerance)
    }

    @Test
    fun `calculate returns isValid false for zero amount`() {
        val result = calculator.calculate(amount = "0", rate = "12", years = "1", stepUp = "0")
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero rate`() {
        val result = calculator.calculate(amount = "5000", rate = "0", years = "1", stepUp = "0")
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero years`() {
        val result = calculator.calculate(amount = "5000", rate = "12", years = "0", stepUp = "0")
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for empty amount string`() {
        val result = calculator.calculate(amount = "", rate = "12", years = "1", stepUp = "0")
        assertFalse(result.isValid)
    }

    @Test
    fun `totalGain equals maturityValue minus totalInvested`() {
        val result = calculator.calculate(amount = "5000", rate = "12", years = "1", stepUp = "0")
        assertTrue(result.isValid)
        assertTrue(
            "totalGain should equal maturityValue - totalInvested",
            abs(result.totalGain - (result.maturityValue - result.totalInvested)) < tolerance
        )
    }

    @Test
    fun `yearlyDataList size matches number of years requested`() {
        val result = calculator.calculate(amount = "5000", rate = "12", years = "3", stepUp = "0")
        assertTrue(result.isValid)
        assertEquals(3, result.yearlyDataList.size)
    }
}
