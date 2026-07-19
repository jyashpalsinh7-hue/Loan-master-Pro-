package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.EmiCalculator
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs

class EmiCalculatorViewModelTest {

    private val calculator = EmiCalculator()
    private val tolerance = 0.01 // allow for floating point rounding in currency calculations

    @Test
    fun `calculateEMI returns correct value for 10 lakh at 10 percent for 1 year`() {
        val emi = calculator.calculateEMI(principal = 1000000.0, annualRate = 10.0, months = 12)
        assertTrue("Expected EMI near 87915.89 but was $emi", abs(emi - 87915.89) < tolerance)
    }

    @Test
    fun `calculateEMI returns correct value for 5 lakh at 8_5 percent for 5 years`() {
        val emi = calculator.calculateEMI(principal = 500000.0, annualRate = 8.5, months = 60)
        assertTrue("Expected EMI near 10258.27 but was $emi", abs(emi - 10258.27) < tolerance)
    }

    @Test
    fun `calculateEMI returns exactly P times (1 plus r) for a single month loan`() {
        // For a 1-month loan, EMI = P * (1+r) exactly — this is a separate mathematical
        // identity independent of the general formula, used here as a cross-check.
        val principal = 100000.0
        val annualRate = 12.0
        val emi = calculator.calculateEMI(principal = principal, annualRate = annualRate, months = 1)
        val monthlyRate = annualRate / 12 / 100
        val expected = principal * (1 + monthlyRate)
        assertTrue("Expected EMI near $expected but was $emi", abs(emi - expected) < tolerance)
    }

    @Test
    fun `calculateEMI returns zero for zero principal`() {
        val emi = calculator.calculateEMI(principal = 0.0, annualRate = 10.0, months = 12)
        assertEquals(0.0, emi, tolerance)
    }

    @Test
    fun `calculateEMI returns zero for zero interest rate`() {
        val emi = calculator.calculateEMI(principal = 1000000.0, annualRate = 0.0, months = 12)
        assertEquals(0.0, emi, tolerance)
    }

    @Test
    fun `calculateEMI returns zero for zero months`() {
        val emi = calculator.calculateEMI(principal = 1000000.0, annualRate = 10.0, months = 0)
        assertEquals(0.0, emi, tolerance)
    }

    @Test
    fun `calculateEMI returns zero for negative principal`() {
        val emi = calculator.calculateEMI(principal = -50000.0, annualRate = 10.0, months = 12)
        assertEquals(0.0, emi, tolerance)
    }

    @Test
    fun `calculateFull returns hasValidInput false for empty loan amount`() {
        val result = calculator.calculateFull(
            loanAmount = "",
            interestRate = "10",
            tenureInput = "12",
            isTenureInMonths = true,
            loanType = "Home Loan"
        )
        assertFalse(result.hasValidInput)
    }

    @Test
    fun `calculateFull totalInterest equals totalPayment minus principal`() {
        val result = calculator.calculateFull(
            loanAmount = "1000000",
            interestRate = "10",
            tenureInput = "12",
            isTenureInMonths = true,
            loanType = "Home Loan"
        )
        assertTrue(result.hasValidInput)
        assertTrue(
            "totalInterest should equal totalPayment - principal",
            abs(result.totalInterest - (result.totalPayment - result.parsedLoanAmount)) < tolerance
        )
    }

    @Test
    fun `calculateFull principalPercentage and interestPercentage sum to approximately 100`() {
        val result = calculator.calculateFull(
            loanAmount = "1000000",
            interestRate = "10",
            tenureInput = "12",
            isTenureInMonths = true,
            loanType = "Home Loan"
        )
        assertTrue(result.hasValidInput)
        assertTrue(
            "principalPercentage + interestPercentage should be ~100",
            abs((result.principalPercentage + result.interestPercentage) - 100.0) < 0.1
        )
    }
}
