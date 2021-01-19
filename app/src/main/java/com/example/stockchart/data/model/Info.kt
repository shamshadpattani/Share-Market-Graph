package com.example.stockchart.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stockchart.data.room.EntityDescriptions.TABLE_INFO
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@Entity(
    tableName = TABLE_INFO,
    indices = [
        Index(value = ["date"],unique = true)
    ]
)
data class Info(
    @PrimaryKey
    val date: String,
    val price: Double,
){
    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}