package com.loanmaster.pro.feature.loaneligibility.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.loanmaster.pro.domain.model.LoanProfile

val loanProfiles = listOf(
    LoanProfile("Home Loan", 0.65, "8.5", "20"),
    LoanProfile("Personal Loan", 0.50, "11.0", "5"),
    LoanProfile("Car Loan", 0.55, "9.0", "7"),
    LoanProfile("Education Loan", 0.50, "10.0", "10"),
    LoanProfile("Business Loan", 0.50, "12.0", "5"),
    LoanProfile("Gold Loan", 0.75, "9.5", "2"),
    LoanProfile("Medical Loan", 0.45, "11.5", "4"),
    LoanProfile("Travel Loan", 0.40, "12.5", "2"),
    LoanProfile("Two Wheeler Loan", 0.50, "10.5", "3")
)

fun getLoanTypeIcon(name: String): ImageVector {
    return when (name) {
        "Home Loan" -> Icons.Rounded.HomeWork
        "Personal Loan" -> Icons.Rounded.Person
        "Car Loan" -> Icons.Rounded.DirectionsCar
        "Education Loan" -> Icons.Rounded.School
        "Business Loan" -> Icons.Rounded.Storefront
        "Gold Loan" -> Icons.Rounded.MonetizationOn
        "Medical Loan" -> Icons.Rounded.LocalHospital
        "Travel Loan" -> Icons.Rounded.FlightTakeoff
        "Two Wheeler Loan" -> Icons.Rounded.TwoWheeler
        else -> Icons.Rounded.HomeWork
    }
}
