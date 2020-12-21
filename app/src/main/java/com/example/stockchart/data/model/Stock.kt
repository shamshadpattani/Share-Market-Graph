package com.example.stockchart.data.model

data class Stock(
    val values: List<Value>,
    val details: Detail,
    val status: String
)

data class Value(
    val date: String,
    val nav: String
)

data class Detail(
    val fund_house: String,
    val scheme_category: String,
    val scheme_code: Int,
    val scheme_name: String,
    val scheme_type: String
)