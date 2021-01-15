package com.example.stockchart.data.room.dao

import android.icu.text.IDNA
import androidx.room.*
import com.example.stockchart.data.model.Info

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

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(centers: List<Center>): List<Long>*/

   /* fun getSummary(): AuditSummary? {
        val summary = getPartialSummary()
        summary?.let { it.centers = getCenters(summary.localId) }
        return summary
    }*/

}