package com.example.stockchart.ui

import android.app.Application
import androidx.lifecycle.*
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.MyInvest
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.model.Stock
import com.example.stockchart.data.repository.StockRepository
import com.example.stockchart.data.utlis.ResultOf
import com.example.stockchart.utlis.CommonUtils.dateConversion
import com.example.stockchart.utlis.CommonUtils.dateConversionSrting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val date = MutableLiveData<List<String>>()
   val price = MutableLiveData<List<Double>>()

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

    val totalInvest : MutableLiveData<String> = MutableLiveData()
    val totalUnit : MutableLiveData<String> = MutableLiveData()
    val totalProfit : MutableLiveData<String> = MutableLiveData()
    val totalAmount : MutableLiveData<String> = MutableLiveData()
    val todaydateMap : MutableLiveData<String> = MutableLiveData()
    val myInvestDB : LiveData<List<MyInvestDB>> = stockRepo.getsaveInvestLive()


    var my_price : MutableLiveData<String> = MutableLiveData()
    var price_diff : MutableLiveData<String> = MutableLiveData()

    var stockvalues = MutableLiveData<Pair<List<String>, List<Double>>> ()

    private val _myInvestListData = MutableLiveData<List<MyInvest>>()
    val myInvestListData:LiveData<List<MyInvest>> =_myInvestListData

    /**
     * every time myInvestDB updated
     * it call stockRepo.getPriceFromDBLive()
     * [return] livedata
    **/

    private  val _liveTodayPrice = Transformations.switchMap(myInvestDB) {
        stockRepo.getPriceFromDBLive()
    }
    val liveTodayPrice: LiveData<Double> = _liveTodayPrice



    fun updateInvestList(todayRate: Double) {
        totalAmount.value=0.0.toString()
        totalProfit.value=0.0.toString()
        totalInvest.value=0.0.toString()
        totalUnit.value=0.0.toString()
        var inv:MutableList<MyInvest> = mutableListOf()
        myInvestDB.value?.map { myInv ->
            my_price.value = String.format("%.3f", todayRate.times(myInv.unit.toDouble()))
            price_diff.value = String.format("%.2f", myInv.invest_price.toDouble().let { my_price.value!!.toDouble().minus(it) })

            totalAmount.value = String.format("%.3f",my_price.value!!.toDouble().plus(totalAmount.value?.toDouble()!!))
            totalUnit.value = String.format("%.3f",totalUnit.value!!.toDouble().plus(myInv.unit.toDouble()))
            totalProfit.value = String.format("%.3f",totalProfit.value!!.toDouble().plus(price_diff.value!!.toDouble()))
            totalInvest.value=String.format("%.2f",totalInvest.value!!.toDouble().plus(myInv.invest_price.toDouble()))

          inv.add(MyInvest(my_price = my_price.value!!,
                    invest_price = myInv.invest_price,
                    invest_date = myInv.invest_date,
                    unit = myInv.unit, nav = myInv.nav.toString(),
                    price_diff = price_diff.value))
        }
        _myInvestListData.postValue(inv)
    }
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
        priceIncYesterday.value="₹${String.format("%.2f",_price[1].minus(_price[2]).toFloat())}"
        percentYest.value="${String.format("%.2f",((_price[1].minus(_price[2]).toFloat()).div(_price[2])).times(100))}%"
        percentIncValYest.value=_price[1].minus(_price[2]).toFloat()

        todaydate.value = dateConversionSrting(_date[0])!!
        todaydateMap.value="As On ${todaydate.value}"
        todayprice.value="₹${_price[0]}"
        todayPriceIncr.value="₹${String.format("%.2f",_price[0].minus(_price[1]).toFloat())}"
        todayPercentage.value="${String.format("%.2f",((_price[0].minus(_price[1]).toFloat()).div(_price[1])).times(100))}%"
        todayIncVal.value=_price[0].minus(_price[1]).toFloat()

        stockvalues.value= Pair(dateConversion(_date.reversed()),_price.reversed())

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

    fun savetoDB(){
        var counts = 0
        var info: MutableList<Info?> = mutableListOf()
        _fundDetails.value?.dataset?.data?.map { values ->
            counts += 1
            if (counts <= _fundDetails.value!!.dataset.data.size) {
                info.add(Info(values[0] as String, values[1] as Double))
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            if(info.size!=0){
                val i = info.let { stockRepo.saveStockDetailsdata(it) }
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

    //Invest

    fun deleteInvest(pos: Int, holder: BaseViewHolder) {
        var id= myInvestDB.value?.asReversed()?.get(pos)?.id
        viewModelScope.launch(Dispatchers.IO) {
            stockRepo.deleteInvestData(id)
        }
    }


}
