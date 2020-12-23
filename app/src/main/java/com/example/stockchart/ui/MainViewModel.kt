package com.example.stockchart.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stockchart.data.model.Stock
import com.example.stockchart.data.repository.StockRepository
import com.example.stockchart.data.utlis.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val date = MutableLiveData<List<String>>()
    val price = MutableLiveData<List<Double>>()

    var stockvalues = MutableLiveData<Pair<List<String>, List<Double>>> ()

    fun setData(data: List<List<Any>>) {
        var _price:MutableList<Double> = mutableListOf()
        var _date:MutableList<String> = mutableListOf()
        data.map { values->
            _date.add(values[0] as String)
            _price.add(values[1] as Double)
        }

        date.postValue(dateConversion(_date.reversed()))
        price.postValue(_price.reversed())
        stockvalues.value= Pair(dateConversion(_date.reversed()),_price.reversed())
    }

    private fun dateConversion(date: List<String>): List<String> {
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

    private val _stockdata = MutableLiveData<Stock>()
    val stockdata: LiveData<Stock>
        get() {
            _stockdata.value ?: fetchStockDetails()
            return _stockdata
        }

    private fun fetchStockDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepo.getData().collect{ result ->
                when(result) {
                    is ResultOf.Success -> {
                        _stockdata.postValue(result.value!!)
                    }
                    is ResultOf.Loading -> _dataLoading.postValue(true)
                    is ResultOf.Error -> _dataLoading.postValue(false)
                }
            }
        }
    }
}
