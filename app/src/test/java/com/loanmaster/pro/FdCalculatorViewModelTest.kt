package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.FdCalculator
import com.loanmaster.pro.domain.calculator.CompoundingFrequency
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs

class FdCalculatorViewModelTest {

    private val calculator = FdCalculator()
    private val tolerance = 0.01

    @Test
    fun `calculate returns correct maturity for 1 lakh at 7 percent quarterly for 1 year`() {
        val result = calculator.calculate(
            deposit = "100000", rate = "7", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 107185.90 but was ${result.maturityValue}", abs(result.maturityValue - 107185.90) < tolerance)
    }

    @Test
    fun `calculate returns correct maturity for 5 lakh at 8 percent yearly for 3 years`() {
        // Yearly compounding (n=1) simplifies to maturity = P * (1+r)^t exactly —
        // used here as a cross-checkable simple case.
        val result = calculator.calculate(
            deposit = "500000", rate = "8", tenureYears = "3",
            frequency = CompoundingFrequency.YEARLY
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 629856.00 but was ${result.maturityValue}", abs(result.maturityValue - 629856.00) < tolerance)
        // Independent cross-check of the same case using the simplified yearly formula
        val manualCheck = 500000.0 * Math.pow(1.08, 3.0)
        assertTrue("Manual cross-check should match calculator output", abs(result.maturityValue - manualCheck) < tolerance)
    }

    @Test
    fun `calculate returns correct maturity for 2 lakh at 6 percent monthly for 2 years`() {
        val result = calculator.calculate(
            deposit = "200000", rate = "6", tenureYears = "2",
            frequency = CompoundingFrequency.MONTHLY
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 225431.96 but was ${result.maturityValue}", abs(result.maturityValue - 225431.96) < tolerance)
    }

    @Test
    fun `calculate returns correct maturity for 10 lakh at 7_5 percent half-yearly for 5 years`() {
        val result = calculator.calculate(
            deposit = "1000000", rate = "7.5", tenureYears = "5",
            frequency = CompoundingFrequency.HALF_YEARLY
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 1445043.94 but was ${result.maturityValue}", abs(result.maturityValue - 1445043.94) < tolerance)
    }

    @Test
    fun `calculate returns isValid false for zero deposit`() {
        val result = calculator.calculate(
            deposit = "0", rate = "7", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero rate`() {
        val result = calculator.calculate(
            deposit = "100000", rate = "0", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero tenure`() {
        val result = calculator.calculate(
            deposit = "100000", rate = "7", tenureYears = "0",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for empty deposit string`() {
        val result = calculator.calculate(
            deposit = "", rate = "7", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate totalInterest equals maturityValue minus totalInvested`() {
        val result = calculator.calculate(
            deposit = "100000", rate = "7", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertTrue(result.isValid)
        assertTrue(
            "totalInterest should equal maturityValue - totalInvested",
            abs(result.totalInterest - (result.maturityValue - result.totalInvested)) < tolerance
        )
    }

    @Test
    fun `calculate wealthGain matches totalInterest divided by totalInvested times 100`() {
        val result = calculator.calculate(
            deposit = "100000", rate = "7", tenureYears = "1",
            frequency = CompoundingFrequency.QUARTERLY
        )
        assertTrue(result.isValid)
        val expectedWealthGain = (result.totalInterest / result.totalInvested) * 100
        assertTrue(
            "wealthGain should match totalInterest/totalInvested*100",
            abs(result.wealthGain - expectedWealthGain) < tolerance
        )
    }
}
