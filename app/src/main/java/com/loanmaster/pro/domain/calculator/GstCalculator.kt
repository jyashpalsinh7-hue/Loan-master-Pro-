package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.GstMode

data class GstResult(
    val baseAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val totalGst: Double = 0.0,
    val totalCess: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val igst: Double = 0.0
)

class GstCalculator {
    fun calculate(
        mode: GstMode,
        amountText: String,
        selectedRate: Double,
        cessRateText: String,
        isIntrastate: Boolean
    ): GstResult {
        val amount = amountText.toDoubleOrNull() ?: 0.0
        val cessRate = cessRateText.toDoubleOrNull() ?: 0.0
                
        val baseAmount: Double
        val totalAmount: Double
        
        if (mode == GstMode.ADD) {
            baseAmount = amount
            totalAmount = amount * (1 + (selectedRate + cessRate) / 100.0)
        } else {
            totalAmount = amount
            baseAmount = amount / (1 + (selectedRate + cessRate) / 100.0)
        }
        
        val totalGst = baseAmount * (selectedRate / 100.0)
        val totalCess = baseAmount * (cessRate / 100.0)
        
        val cgst = if (isIntrastate) totalGst / 2 else 0.0
        val sgst = if (isIntrastate) totalGst / 2 else 0.0
        val igst = if (!isIntrastate) totalGst else 0.0
        
        return GstResult(
            baseAmount = baseAmount,
            totalAmount = totalAmount,
            totalGst = totalGst,
            totalCess = totalCess,
            cgst = cgst,
            sgst = sgst,
            igst = igst
        )
    }
}
