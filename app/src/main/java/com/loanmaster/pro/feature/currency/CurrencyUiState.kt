package com.loanmaster.pro.feature.currency
import retrofit2.http.*

import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

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


