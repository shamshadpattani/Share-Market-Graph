package com.example.stockchart.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stockchart.R
import com.example.stockchart.data.model.Dataset
import com.example.stockchart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val aaChartModel: AAChartModel = AAChartModel()
    private lateinit var dat: List<List<Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.stock = mainViewModel
        binding.lifecycleOwner = this
        observer()
        setAdjustScreen();
        init()
        initAdapter()
    }

    private fun initAdapter() {


    }

    private fun init() {
        selectorGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            val listenerButton: MaterialButton = group.findViewById(checkedId)
            val checkedButton: MaterialButton? = group.findViewById(group.checkedButtonId)
            if (checkedButton != null) {
                if (isChecked) {
                    Toast.makeText(this, "cheked${checkedButton.text}", Toast.LENGTH_SHORT).show()
                    mainViewModel.setvalues(checkedButton.text.toString(),dat)
                }else{

                }
            }
        }
    }



    private fun setAdjustScreen() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        mainViewModel.fundDetails.observe(this, { stock ->
            showTitles(stock.dataset)
            dat=stock.dataset.data
            mainViewModel.setData(dat,5)
        })
        mainViewModel.stockvalues.observe(this, { values ->
            setmap(values.second, values.first)
        })
        mainViewModel.percentIncValYest.observe(this, Observer { py->
            if(py<0.0){
                percentYstrday.setTextColor(ContextCompat.getColor(this,R.color.negative_color))
                priceIncYday.setTextColor(ContextCompat.getColor(this,R.color.negative_color))
            }else{
                percentYstrday.setTextColor(ContextCompat.getColor(this,R.color.light_green_color))
                priceIncYday.setTextColor(ContextCompat.getColor(this,R.color.dark_green_color))
            }
        })
        mainViewModel.todayIncVal.observe(this, Observer { ty->
            if(ty<0.0){
                today_price_incr.setTextColor(ContextCompat.getColor(this,R.color.negative_color))
                today_percentage.setTextColor(ContextCompat.getColor(this,R.color.negative_color))
            }else{
                today_percentage.setTextColor(ContextCompat.getColor(this,R.color.light_green_color))
                today_price_incr.setTextColor(ContextCompat.getColor(this,R.color.dark_green_color))
            }
        })
    }

    private fun setmap(dataset: List<Double>, date: List<String>) {
            aaChartModel.chartType(AAChartType.Areaspline)
            .backgroundColor("#1c232e")
            .xAxisTickInterval(150)
            .borderRadius(0f)
            .yAxisGridLineWidth(0.001f)
            .gradientColorEnable(false)
            .markerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .categories(date.toTypedArray())
            .dataLabelsEnabled(false)
            .animationDuration(2000)
            .animationType(AAChartAnimationType.EaseInSine)
            .series(
                    arrayOf(AASeriesElement()
                            .data(dataset.toTypedArray())
                            .color("#85c785")
                            .fillOpacity(0.12f)
                            .borderWidth(0f)
                            .enableMouseTracking(true)
                            .dashStyle(AAChartLineDashStyleType.Solid))
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun showTitles(data: Dataset) {
        aaChartModel.title(data.name)
                .titleStyle(AAStyle().color("#FFFFFF").fontWeight(AAChartFontWeightType.Bold))
                .subtitle(data.description)
    }
}