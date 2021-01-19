package com.example.stockchart.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.stockchart.data.model.Info
import com.example.stockchart.data.model.MyInvest
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

     val myInvest : LiveData<List<MyInvest>> = stockRepo.getsaveInvest()

    private fun getInvest() {
        val myIn: MyInvest? =
            MyInvest(
                nav = info?.price,
                invest_price = amount.value,
                invest_date = seletedDate.value.toString(),
                unit = unit.value.toString(),
                my_price = now_amount.value
            )
        val inv: MutableList<MyInvest> = mutableListOf()
        if (myIn != null) {
            inv.add(myIn)
            viewModelScope.launch(Dispatchers.IO) {
                stockRepo.saveInvest(myIn)
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
                now_amount.value = unit.value?.let { it1 -> info!!.price.times(it1?.toDouble()).toString() }
                getInvest()
            }
        }

    }

    fun discardChanges() {
        TODO("Not yet implemented")
    }
}

