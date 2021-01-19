package com.example.stockchart.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    private var apiService: APIInterface? = null
val httpClient = OkHttpClient.Builder()

    fun getApiService():APIInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl(APIInterface.STOCK_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
        apiService = retrofit.create(APIInterface::class.java)
        return apiService!!
    }
}