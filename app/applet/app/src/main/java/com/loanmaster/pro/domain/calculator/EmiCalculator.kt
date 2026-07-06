package com.loanmaster.pro.domain.calculator

class EmiCalculator {
    data class Result(
        val monthlyEmi: Double,
        val totalInterest: Double,
        val totalPayment: Double,
        val isValid: Boolean
    )

    fun calculate(loanAmount: String, interestRate: String, tenureYears: String): Result {
        val p = loanAmount.toDoubleOrNull() ?: 0.0
        val r = interestRate.toDoubleOrNull() ?: 0.0
        val t = tenureYears.toDoubleOrNull() ?: 0.0

        if (p <= 0 || r <= 0 || t <= 0) {
            return Result(0.0, 0.0, 0.0, false)
        }

        val monthlyRate = r / 12 / 100
        val months = t * 12
        val emi = p * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1)
        val totalPayment = emi * months
        val totalInterest = totalPayment - p

        return Result(emi, totalInterest, totalPayment, true)
    }
}
