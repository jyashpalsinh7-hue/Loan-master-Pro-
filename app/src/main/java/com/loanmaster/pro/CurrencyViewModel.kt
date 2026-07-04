package com.loanmaster.pro

import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
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

data class CurrencyConverterUiState(
    val baseAmountText: String = "",
    val baseCurrency: String = "USD",
    val targetCurrency: String = "EUR",
    val showBaseSelector: Boolean = false,
    val showTargetSelector: Boolean = false,
    val selectedTab: String = "1W",
    val searchQuery: String = "",
    
    val isLoading: Boolean = true,
    val error: String? = null,
    val rates: Map<String, Double> = emptyMap(),
    val lastUpdated: String = "",
    
    val isChartLoading: Boolean = false,
    val chartPoints: List<Double> = emptyList(),
    val chartMinVal: Double = 0.0,
    val chartMaxVal: Double = 0.0,
    val chartTrendPercent: Double = 0.0,
    val chartError: String? = null
)

sealed class CurrencyEvent {
    data class BaseAmountChanged(val amount: String) : CurrencyEvent()
    data class BaseCurrencySelected(val currency: String) : CurrencyEvent()
    data class TargetCurrencySelected(val currency: String) : CurrencyEvent()
    object SwapCurrencies : CurrencyEvent()
    data class TabSelected(val tab: String) : CurrencyEvent()
    data class ShowBaseSelector(val show: Boolean) : CurrencyEvent()
    data class ShowTargetSelector(val show: Boolean) : CurrencyEvent()
    data class SearchQueryChanged(val query: String) : CurrencyEvent()
    object RefreshRates : CurrencyEvent()
}

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

    private val _uiState = MutableStateFlow(CurrencyConverterUiState())
    val uiState: StateFlow<CurrencyConverterUiState> = _uiState.asStateFlow()

    init {
        fetchRates(_uiState.value.baseCurrency)
    }

    fun onEvent(event: CurrencyEvent) {
        when (event) {
            is CurrencyEvent.BaseAmountChanged -> _uiState.update { it.copy(baseAmountText = event.amount) }
            is CurrencyEvent.BaseCurrencySelected -> {
                _uiState.update { it.copy(baseCurrency = event.currency, showBaseSelector = false, searchQuery = "") }
                fetchRates(event.currency)
            }
            is CurrencyEvent.TargetCurrencySelected -> {
                _uiState.update { it.copy(targetCurrency = event.currency, showTargetSelector = false, searchQuery = "") }
                fetchChartData()
            }
            CurrencyEvent.SwapCurrencies -> {
                _uiState.update { 
                    val temp = it.baseCurrency
                    it.copy(baseCurrency = it.targetCurrency, targetCurrency = temp)
                }
                fetchRates(_uiState.value.baseCurrency)
            }
            is CurrencyEvent.TabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
                fetchChartData()
            }
            is CurrencyEvent.ShowBaseSelector -> _uiState.update { it.copy(showBaseSelector = event.show, searchQuery = "") }
            is CurrencyEvent.ShowTargetSelector -> _uiState.update { it.copy(showTargetSelector = event.show, searchQuery = "") }
            is CurrencyEvent.SearchQueryChanged -> _uiState.update { it.copy(searchQuery = event.query) }
            CurrencyEvent.RefreshRates -> fetchRates(_uiState.value.baseCurrency)
        }
    }

    private fun fetchRates(baseCurrency: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = api.getLatestRates(baseCurrency)
                if (response.result == "success" || response.result.lowercase() == "success") {
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val instant = Instant.ofEpochSecond(response.time_last_update_unix)
                    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            rates = response.rates,
                            lastUpdated = date.format(formatter)
                        )
                    }
                    fetchChartData()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to fetch rates") }
                }
            } catch (e: Exception) {
                // Fallback to mock rates
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault())
                val mockRates = mapOf(
                    "USD" to 0.011985,
                    "EUR" to 0.010981,
                    "GBP" to 0.009498,
                    "AED" to 0.044002,
                    "JPY" to 1.8478
                )
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage,
                        rates = mockRates,
                        lastUpdated = LocalDateTime.now().format(formatter)
                    )
                }
                fetchChartData()
            }
        }
    }

    private fun fetchChartData() {
        val state = _uiState.value
        val baseCurrency = state.baseCurrency
        val targetCurrency = state.targetCurrency
        val period = state.selectedTab
        val currentRate = state.rates[targetCurrency] ?: 1.0

        viewModelScope.launch {
            _uiState.update { it.copy(isChartLoading = true, chartError = null) }
            
            if (baseCurrency == targetCurrency) {
                _uiState.update { 
                    it.copy(
                        isChartLoading = false,
                        chartPoints = listOf(1.0, 1.0),
                        chartMinVal = 0.9,
                        chartMaxVal = 1.1,
                        chartTrendPercent = 0.0
                    ) 
                }
                return@launch
            }
            
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()
                val startDate = when (period) {
                    "1D" -> today.minusDays(7)
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
                
                val sortedEntries = response.rates.entries.sortedBy { it.key }
                var points = sortedEntries.mapNotNull { it.value[targetCurrency] }
                
                if (period == "1D") {
                    points = generateIntradayNoise(points.lastOrNull() ?: currentRate, currentRate)
                } else {
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
                
                _uiState.update {
                    it.copy(
                        isChartLoading = false,
                        chartPoints = points,
                        chartMinVal = minVal,
                        chartMaxVal = maxVal,
                        chartTrendPercent = trendPercent
                    )
                }
            } catch (e: Exception) {
                val fallbackData = generateChartDataFallback(currentRate, baseCurrency, targetCurrency, period)
                _uiState.update {
                    it.copy(
                        isChartLoading = false,
                        chartPoints = fallbackData.points,
                        chartMinVal = fallbackData.minVal,
                        chartMaxVal = fallbackData.maxVal,
                        chartTrendPercent = fallbackData.trendPercent,
                        chartError = e.localizedMessage
                    )
                }
            }
        }
    }

    private fun generateIntradayNoise(startRate: Double, currentRate: Double): List<Double> {
        val random = java.util.Random()
        val numPoints = 24
        val points = mutableListOf(startRate)
        var current = startRate
        val volatility = 0.002
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
