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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val stockRepo: StockRepository = StockRepository(getApplication())

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

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
