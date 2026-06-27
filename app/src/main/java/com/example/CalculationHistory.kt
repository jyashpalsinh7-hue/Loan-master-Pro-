package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val calculatorType: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val param1: String = "",
    val param2: String = "",
    val param3: String = "",
    val param4: String = "",
    val param5: String = ""
)
