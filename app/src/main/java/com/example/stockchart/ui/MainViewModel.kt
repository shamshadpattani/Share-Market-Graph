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

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    val date = MutableLiveData<List<String>>()
    val price = MutableLiveData<List<Double>>()

    var stockvalues = MutableLiveData<Pair<List<String>, List<Double>>> ()

    fun setData(data: List<List<Any>>, i: Int) {
        var _price:MutableList<Double> = mutableListOf()
        var _date:MutableList<String> = mutableListOf()
        var count=0
        data.map { values->
            count += 1
            if(count <= i){
                _date.add(values[0] as String)
                _price.add(values[1] as Double)
            }
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

    private val _fundDetails = MutableLiveData<Stock>()
    val fundDetails: LiveData<Stock>
        get() {
            _fundDetails.value ?: fetchStockDetails()
            return _fundDetails
        }

    private fun fetchStockDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepo.getData().collect{ result ->
                when(result) {
                    is ResultOf.Success -> {
                        _fundDetails.postValue(result.value!!)
                    }
                    is ResultOf.Loading -> _dataLoading.postValue(true)
                    is ResultOf.Error -> _dataLoading.postValue(false)
                }
            }
        }
    }

    fun setvalues(text: String?, dat: List<List<Any>>) {
        if(text.toString()=="5D"){
            setData(dat,5)
        }else if(text.toString()=="1M"){
            setData(dat,31)
        }else if(text.toString()=="3M"){
            setData(dat,90)
        }else if(text.toString()=="1Y"){
            setData(dat,360)
        }else if(text.toString()=="2Y"){
            setData(dat,360*2)
        }else if(text.toString()=="3Y"){
            setData(dat,360*3)
        }else{
            setData(dat,36000)
        }
    }

    private fun setgraphValues(i: Int) {

    }
}
