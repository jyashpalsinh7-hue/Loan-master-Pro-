package com.loanmaster.pro.data.local.room

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object DatabasePrepopulator {
    fun prepopulateIfEmpty(context: Context, repository: HistoryRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            val history = repository.getAllHistory().first()
            if (history.isEmpty()) {
                val sampleData = mutableListOf<CalculationHistory>()
                
                // EMI Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "EMI", title = "Home Loan - ₹50,00,000", param1 = "5000000", param2 = "8.5", param3 = "20", param4 = "false", param5 = "Home Loan", timestamp = System.currentTimeMillis() - 100000))
                sampleData.add(CalculationHistory(calculatorType = "EMI", title = "Car Loan - ₹8,00,000", param1 = "800000", param2 = "9.0", param3 = "5", param4 = "false", param5 = "Car Loan", timestamp = System.currentTimeMillis() - 200000))
                sampleData.add(CalculationHistory(calculatorType = "EMI", title = "Personal Loan - ₹2,50,000", param1 = "250000", param2 = "12.5", param3 = "3", param4 = "false", param5 = "Personal Loan", timestamp = System.currentTimeMillis() - 300000))
                sampleData.add(CalculationHistory(calculatorType = "EMI", title = "Education Loan - ₹15,00,000", param1 = "1500000", param2 = "7.5", param3 = "10", param4 = "false", param5 = "Education Loan", timestamp = System.currentTimeMillis() - 400000))
                sampleData.add(CalculationHistory(calculatorType = "EMI", title = "Bike Loan - ₹1,20,000", param1 = "120000", param2 = "11.0", param3 = "2", param4 = "false", param5 = "Two-Wheeler Loan", timestamp = System.currentTimeMillis() - 500000))

                // SIP Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "SIP", title = "₹5000 for 10 Yrs at 12%", param1 = "5000", param2 = "12", param3 = "10", param4 = "", timestamp = System.currentTimeMillis() - 600000))
                sampleData.add(CalculationHistory(calculatorType = "SIP", title = "₹10000 for 15 Yrs at 12%", param1 = "10000", param2 = "12", param3 = "15", param4 = "", timestamp = System.currentTimeMillis() - 700000))
                sampleData.add(CalculationHistory(calculatorType = "SIP", title = "₹2000 for 5 Yrs at 10%", param1 = "2000", param2 = "10", param3 = "5", param4 = "", timestamp = System.currentTimeMillis() - 800000))
                sampleData.add(CalculationHistory(calculatorType = "SIP", title = "₹15000 for 20 Yrs at 15%", param1 = "15000", param2 = "15", param3 = "20", param4 = "10", timestamp = System.currentTimeMillis() - 900000))
                sampleData.add(CalculationHistory(calculatorType = "SIP", title = "₹500 for 30 Yrs at 12%", param1 = "500", param2 = "12", param3 = "30", param4 = "", timestamp = System.currentTimeMillis() - 1000000))

                // FD Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "FD", title = "FD - ₹1,00,000 for 1 Yrs", param1 = "100000", param2 = "7.0", param3 = "1", param4 = "Quarterly", timestamp = System.currentTimeMillis() - 1100000))
                sampleData.add(CalculationHistory(calculatorType = "FD", title = "FD - ₹5,00,000 for 3 Yrs", param1 = "500000", param2 = "7.5", param3 = "3", param4 = "Quarterly", timestamp = System.currentTimeMillis() - 1200000))
                sampleData.add(CalculationHistory(calculatorType = "FD", title = "FD - ₹10,00,000 for 5 Yrs", param1 = "1000000", param2 = "8.0", param3 = "5", param4 = "Quarterly", timestamp = System.currentTimeMillis() - 1300000))
                sampleData.add(CalculationHistory(calculatorType = "FD", title = "FD - ₹50,000 for 0.5 Yrs", param1 = "50000", param2 = "6.5", param3 = "0.5", param4 = "Quarterly", timestamp = System.currentTimeMillis() - 1400000))
                sampleData.add(CalculationHistory(calculatorType = "FD", title = "FD - ₹2,50,000 for 10 Yrs", param1 = "250000", param2 = "8.5", param3 = "10", param4 = "Yearly", timestamp = System.currentTimeMillis() - 1500000))

                // RD Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "RD", title = "RD - ₹2000/mo for 2 Yrs", param1 = "2000", param2 = "7.0", param3 = "2", param4 = "Quarterly", param5 = "Standard", timestamp = System.currentTimeMillis() - 1600000))
                sampleData.add(CalculationHistory(calculatorType = "RD", title = "RD - ₹5000/mo for 5 Yrs", param1 = "5000", param2 = "7.5", param3 = "5", param4 = "Quarterly", param5 = "Standard", timestamp = System.currentTimeMillis() - 1700000))
                sampleData.add(CalculationHistory(calculatorType = "RD", title = "RD - ₹10000/mo for 1 Yrs", param1 = "10000", param2 = "6.5", param3 = "1", param4 = "Quarterly", param5 = "Standard", timestamp = System.currentTimeMillis() - 1800000))
                sampleData.add(CalculationHistory(calculatorType = "RD", title = "RD - ₹500/mo for 10 Yrs", param1 = "500", param2 = "8.0", param3 = "10", param4 = "Quarterly", param5 = "Standard", timestamp = System.currentTimeMillis() - 1900000))
                sampleData.add(CalculationHistory(calculatorType = "RD", title = "Goal Based - ₹1,00,000", param1 = "", param2 = "7.0", param3 = "3", param4 = "Quarterly", param5 = "Target", timestamp = System.currentTimeMillis() - 2000000))

                // GST Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "GST", title = "GST ADD - ₹1000 (18%)", param1 = "ADD", param2 = "1000", param3 = "18", param4 = "0", timestamp = System.currentTimeMillis() - 2100000))
                sampleData.add(CalculationHistory(calculatorType = "GST", title = "GST REMOVE - ₹1180 (18%)", param1 = "REMOVE", param2 = "1180", param3 = "18", param4 = "0", timestamp = System.currentTimeMillis() - 2200000))
                sampleData.add(CalculationHistory(calculatorType = "GST", title = "GST ADD - ₹5000 (5%)", param1 = "ADD", param2 = "5000", param3 = "5", param4 = "0", timestamp = System.currentTimeMillis() - 2300000))
                sampleData.add(CalculationHistory(calculatorType = "GST", title = "GST REMOVE - ₹1050 (5%)", param1 = "REMOVE", param2 = "1050", param3 = "5", param4 = "0", timestamp = System.currentTimeMillis() - 2400000))
                sampleData.add(CalculationHistory(calculatorType = "GST", title = "GST ADD - ₹25000 (28%)", param1 = "ADD", param2 = "25000", param3 = "28", param4 = "12", timestamp = System.currentTimeMillis() - 2500000))

                // Prepayment Calculator (5 items)
                sampleData.add(CalculationHistory(calculatorType = "Prepayment", title = "Prepayment - ₹50,00,000", param1 = "5000000", param2 = "8.5", param3 = "20", param4 = "200000", param5 = "Tenure", timestamp = System.currentTimeMillis() - 2600000))
                sampleData.add(CalculationHistory(calculatorType = "Prepayment", title = "Prepayment - ₹30,00,000", param1 = "3000000", param2 = "9.0", param3 = "15", param4 = "100000", param5 = "EMI", timestamp = System.currentTimeMillis() - 2700000))
                sampleData.add(CalculationHistory(calculatorType = "Prepayment", title = "Prepayment - ₹10,00,000", param1 = "1000000", param2 = "10.5", param3 = "5", param4 = "50000", param5 = "Tenure", timestamp = System.currentTimeMillis() - 2800000))
                sampleData.add(CalculationHistory(calculatorType = "Prepayment", title = "Prepayment - ₹75,00,000", param1 = "7500000", param2 = "8.0", param3 = "25", param4 = "500000", param5 = "EMI", timestamp = System.currentTimeMillis() - 2900000))
                sampleData.add(CalculationHistory(calculatorType = "Prepayment", title = "Prepayment - ₹15,00,000", param1 = "1500000", param2 = "12.0", param3 = "10", param4 = "0", param5 = "Tenure", timestamp = System.currentTimeMillis() - 3000000))

                sampleData.forEach {
                    repository.saveHistory(it)
                }
            }
        }
    }
}
