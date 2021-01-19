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
data class MyInvestDB(
    var invest_date: String,
    var invest_price: String?,
    var unit: String,
    var nav: Double?,

){
    @PrimaryKey(autoGenerate = true)
    var id :Int? =null
    @Ignore
    var my_price: Double? = null
    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      /*  fun from(myInvest: MyInvest): MyInvest {
            return MyInvest(
                    invest_date = myInvest.invest_date,
                    invest_price = myInvest.invest_price,
                    unit = myInvest.unit,
                    nav =myInvest.nav,
                    my_price = myInvest.my_price
            )
        }*/
    }
}