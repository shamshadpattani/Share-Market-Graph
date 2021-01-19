package com.example.stockchart.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.stockchart.data.APIClient
import com.example.stockchart.data.NetworkBoundRepository
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.model.Stock
import com.example.stockchart.data.room.StockDatabase
import com.example.stockchart.data.utlis.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class StockRepository (c: Application) {
    private var mContext: Application = c
    private val db = StockDatabase.getInstance(mContext).saveInfoDao()
    private val investDb = StockDatabase.getInstance(mContext).saveInvestDao()

    @ExperimentalCoroutinesApi
    fun getData(): Flow<ResultOf<Stock>> {
        return object : NetworkBoundRepository<Stock>() {
            override suspend fun fetchFromRemote(): Response<Stock> = APIClient.getApiService().getData()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun savedbdata(info: Info): Long {
        val inputDb = db.insert(info)
        return inputDb
    }

    fun getPriceFromDB(it: String): Info? {
        return db.getPrice(it)
    }

    fun saveInvest(myInDB: MyInvestDB): Long {
        val inputDb = investDb.insert(myInDB)
        return inputDb
    }

    fun getsaveInvest(): LiveData<List<MyInvestDB>> {
        return investDb.getInvest()
    }
}
