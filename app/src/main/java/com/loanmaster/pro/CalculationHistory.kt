package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val calculatorType: String,
    val title: String,
    val param1: String? = null,
    val param2: String? = null,
    val param3: String? = null,
    val param4: String? = null,
    val param5: String? = null,
    val result1: Double? = null,
    val result2: Double? = null,
    val result3: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
