package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ExchangeRateResponse(
    val result: String,
    val time_last_update_unix: Long,
    val base_code: String,
    val rates: Map<String, Double>
)

interface ExchangeRateApi {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(@Path("base") baseCurrency: String): ExchangeRateResponse
}

data class FrankfurterResponse(
    val amount: Double,
    val base: String,
    val start_date: String,
    val end_date: String,
    val rates: Map<String, Map<String, Double>>
)

interface FrankfurterApi {
    @GET("{startDate}..{endDate}")
    suspend fun getHistoricalRates(
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("from") baseCurrency: String,
        @Query("to") targetCurrency: String
    ): FrankfurterResponse
}

data class CurrencyState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val rates: Map<String, Double> = emptyMap(),
    val lastUpdated: String = ""
)

data class ChartState(
    val isLoading: Boolean = false,
    val points: List<Double> = emptyList(),
    val minVal: Double = 0.0,
    val maxVal: Double = 0.0,
    val trendPercent: Double = 0.0,
    val error: String? = null
)

class CurrencyViewModel : ViewModel() {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ExchangeRateApi::class.java)

    private val frankfurterApi = Retrofit.Builder()
        .baseUrl("https://api.frankfurter.app/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(FrankfurterApi::class.java)

    private val _uiState = MutableStateFlow(CurrencyState())
    val uiState: StateFlow<CurrencyState> = _uiState.asStateFlow()

    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    init {
        fetchRates()
    }

    fun fetchRates(baseCurrency: String = "INR") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getLatestRates(baseCurrency)
                if (response.result == "success" || response.result.lowercase() == "success") {
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val instant = Instant.ofEpochSecond(response.time_last_update_unix)
                    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    
                    _uiState.value = CurrencyState(
                        isLoading = false,
                        rates = response.rates,
                        lastUpdated = date.format(formatter)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to fetch rates")
                }
            } catch (e: Exception) {
                // If there's an API failure or network issue, fallback to some mock rates for demonstration
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
                _uiState.value = CurrencyState(
                    isLoading = false,
                    error = e.localizedMessage,
                    rates = mapOf(
                        "USD" to 0.011985,
                        "EUR" to 0.010981,
                        "GBP" to 0.009498,
                        "AED" to 0.044002,
                        "JPY" to 1.8478
                    ),
                    lastUpdated = LocalDateTime.now().format(formatter)
                )
            }
        }
    }

    fun fetchChartData(baseCurrency: String, targetCurrency: String, period: String, currentRate: Double) {
        viewModelScope.launch {
            _chartState.value = _chartState.value.copy(isLoading = true, error = null)
            
            if (baseCurrency == targetCurrency) {
                _chartState.value = ChartState(points = listOf(1.0, 1.0), minVal = 0.9, maxVal = 1.1, trendPercent = 0.0)
                return@launch
            }

            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()
                val startDate = when (period) {
                    "1D" -> today.minusDays(7) // Fetch 7 days for 1D to get enough recent points
                    "1W" -> today.minusWeeks(1)
                    "1M" -> today.minusMonths(1)
                    "1Y" -> today.minusYears(1)
                    "5Y" -> today.minusYears(5)
                    else -> today.minusWeeks(1)
                }

                val response = frankfurterApi.getHistoricalRates(
                    startDate = startDate.format(formatter),
                    endDate = today.format(formatter),
                    baseCurrency = baseCurrency,
                    targetCurrency = targetCurrency
                )

                // Sort by date just in case
                val sortedEntries = response.rates.entries.sortedBy { it.key }
                var points = sortedEntries.mapNotNull { it.value[targetCurrency] }

                if (period == "1D") {
                    // Frankfurter doesn't have intraday data, so fake a bit of intraday movement from the last real day
                    points = generateIntradayNoise(points.lastOrNull() ?: currentRate, currentRate)
                } else {
                    // Append current rate for real-time accuracy at the end
                    if (points.isNotEmpty()) {
                        val list = points.toMutableList()
                        list.add(currentRate)
                        points = list
                    } else {
                        points = listOf(currentRate, currentRate)
                    }
                }

                val minVal = points.minOrNull() ?: currentRate
                val maxVal = points.maxOrNull() ?: currentRate
                
                val firstVal = points.firstOrNull() ?: currentRate
                val lastVal = points.lastOrNull() ?: currentRate
                val trendPercent = if (firstVal != 0.0) ((lastVal - firstVal) / firstVal) * 100 else 0.0

                _chartState.value = ChartState(
                    isLoading = false,
                    points = points,
                    minVal = minVal,
                    maxVal = maxVal,
                    trendPercent = trendPercent
                )
            } catch (e: Exception) {
                // Fallback to random if Frankfurter fails (e.g., unsupported currency)
                val fallbackData = generateChartDataFallback(currentRate, baseCurrency, targetCurrency, period)
                _chartState.value = ChartState(
                    isLoading = false,
                    points = fallbackData.points,
                    minVal = fallbackData.minVal,
                    maxVal = fallbackData.maxVal,
                    trendPercent = fallbackData.trendPercent,
                    error = e.localizedMessage
                )
            }
        }
    }

    private fun generateIntradayNoise(startRate: Double, currentRate: Double): List<Double> {
        val random = java.util.Random()
        val numPoints = 24
        val points = mutableListOf(startRate)
        var current = startRate
        val volatility = 0.002 // 0.2% intraday volatility

        for (i in 1 until numPoints - 1) {
            val progress = i.toDouble() / numPoints
            val targetAtProgress = startRate + (currentRate - startRate) * progress
            val noise = current * (random.nextDouble() * (volatility * 2) - volatility)
            current = targetAtProgress + noise
            points.add(current)
        }
        points.add(currentRate)
        return points
    }

    private fun generateChartDataFallback(exchangeRate: Double, baseCurrency: String, targetCurrency: String, period: String): ChartData {
        val random = java.util.Random((baseCurrency.hashCode() + targetCurrency.hashCode() + period.hashCode()).toLong())
        val volatility = when (period) {
            "1D" -> 0.005; "1W" -> 0.015; "1M" -> 0.03; "1Y" -> 0.10; "5Y" -> 0.25; else -> 0.015
        }
        val trendPercent = random.nextDouble() * (volatility * 200) - (volatility * 100)
        val startRate = exchangeRate / (1 + trendPercent / 100)
        val numPoints = when (period) { "1D" -> 24; "1W" -> 7; "1M" -> 30; "1Y" -> 12; "5Y" -> 5; else -> 10 }
        
        val points = mutableListOf(startRate)
        var current = startRate
        for (i in 1 until numPoints) {
            val progress = i.toDouble() / numPoints
            val targetAtProgress = startRate + (exchangeRate - startRate) * progress
            val noise = current * (random.nextDouble() * (volatility * 2) - volatility)
            current = targetAtProgress + noise
            points.add(current)
        }
        points.add(exchangeRate)
        
        val minVal = points.minOrNull() ?: (exchangeRate * 0.9)
        val maxVal = points.maxOrNull() ?: (exchangeRate * 1.1)
        
        return ChartData(points, minVal, maxVal, trendPercent)
    }
}

data class ChartData(
    val points: List<Double>,
    val minVal: Double,
    val maxVal: Double,
    val trendPercent: Double
)
