package com.example.stockchart.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stockchart.R
import com.example.stockchart.data.model.Dataset
import com.example.stockchart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
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

    @SuppressLint("SetTextI18n")
    private fun observer() {
        mainViewModel.stockdata.observe(this, Observer { stock ->
            showTitles(stock.dataset)
            setData(stock.dataset.data)
        })
        mainViewModel.stockvalues.observe(this, Observer { values ->
            setmap(values.second, values.first)
            binding.latestPrice.text = "LAST UPDATE:${values.first.last()} -  ₹${values.second.last()}"
            binding.latestPrice.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
        })
    }

    private fun setData(data: List<List<Any>>) {
        mainViewModel.setData(data)
    }


    private fun setmap(dataset: List<Double>, date: List<String>) {
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Areaspline)
            .title("TATA")
            .subtitle("")
            .backgroundColor("#00000")
            .xAxisTickInterval(100)
            .borderRadius(0f)
            .yAxisGridLineWidth(0.001f)
            .gradientColorEnable(false)
            .markerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .categories(date.toTypedArray())
            .dataLabelsEnabled(false)
            .animationDuration(2000)
            .animationType(AAChartAnimationType.EaseInSine)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("aa")
                        .data(dataset.toTypedArray())
                        .color("#85c785")
                        .fillOpacity(0.12f)
                        .borderWidth(0f)
                        .enableMouseTracking(true)
                        .dashStyle(AAChartLineDashStyleType.Solid)
                )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun showTitles(data: Dataset) {
        binding.mainTitle.text = data.name
    }
}