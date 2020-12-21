package com.example.stockchart.data



import com.example.stockchart.data.model.Stock
import retrofit2.Response
import retrofit2.http.*

interface APIInterface {
    @GET("/mf/119172")
    suspend fun getData(): Response<Stock>

    companion object {
        const val FOODIUM_API_URL = "https://api.mfapi.in/"
    }
}
