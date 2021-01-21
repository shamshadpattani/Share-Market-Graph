package com.example.stockchart.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.room.EntityDescriptions

@Dao
abstract class InvestDao {

   /* fun insertAuditSummary(homeSummary: StockInfoDao) {
        softDeleteAllCenters()
        val sId = insert(homeSummary).toInt()
        homeSummary.centers?.let{
            it.forEach { center ->
                center.localSummaryId = sId
            }
            insert(it)
        }
    }*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(summary: MyInvestDB): Long

    @Query("SELECT * FROM ${EntityDescriptions.TABLE_INVEST} ")
    abstract fun getInvestLive(): LiveData<List<MyInvestDB>>

    @Query("SELECT * FROM ${EntityDescriptions.TABLE_INVEST} ")
    abstract fun getInvest(): List<MyInvestDB>
}