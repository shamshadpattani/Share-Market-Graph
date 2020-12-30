package com.example.stockchart.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observer()
        setAdjustScreen();
        inits()
    }

    private fun inits() {
        selectorGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->

            val listenerButton: MaterialButton = group.findViewById(checkedId)
            val checkedButton: MaterialButton? = group.findViewById(group.checkedButtonId)

            if (checkedButton != null) {
                if (!isChecked) {
                    Toast.makeText(this, "cheked${checkedButton.text}", Toast.LENGTH_SHORT).show()
                    mainViewModel.setvalues(checkedButton.text.toString(),dat)
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
        mainViewModel.fundDetails.observe(this, Observer { stock ->
            showTitles(stock.dataset)
            dat=stock.dataset.data
            setData(dat)
        })
        mainViewModel.stockvalues.observe(this, Observer { values ->
            setmap(values.second, values.first)
        })
    }

    private fun setData(data: List<List<Any>>) {
        mainViewModel.setData(data,5)
    }


    private fun setmap(dataset: List<Double>, date: List<String>) {
            aaChartModel.chartType(AAChartType.Areaspline)
            .backgroundColor("#1c232e")
            .xAxisTickInterval(500)
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
                .titleStyle(AAStyle().color("#810000").fontWeight(AAChartFontWeightType.Bold))
                .subtitle(data.description)
    }
}