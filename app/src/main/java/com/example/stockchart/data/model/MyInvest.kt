package com.example.stockchart.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stockchart.data.room.EntityDescriptions.TABLE_INVEST
import java.time.format.DateTimeFormatter

@Entity(
    tableName = TABLE_INVEST,
    indices = [
        Index(value = ["id"],unique = true)
    ]
)
data class MyInvest(
    var invest_date: String,
    var invest_price: String?,
    var unit: String,
    var nav: Double?,
    var my_price: Double?,
    var price_diff:Double
){

}