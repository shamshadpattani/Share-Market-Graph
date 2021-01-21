package com.example.stockchart.utlis

import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log10
import kotlin.math.pow

object CommonUtils {

    fun getReadableDate(dt: LocalDate?) : String {
        val fm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("EEE, d LLL yyyy")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return dt?.format(fm)?:""
    }

  fun dateConversion(date: List<String>): List<String> {
        val formattedDate: MutableList<String> = mutableListOf()
        date.map {
            try {
                val parser = SimpleDateFormat("yyyy-MM-d", Locale.ROOT)
                val formatter = SimpleDateFormat("dd MMM", Locale.ROOT)
                formattedDate.add(formatter.format(parser.parse(it)))
            } catch(e: ParseException) {
                listOf("")
            }
        }
        return formattedDate
    }
     fun dateConversionSrting(date:String): String? {
        var formattedDate:String?=null
        try{
            val parser = SimpleDateFormat("yyyy-MM-d", Locale.ROOT)
            val formatter = SimpleDateFormat("dd MMM", Locale.ROOT)
            formattedDate=formatter.format(parser.parse(date))
        } catch(e: ParseException) {
            ""
        }
        return formattedDate
    }
}