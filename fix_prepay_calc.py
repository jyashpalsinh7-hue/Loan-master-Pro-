import re
import os

calc_path = "app/src/main/java/com/loanmaster/pro/domain/calculator/PrepaymentCalculator.kt"

new_calc = """package com.loanmaster.pro.domain.calculator

import kotlin.math.pow

data class PrepaymentAmortizationRow(
    val month: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double,
    val isPrepayment: Boolean = false,
    val label: String = ""
)

data class PrepaymentResult(
    val originalEmi: Double = 0.0,
    val originalTotalPayment: Double = 0.0,
    val originalTotalInterest: Double = 0.0,
    val newEmi: Double = 0.0,
    val newTenureMonths: Double = 0.0,
    val newTotalInterest: Double = 0.0,
    val interestSaved: Double = 0.0,
    val tenureReducedMonths: Double = 0.0,
    val emiReduced: Double = 0.0,
    val isValid: Boolean = false,
    val standardSchedule: List<PrepaymentAmortizationRow> = emptyList(),
    val prepaySchedule: List<PrepaymentAmortizationRow> = emptyList()
)

class PrepaymentCalculator {

    fun generateStandardSchedule(p: Double, r: Double, n: Double, emi: Double): List<PrepaymentAmortizationRow> {
        var balance = p
        val schedule = mutableListOf<PrepaymentAmortizationRow>()
        for (i in 1..n.toInt()) {
            val interest = balance * r
            var principal = emi - interest
            if (balance < principal) {
                principal = balance
            }
            balance -= principal
            schedule.add(PrepaymentAmortizationRow(i, principal + interest, principal, interest, if (balance < 0) 0.0 else balance, false, "EMI"))
            if (balance <= 0) break
        }
        return schedule
    }

    fun generatePrepaymentSchedule(p: Double, prePay: Double, monthlyPrepay: Double, annualPrepay: Double, r: Double, n: Double, emi: Double, strategy: String): List<PrepaymentAmortizationRow> {
        var balance = p
        val schedule = mutableListOf<PrepaymentAmortizationRow>()
        
        if (prePay > 0) {
            val actualPrepay = if (balance < prePay) balance else prePay
            balance -= actualPrepay
            schedule.add(PrepaymentAmortizationRow(0, actualPrepay, actualPrepay, 0.0, balance, true, "Lump Sum"))
        }

        if (balance <= 0) return schedule

        var currentEmi = emi
        if (strategy == "EMI") {
            currentEmi = if (r > 0 && n > 0) balance * (r * kotlin.math.pow(1 + r, n)) / (kotlin.math.pow(1 + r, n) - 1) else balance / n
        }

        var month = 1
        while (balance > 0.01 && month <= n.toInt() * 2) {
            val interest = balance * r
            var principal = currentEmi - interest
            if (balance < principal) {
                principal = balance
                currentEmi = principal + interest
            }
            balance -= principal
            schedule.add(PrepaymentAmortizationRow(month, currentEmi, principal, interest, if (balance < 0) 0.0 else balance, false, "EMI"))
            
            if (strategy == "Tenure" && monthlyPrepay > 0 && balance > 0.01) {
                val extraPrepay = if (balance < monthlyPrepay) balance else monthlyPrepay
                balance -= extraPrepay
                schedule.add(PrepaymentAmortizationRow(month, extraPrepay, extraPrepay, 0.0, if (balance < 0) 0.0 else balance, true, "Extra Pay"))
            }

            if (strategy == "Tenure" && annualPrepay > 0 && balance > 0.01 && month % 12 == 0) {
                val extraPrepay = if (balance < annualPrepay) balance else annualPrepay
                balance -= extraPrepay
                schedule.add(PrepaymentAmortizationRow(month, extraPrepay, extraPrepay, 0.0, if (balance < 0) 0.0 else balance, true, "Annual Pay"))
            }

            month++
        }
        return schedule
    }

    fun calculate(
        loanAmount: String,
        rateStr: String,
        tenureStr: String,
        prepayStr: String,
        strategy: String,
        monthlyStr: String,
        annualStr: String
    ): PrepaymentResult {
        val p = loanAmount.toDoubleOrNull() ?: 0.0
        val r = rateStr.toDoubleOrNull() ?: 0.0
        val y = tenureStr.toDoubleOrNull() ?: 0.0
        val prepay = prepayStr.toDoubleOrNull() ?: 0.0
        val monthlyPrepay = monthlyStr.toDoubleOrNull() ?: 0.0
        val annualPrepay = annualStr.toDoubleOrNull() ?: 0.0

        if (p <= 0 || r <= 0 || y <= 0) {
            return PrepaymentResult(isValid = false)
        }

        val monthlyRate = r / 12 / 100
        val totalMonths = y * 12

        val originalEmi = if (monthlyRate > 0) {
            p * monthlyRate * (1 + monthlyRate).pow(totalMonths) /
                    ((1 + monthlyRate).pow(totalMonths) - 1)
        } else 0.0

        val originalTotalInterest = (originalEmi * totalMonths) - p
        val originalTotalPayment = originalEmi * totalMonths

        var newEmi = originalEmi
        var newTenureMonths = totalMonths
        var newTotalInterest = 0.0

        val effectiveP = p - prepay

        if (effectiveP <= 0) {
            return PrepaymentResult(
                originalEmi = originalEmi,
                originalTotalPayment = originalTotalPayment,
                originalTotalInterest = originalTotalInterest,
                newEmi = 0.0,
                newTenureMonths = 0.0,
                newTotalInterest = 0.0,
                interestSaved = originalTotalInterest,
                tenureReducedMonths = totalMonths,
                emiReduced = originalEmi,
                isValid = true,
                standardSchedule = generateStandardSchedule(p, monthlyRate, totalMonths, originalEmi),
                prepaySchedule = generatePrepaymentSchedule(p, prepay, monthlyPrepay, annualPrepay, monthlyRate, totalMonths, originalEmi, strategy)
            )
        }

        if (strategy == "EMI") {
            if (monthlyRate > 0) {
                newEmi = effectiveP * monthlyRate * (1 + monthlyRate).pow(totalMonths) /
                        ((1 + monthlyRate).pow(totalMonths) - 1)
            } else {
                newEmi = effectiveP / totalMonths
            }
            
            var bal = effectiveP
            var i = 0
            while (bal > 0 && i < totalMonths.toInt()) {
                val interest = bal * monthlyRate
                var principalPaid = newEmi - interest + monthlyPrepay
                if ((i + 1) % 12 == 0) {
                    principalPaid += annualPrepay
                }
                newTotalInterest += interest
                bal -= principalPaid
                i++
            }
            newTenureMonths = totalMonths
        } else {
            var bal = effectiveP
            var i = 0
            while (bal > 0) {
                val interest = bal * monthlyRate
                var principalPaid = originalEmi - interest + monthlyPrepay
                if ((i + 1) % 12 == 0) {
                    principalPaid += annualPrepay
                }
                newTotalInterest += interest
                bal -= principalPaid
                i++
                if (i > totalMonths * 3) break // safety
            }
            newTenureMonths = i.toDouble()
        }

        val interestSaved = if (originalTotalInterest > newTotalInterest) originalTotalInterest - newTotalInterest else 0.0
        val tenureReducedMonths = if (totalMonths > newTenureMonths) totalMonths - newTenureMonths else 0.0
        val emiReduced = if (originalEmi > newEmi) originalEmi - newEmi else 0.0

        return PrepaymentResult(
            originalEmi = originalEmi,
            originalTotalPayment = originalTotalPayment,
            originalTotalInterest = originalTotalInterest,
            newEmi = newEmi,
            newTenureMonths = newTenureMonths,
            newTotalInterest = newTotalInterest,
            interestSaved = interestSaved,
            tenureReducedMonths = tenureReducedMonths,
            emiReduced = emiReduced,
            isValid = true,
            standardSchedule = generateStandardSchedule(p, monthlyRate, totalMonths, originalEmi),
            prepaySchedule = generatePrepaymentSchedule(p, prepay, monthlyPrepay, annualPrepay, monthlyRate, totalMonths, originalEmi, strategy)
        )
    }
}
"""
with open(calc_path, "w") as f:
    f.write(new_calc)
