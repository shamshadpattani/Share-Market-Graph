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

    val unit: MutableLiveData<String> = MutableLiveData()
    val seletedDate: MutableLiveData<LocalDate> = MutableLiveData()
    var info: Info? = null
    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _dataError = MutableLiveData<Boolean>()
    val dataError: LiveData<Boolean> = _dataError

    private val _savedata = MutableLiveData<Boolean>()
    val savedata: LiveData<Boolean> = _savedata

    private val _dismissDialog = MutableLiveData<Boolean>(false)
    val dismissDialog: LiveData<Boolean> = _dismissDialog


    private fun getInvest() {
        val myInDB: MyInvestDB? =
            MyInvestDB(
                nav = info!!.price,
                invest_price = amount.value!!,
                invest_date = seletedDate.value.toString(),
                unit = unit?.value.toString()
            )
        if (myInDB != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val i = stockRepo.saveInvest(myInDB)
                if(i!=null){
                    _savedata.postValue(true)
                }else{
                    _savedata.postValue(false)
                }
            }
        }
    }

    fun saveProblem() {
        val purchaseAmount = amount.value!!.toDouble()
        viewModelScope.launch(Dispatchers.IO) {
            info = stockRepo.getPriceFromDB(seletedDate.value.toString())
            withContext(Dispatchers.Main) {
                // call to UI thread
                if(info?.price!=null) {
                    unit.value = String.format("%.2f", (info?.price?.let { purchaseAmount.div(it) }))
                    getInvest()
                    _dismissDialog.postValue(true)
                }else{
                    unit.value ="No value found please select a better date"
                    _dataError.postValue(true)
                }
            }
        }

    }

    fun discardChanges() {
        _dismissDialog.postValue(true)
    }


}

