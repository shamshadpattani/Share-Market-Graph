package com.example.stockchart.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class InvestViewModel(application: Application) : AndroidViewModel(application) {

    val amount: MutableLiveData<String> = MutableLiveData()
    val now_amount: MutableLiveData<String> = MutableLiveData()
    val unit: MutableLiveData<String> = MutableLiveData()
    val seletedDate: MutableLiveData<LocalDate> = MutableLiveData()
    var info: Info? = null
    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _todayprices= MutableLiveData<Double>()
    val todayprices: LiveData<Double> = _todayprices
    val myInvestDB : LiveData<List<MyInvestDB>> = stockRepo.getsaveInvest()

    private fun getInvest() {
        val myInDB: MyInvestDB? =
            MyInvestDB(
                nav = info?.price,
                invest_price = amount.value,
                invest_date = seletedDate.value.toString(),
                unit = unit.value.toString()
            )
        val inv: MutableList<MyInvestDB> = mutableListOf()
        if (myInDB != null) {
            inv.add(myInDB)
            viewModelScope.launch(Dispatchers.IO) {
                stockRepo.saveInvest(myInDB)
            }
        }
    }

    fun saveProblem() {
        val purchaseAmount = amount.value!!.toDouble()
        viewModelScope.launch(Dispatchers.IO) {
            info = stockRepo.getPriceFromDB(seletedDate.value.toString())
            withContext(Dispatchers.Main) {
                // call to UI thread
                unit.value = String.format("%.2f", (info?.price?.let { purchaseAmount.div(it) }))
                now_amount.value = unit.value?.let { it1 -> todayprices.value?.times(it1?.toDouble()).toString() }
                getInvest()
            }
        }

    }

    fun discardChanges() {
        TODO("Not yet implemented")
    }

    fun todaypricefun(second: Double) {
        _todayprices.value=second
    }
}

