package com.loanmaster.pro

import com.loanmaster.pro.domain.calculator.GstCalculator
import com.loanmaster.pro.domain.model.GstMode
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs

class GstCalculatorViewModelTest {

    private val calculator = GstCalculator()
    private val tolerance = 0.01

    @Test
    fun `ADD mode with 18 percent GST intrastate splits correctly into CGST and SGST`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "10000", selectedRate = 18.0,
            cessRateText = "0", isIntrastate = true
        )
        assertTrue("Expected baseAmount 10000.00 but was ${result.baseAmount}", abs(result.baseAmount - 10000.00) < tolerance)
        assertTrue("Expected totalAmount 11800.00 but was ${result.totalAmount}", abs(result.totalAmount - 11800.00) < tolerance)
        assertTrue("Expected totalGst 1800.00 but was ${result.totalGst}", abs(result.totalGst - 1800.00) < tolerance)
        assertTrue("Expected cgst 900.00 but was ${result.cgst}", abs(result.cgst - 900.00) < tolerance)
        assertTrue("Expected sgst 900.00 but was ${result.sgst}", abs(result.sgst - 900.00) < tolerance)
        assertEquals("igst should be 0 for intrastate", 0.0, result.igst, tolerance)
    }

    @Test
    fun `REMOVE mode correctly reverses ADD mode output, and routes fully to IGST for interstate`() {
        // This amount (11800) is the exact totalAmount produced by the ADD-mode test above
        // (10000 base + 18 percent GST). REMOVE mode should reverse-derive back to
        // baseAmount = 10000 exactly — an independent cross-check between two different
        // code paths (forward ADD math vs reverse REMOVE math), not the same formula twice.
        val result = calculator.calculate(
            mode = GstMode.REMOVE, amountText = "11800", selectedRate = 18.0,
            cessRateText = "0", isIntrastate = false
        )
        assertTrue("Expected baseAmount 10000.00 but was ${result.baseAmount}", abs(result.baseAmount - 10000.00) < tolerance)
        assertTrue("Expected totalAmount 11800.00 but was ${result.totalAmount}", abs(result.totalAmount - 11800.00) < tolerance)
        assertTrue("Expected totalGst 1800.00 but was ${result.totalGst}", abs(result.totalGst - 1800.00) < tolerance)
        assertTrue("Expected igst 1800.00 but was ${result.igst}", abs(result.igst - 1800.00) < tolerance)
        assertEquals("cgst should be 0 for interstate", 0.0, result.cgst, tolerance)
        assertEquals("sgst should be 0 for interstate", 0.0, result.sgst, tolerance)
    }

    @Test
    fun `ADD mode with cess correctly calculates totalCess separately from totalGst`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "5000", selectedRate = 28.0,
            cessRateText = "12", isIntrastate = true
        )
        assertTrue("Expected baseAmount 5000.00 but was ${result.baseAmount}", abs(result.baseAmount - 5000.00) < tolerance)
        assertTrue("Expected totalAmount 7000.00 but was ${result.totalAmount}", abs(result.totalAmount - 7000.00) < tolerance)
        assertTrue("Expected totalGst 1400.00 but was ${result.totalGst}", abs(result.totalGst - 1400.00) < tolerance)
        assertTrue("Expected totalCess 600.00 but was ${result.totalCess}", abs(result.totalCess - 600.00) < tolerance)
        // Manual cross-check: total should equal base * (1 + (rate+cess)/100) = 5000 * 1.40
        val manualCheck = 5000.0 * 1.40
        assertTrue("Manual cross-check should match calculator output", abs(result.totalAmount - manualCheck) < tolerance)
    }

    @Test
    fun `calculate returns zero results for empty amount string`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "", selectedRate = 18.0,
            cessRateText = "0", isIntrastate = true
        )
        assertEquals(0.0, result.baseAmount, tolerance)
        assertEquals(0.0, result.totalAmount, tolerance)
    }

    @Test
    fun `calculate returns zero GST when selectedRate is zero`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "10000", selectedRate = 0.0,
            cessRateText = "0", isIntrastate = true
        )
        assertEquals(0.0, result.totalGst, tolerance)
        assertTrue("totalAmount should equal baseAmount when rate is 0", abs(result.totalAmount - result.baseAmount) < tolerance)
    }

    @Test
    fun `cgst plus sgst equals totalGst for intrastate`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "10000", selectedRate = 18.0,
            cessRateText = "0", isIntrastate = true
        )
        assertTrue(
            "cgst + sgst should equal totalGst",
            abs((result.cgst + result.sgst) - result.totalGst) < tolerance
        )
    }

    @Test
    fun `igst equals totalGst for interstate`() {
        val result = calculator.calculate(
            mode = GstMode.ADD, amountText = "10000", selectedRate = 18.0,
            cessRateText = "0", isIntrastate = false
        )
        assertTrue(
            "igst should equal totalGst for interstate",
            abs(result.igst - result.totalGst) < tolerance
        )
    }
}
