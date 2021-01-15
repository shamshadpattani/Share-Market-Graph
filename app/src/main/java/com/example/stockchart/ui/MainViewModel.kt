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

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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

    val amount : MutableLiveData<String> = MutableLiveData()
    val seletedDate : MutableLiveData<LocalDate> = MutableLiveData()

    var stockvalues = MutableLiveData<Pair<List<String>, List<Double>>> ()

    fun setData(data: List<List<Any>>, i: Int) {
        amount.value="120"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            seletedDate.value= LocalDate.now()
        }
        val _price:MutableList<Double> = mutableListOf()
        val _date:MutableList<String> = mutableListOf()
        var count=0
        data.map { values->
            count += 1
            if(count <= i){
                _date.add(values[0] as String)
                _price.add(values[1] as Double)
            }
            savetoDb(dateConversion(_date.reversed()),_price)
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

    }

    private fun savetoDb(
        date: List<String>,
        price: MutableList<Double>
    ) {
        var info:Info
        date.map { dt->
                price.map { pr ->
                        info = Info(dt, pr)
                        viewModelScope.launch(Dispatchers.Default) {
                            val i= stockRepo.savedbdata(info)
                        }
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
