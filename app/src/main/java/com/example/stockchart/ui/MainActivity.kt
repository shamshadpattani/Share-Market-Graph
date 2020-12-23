package com.example.stockchart.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stockchart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observer()
    }

    private fun observer() {
        mainViewModel.stockdata.observe(this, Observer { stock->
            showTitles(stock.dataset.name)
            setData(stock.dataset.data)
        })
        mainViewModel.stockvalues.observe(this, Observer { values->
            setmap(values.second,values.first)
        })
    }

    private fun setData(data: List<List<Any>>) {
      mainViewModel.setData(data)
    }


    private fun setmap(dataset:List<Double>, date:List<String>) {
        val aaChartModel : AAChartModel = AAChartModel()
        .chartType(AAChartType.Line)
                .title("title")
                .subtitle("subtitle")
                .backgroundColor("#00000")
                .categories(date.toTypedArray())
                .dataLabelsEnabled(false)
                .series(arrayOf(
                        AASeriesElement()
                                .name("aa")
                                .data(dataset.toTypedArray())
                )
                )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun showTitles(name: String) {
        binding.mainTitle.text=name
    }

}