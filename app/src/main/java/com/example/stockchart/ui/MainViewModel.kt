package com.example.stockchart.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.Stock
import com.example.stockchart.data.repository.StockRepository
import com.example.stockchart.data.utlis.ResultOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val date = MutableLiveData<List<String>>()
    private val price = MutableLiveData<List<Double>>()

    val yesterdayDate : MutableLiveData<String> = MutableLiveData()
    val yesterdayPrice : MutableLiveData<String> = MutableLiveData()
    val priceIncYesterday : MutableLiveData<String> = MutableLiveData()
    val percentYest : MutableLiveData<String> = MutableLiveData()
    val percentIncValYest : MutableLiveData<Float> = MutableLiveData()

    val todaydate : MutableLiveData<String> = MutableLiveData()
    val todayprice : MutableLiveData<String> = MutableLiveData()
    val todayPriceIncr : MutableLiveData<String> = MutableLiveData()
    val todayPercentage : MutableLiveData<String> = MutableLiveData()
    val todayIncVal : MutableLiveData<Float> = MutableLiveData()



    var stockvalues = MutableLiveData<Pair<List<String>, List<Double>>> ()

    fun setData(data: List<List<Any>>, i: Int) {
        val _price:MutableList<Double> = mutableListOf()
        val _date:MutableList<String> = mutableListOf()
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

        yesterdayDate.value=dateConversionSrting(_date[1])!!
        yesterdayPrice.value="₹${_price[1]}"
        priceIncYesterday.value="₹${_price[1].minus(_price[2]).toFloat()}"
        percentYest.value="%${String.format("%.3f",((_price[1].minus(_price[2]).toFloat()).div(_price[2])).times(100))}"
        percentIncValYest.value=_price[1].minus(_price[2]).toFloat()

        todaydate.value = dateConversionSrting(_date[0])!!
        todayprice.value="₹${_price[0]}"
        todayPriceIncr.value="₹${_price[0].minus(_price[1]).toFloat()}"
        todayPercentage.value="%${String.format("%.3f",((_price[0].minus(_price[1]).toFloat()).div(_price[1])).times(100))}"
        todayIncVal.value=_price[0].minus(_price[1]).toFloat()

        stockvalues.value= Pair(dateConversion(_date.reversed()),_price.reversed())


        var counts = 0
        _fundDetails.value?.dataset?.data?.map { values ->
            counts += 1
            var info: Info? =null
            if (count <= _fundDetails.value!!.dataset.data.size) {
                info = Info(values[0] as String, values[1] as Double)
            }
            viewModelScope.launch(Dispatchers.IO) {
                val i = info?.let { stockRepo.savedbdata(it) }
            }
        }
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
    private fun dateConversionSrting(date:String): String? {
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
        when {
            text.toString()=="5D" -> {
                setData(dat,5)
            }
            text.toString()=="1M" -> {
                setData(dat,31)
            }
            text.toString()=="3M" -> {
                setData(dat,90)
            }
            text.toString()=="1Y" -> {
                setData(dat,360)
            }
            text.toString()=="2Y" -> {
                setData(dat,360*2)
            }
            text.toString()=="3Y" -> {
                setData(dat,360*3)
            }
            else -> {
                setData(dat,36000)
            }
        }
    }

}
