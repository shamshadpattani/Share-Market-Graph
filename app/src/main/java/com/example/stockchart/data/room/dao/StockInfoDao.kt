package com.example.stockchart.data.room.dao

import android.icu.text.IDNA
import androidx.room.*
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.room.EntityDescriptions

@Dao
abstract class StockInfoDao {

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
    abstract fun insert(summary: Info): Long

    @Query("SELECT * FROM ${EntityDescriptions.TABLE_INFO} WHERE date = :id")
    abstract fun getPrice(id: String): Info?

}