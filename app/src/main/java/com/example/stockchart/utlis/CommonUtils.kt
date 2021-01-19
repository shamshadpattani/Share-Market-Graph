package com.example.stockchart.utlis

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
}