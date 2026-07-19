package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.RdCalculator
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs

class RdCalculatorViewModelTest {

    private val calculator = RdCalculator()
    private val tolerance = 0.1 // slightly wider tolerance due to 12-iteration compounding loop accumulation

    @Test
    fun `Standard mode returns correct maturity for 5000 per month at 8 percent for 1 year quarterly`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "5000", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = ""
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 62646.63 but was ${result.maturityValue}", abs(result.maturityValue - 62646.63) < tolerance)
        assertTrue("Expected totalInvested 60000.00 but was ${result.totalInvested}", abs(result.totalInvested - 60000.00) < tolerance)
        assertTrue("Expected totalReturns near 2646.63 but was ${result.totalReturns}", abs(result.totalReturns - 2646.63) < tolerance)
    }

    @Test
    fun `Goal Based mode reverse-solves back to the original monthly deposit from Standard mode`() {
        // This target (62646.63) is the exact maturity produced by the Standard-mode test above
        // (5000/month at 8 percent for 1 year, quarterly compounding). Goal Based mode should
        // reverse-solve calculatedMonthlyDeposit back to ~5000 — an independent cross-check
        // between the forward summation and its reverse-solve, not the same formula twice.
        val result = calculator.calculate(
            tab = "Goal Based", depositStr = "", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = "62646.63"
        )
        assertTrue(result.isValid)
        assertTrue("Expected calculatedMonthlyDeposit near 5000.00 but was ${result.calculatedMonthlyDeposit}", abs(result.calculatedMonthlyDeposit - 5000.00) < tolerance)
    }

    @Test
    fun `Standard mode returns correct maturity for 2000 per month at 7 percent for 2 years monthly`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "2000", rateStr = "7", tenureStr = "2",
            compFreq = "Monthly", targetStr = ""
        )
        assertTrue(result.isValid)
        assertTrue("Expected maturity near 51661.68 but was ${result.maturityValue}", abs(result.maturityValue - 51661.68) < tolerance)
        assertTrue("Expected totalInvested 48000.00 but was ${result.totalInvested}", abs(result.totalInvested - 48000.00) < tolerance)
        assertTrue("Expected totalReturns near 3661.68 but was ${result.totalReturns}", abs(result.totalReturns - 3661.68) < tolerance)
    }

    @Test
    fun `Standard mode returns isValid false for zero deposit`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "0", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = ""
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `Standard mode returns isValid false for zero rate`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "5000", rateStr = "0", tenureStr = "1",
            compFreq = "Quarterly", targetStr = ""
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `Standard mode returns isValid false for zero tenure`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "5000", rateStr = "8", tenureStr = "0",
            compFreq = "Quarterly", targetStr = ""
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `Goal Based mode returns isValid false for zero target`() {
        val result = calculator.calculate(
            tab = "Goal Based", depositStr = "", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = "0"
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `totalReturns equals maturityValue minus totalInvested when maturity exceeds invested`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "5000", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = ""
        )
        assertTrue(result.isValid)
        assertTrue(
            "totalReturns should equal maturityValue - totalInvested",
            abs(result.totalReturns - (result.maturityValue - result.totalInvested)) < tolerance
        )
    }

    @Test
    fun `wealthGain matches totalReturns divided by totalInvested times 100`() {
        val result = calculator.calculate(
            tab = "Standard", depositStr = "5000", rateStr = "8", tenureStr = "1",
            compFreq = "Quarterly", targetStr = ""
        )
        assertTrue(result.isValid)
        val expectedWealthGain = (result.totalReturns / result.totalInvested) * 100
        assertTrue(
            "wealthGain should match totalReturns/totalInvested*100",
            abs(result.wealthGain - expectedWealthGain) < tolerance
        )
    }
}
