package com.example.stockchart.data.repository

import android.app.Application
import com.example.stockchart.data.APIClient
import com.example.stockchart.data.APIInterface
import com.example.stockchart.data.NetworkBoundRepository
import com.example.stockchart.data.model.Stock
import com.example.stockchart.data.utlis.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class StockRepository (c: Application) {

    @ExperimentalCoroutinesApi
    fun getData(): Flow<ResultOf<Stock>> {
        return object : NetworkBoundRepository<Stock>() {
            override suspend fun fetchFromRemote(): Response<Stock> = APIClient.getApiService().getData()
        }.asFlow().flowOn(Dispatchers.IO)
    }
}
