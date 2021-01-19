package com.example.stockchart.data



import com.example.stockchart.data.model.Stock
import retrofit2.Response
import retrofit2.http.*

interface APIInterface {
    @GET("/api/v3/datasets/AMFI/119172.json?api_key=-ZNfTunpsS65kY8rNJNa")
    suspend fun getData(): Response<Stock>

    companion object {
        const val STOCK_API_URL = "https://www.quandl.com/"
    }
}
