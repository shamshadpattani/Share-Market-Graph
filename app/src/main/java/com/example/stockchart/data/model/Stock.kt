package com.example.stockchart.data.model

data class Stock(
    val dataset: Dataset
)

data class Dataset(
    val collapse: Any,
    val column_index: Any,
    val column_names: List<String>,
    val `data`: List<List<Any>>,
    val database_code: String,
    val database_id: Int,
    val dataset_code: String,
    val description: String,
    val end_date: String,
    val frequency: String,
    val id: Int,
    val limit: Any,
    val name: String,
    val newest_available_date: String,
    val oldest_available_date: String,
    val order: Any,
    val premium: Boolean,
    val refreshed_at: String,
    val start_date: String,
    val transform: Any,
    val type: String
)