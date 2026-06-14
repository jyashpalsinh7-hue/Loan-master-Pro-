package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@JsonClass(generateAdapter = true)
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

data class CurrencyState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val rates: Map<String, Double> = emptyMap(),
    val lastUpdated: String = ""
)

class CurrencyViewModel : ViewModel() {
    private val moshi = Moshi.Builder().build()

    private val api = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ExchangeRateApi::class.java)

    private val _uiState = MutableStateFlow(CurrencyState())
    val uiState: StateFlow<CurrencyState> = _uiState.asStateFlow()

    init {
        fetchRates()
    }

    fun fetchRates(baseCurrency: String = "INR") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getLatestRates(baseCurrency)
                if (response.result == "success" || response.result.lowercase() == "success") {
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val date = Date(response.time_last_update_unix * 1000)
                    
                    _uiState.value = CurrencyState(
                        isLoading = false,
                        rates = response.rates,
                        lastUpdated = sdf.format(date)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to fetch rates")
                }
            } catch (e: Exception) {
                // If there's an API failure or network issue, fallback to some mock rates for demonstration
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
                    lastUpdated = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                )
            }
        }
    }
}
