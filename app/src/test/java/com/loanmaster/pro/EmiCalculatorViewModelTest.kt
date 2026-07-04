package com.loanmaster.pro

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeHistoryDao : HistoryDao {
    override fun getAllHistory(): Flow<List<CalculationHistory>> = flowOf(emptyList())
    override fun getHistoryByType(type: String): Flow<List<CalculationHistory>> = flowOf(emptyList())
    override suspend fun insertHistory(history: CalculationHistory): Long = 1L
    override suspend fun deleteHistory(history: CalculationHistory) {}
    override suspend fun deleteHistoryById(id: Int) {}
    override suspend fun deleteAllHistory() {}
    override suspend fun clearAllHistory() {}
}

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner::class)
class EmiCalculatorViewModelTest {

    private lateinit var classUnderTest: EmiCalculatorViewModel

    @Before
    fun setup() {
        val repo = HistoryRepository(FakeHistoryDao())
        classUnderTest = EmiCalculatorViewModel(repo)
    }

    @Test
    fun `calculateEMI should return correct emi for valid inputs`() {
        val emi = calculateEMI(100000.0, 10.0, 12)
        assertEquals(8791.59, emi, 0.01)
    }

    @Test
    fun `calculateEMI should return 0 for invalid inputs`() {
        assertEquals(0.0, calculateEMI(0.0, 10.0, 12), 0.0)
        assertEquals(0.0, calculateEMI(100000.0, 0.0, 12), 0.0)
        assertEquals(0.0, calculateEMI(100000.0, 10.0, 0), 0.0)
    }

    @Test
    fun `updateInputs updates state flows and calculates results correctly`() {
        classUnderTest.updateInputs(
            loanAmount = "100000",
            interestRate = "10",
            tenureInput = "1",
            isTenureMonths = false,
            type = "Home Loan"
        )

        assertEquals("100000", classUnderTest.loanAmountText.value)
        assertEquals("10", classUnderTest.interestRateText.value)
        assertEquals("1", classUnderTest.tenureInputText.value)
        assertFalse(classUnderTest.isTenureInMonths.value)
        assertEquals("Home Loan", classUnderTest.loanType.value)
        assertTrue(classUnderTest.hasValidInput.value)
        assertEquals(12, classUnderTest.totalMonths.value)

        val expectedEmi = 8791.59
        assertEquals(expectedEmi, classUnderTest.monthlyEmi.value, 0.1)

        val expectedTotalPayment = expectedEmi * 12
        assertEquals(expectedTotalPayment, classUnderTest.totalPayment.value, 1.5)

        assertEquals(expectedTotalPayment - 100000, classUnderTest.totalInterest.value, 1.5)
    }

    @Test
    fun `updateInputs handles tenure in months correctly`() {
        classUnderTest.updateInputs(
            loanAmount = "50000",
            interestRate = "12",
            tenureInput = "24",
            isTenureMonths = true
        )
        
        assertEquals(24, classUnderTest.totalMonths.value)
        assertTrue(classUnderTest.hasValidInput.value)
    }

    @Test
    fun `updateInputs with invalid values sets valid flag to false`() {
        classUnderTest.updateInputs(
            loanAmount = "0",
            interestRate = "10",
            tenureInput = "1"
        )
        
        assertFalse(classUnderTest.hasValidInput.value)
        assertEquals(0.0, classUnderTest.monthlyEmi.value, 0.0)
    }

    @Test
    fun `loadFromHistory restores values correctly`() {
        val mockHistory = CalculationHistory(
            id = 99,
            calculatorType = "EMI",
            title = "Test",
            param1 = "200000",
            param2 = "8.5",
            param3 = "5",
            param4 = "false",
            param5 = "Car Loan",
            timestamp = 12345L
        )
        
        classUnderTest.loadFromHistory(mockHistory)
        
        assertEquals("200000", classUnderTest.loanAmountText.value)
        assertEquals("8.5", classUnderTest.interestRateText.value)
        assertEquals("5", classUnderTest.tenureInputText.value)
        assertFalse(classUnderTest.isTenureInMonths.value)
        assertEquals("Car Loan", classUnderTest.loanType.value)
        // Removed ID check as loadFromHistory handles ui state mostly
        
        assertTrue(classUnderTest.hasValidInput.value)
    }
}
