package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.PrepaymentCalculator
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.pow

class PrepaymentCalculatorViewModelTest {

    private val calculator = PrepaymentCalculator()
    private val tolerance = 0.1

    @Test
    fun `EMI strategy reduces EMI correctly after a lump-sum prepayment on 10 lakh at 10 percent for 10 years`() {
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "10", tenureStr = "10", prepayStr = "200000",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertTrue(result.isValid)
        assertTrue("Expected originalEmi near 13215.07 but was ${result.originalEmi}", abs(result.originalEmi - 13215.07) < tolerance)
        assertTrue("Expected newEmi near 10572.06 but was ${result.newEmi}", abs(result.newEmi - 10572.06) < tolerance)
        assertTrue("Expected interestSaved near 117161.77 but was ${result.interestSaved}", abs(result.interestSaved - 117161.77) < tolerance)
        assertTrue("Expected emiReduced near 2643.01 but was ${result.emiReduced}", abs(result.emiReduced - 2643.01) < tolerance)

        // Cross-check: newEmi should exactly match the standard EMI formula applied directly
        // to the reduced principal (1000000 - 200000 = 800000). This is the same formula
        // already independently verified in EmiCalculatorViewModelTest, applied here as a
        // cross-calculator consistency check, not a repeat of the same code path.
        val effectiveP = 800000.0
        val monthlyRate = 10.0 / 12 / 100
        val months = 120.0
        val manualNewEmi = effectiveP * monthlyRate * (1 + monthlyRate).pow(months) / ((1 + monthlyRate).pow(months) - 1)
        assertTrue("Manual EMI formula on reduced principal should match newEmi", abs(result.newEmi - manualNewEmi) < tolerance)
    }

    @Test
    fun `Tenure strategy reduces tenure correctly with monthly extra payments on 10 lakh at 10 percent for 10 years`() {
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "10", tenureStr = "10", prepayStr = "0",
            strategy = "Tenure", monthlyStr = "5000", annualStr = "0"
        )
        assertTrue(result.isValid)
        assertTrue("Expected newEmi to equal originalEmi for Tenure strategy (EMI unchanged)", abs(result.newEmi - result.originalEmi) < tolerance)
        assertTrue("Expected newTenureMonths near 74 but was ${result.newTenureMonths}", abs(result.newTenureMonths - 74.0) < 1.0)
        assertTrue("Expected tenureReducedMonths near 46 but was ${result.tenureReducedMonths}", abs(result.tenureReducedMonths - 46.0) < 1.0)
        assertTrue("Expected interestSaved near 243475.58 but was ${result.interestSaved}", abs(result.interestSaved - 243475.58) < tolerance)
    }

    @Test
    fun `A lump-sum prepayment equal to the full loan amount pays it off entirely`() {
        val result = calculator.calculate(
            loanAmount = "500000", rateStr = "10", tenureStr = "5", prepayStr = "500000",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertTrue(result.isValid)
        assertEquals("newEmi should be 0 when loan is fully paid off", 0.0, result.newEmi, tolerance)
        assertEquals("newTenureMonths should be 0 when loan is fully paid off", 0.0, result.newTenureMonths, tolerance)
        assertEquals("newTotalInterest should be 0 when loan is fully paid off", 0.0, result.newTotalInterest, tolerance)
        assertTrue("interestSaved should equal the full originalTotalInterest", abs(result.interestSaved - result.originalTotalInterest) < tolerance)
    }

    @Test
    fun `calculate returns isValid false for zero loan amount`() {
        val result = calculator.calculate(
            loanAmount = "0", rateStr = "10", tenureStr = "10", prepayStr = "0",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero interest rate`() {
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "0", tenureStr = "10", prepayStr = "0",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `calculate returns isValid false for zero tenure`() {
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "10", tenureStr = "0", prepayStr = "0",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `interestSaved is never negative when new plan takes longer or costs more than original`() {
        // Sanity guard: interestSaved should be clamped to 0 minimum, never negative,
        // per the calculator's own max(0.0, ...) style guard logic
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "10", tenureStr = "10", prepayStr = "1000",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertTrue(result.isValid)
        assertTrue("interestSaved should never be negative", result.interestSaved >= 0.0)
        assertTrue("tenureReducedMonths should never be negative", result.tenureReducedMonths >= 0.0)
        assertTrue("emiReduced should never be negative", result.emiReduced >= 0.0)
    }

    @Test
    fun `originalTotalInterest equals originalTotalPayment minus loan amount`() {
        val result = calculator.calculate(
            loanAmount = "1000000", rateStr = "10", tenureStr = "10", prepayStr = "200000",
            strategy = "EMI", monthlyStr = "0", annualStr = "0"
        )
        assertTrue(result.isValid)
        assertTrue(
            "originalTotalInterest should equal originalTotalPayment - loanAmount",
            abs(result.originalTotalInterest - (result.originalTotalPayment - 1000000.0)) < tolerance
        )
    }
}
