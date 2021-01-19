package com.example.stockchart.ui

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.example.stockchart.R
import com.example.stockchart.data.model.Dataset
import com.example.stockchart.data.model.MyInvest
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.data.room.EntityDescriptions
import com.example.stockchart.data.room.StockDatabase
import com.example.stockchart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.android.material.button.MaterialButton
import com.wajahatkarim3.roomexplorer.RoomExplorer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
     private val updateInvest: MutableList<MyInvest> = mutableListOf()
    private val invest: MutableList<MyInvestDB> = mutableListOf()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var investViewModel: InvestViewModel

    private lateinit var binding: ActivityMainBinding
    private val aaChartModel: AAChartModel = AAChartModel()
    private lateinit var dat: List<List<Any>>
    private lateinit var mAdapter: InvestQuickAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        investViewModel = ViewModelProvider(this).get(InvestViewModel::class.java)
        binding.stock = mainViewModel
        binding.lifecycleOwner = this
        observer()
        setAdjustScreen();
        init()
        initAdapter()
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        mAdapter = InvestQuickAdapter(mutableListOf())
        recycler_view.adapter = mAdapter
        mAdapter.apply {
            animationEnable = false
            adapterAnimation = SlideInBottomAnimation()
            isAnimationFirstOnly = true

        }
    }

    private fun showAddDialog() {
        val dialog = AddInvestFragment.newInstances()
        dialog.isCancelable = true
        dialog.show(this.supportFragmentManager, "TAG")
    }


    private fun init() {
        selectorGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton: MaterialButton? = group.findViewById(group.checkedButtonId)
            if (checkedButton != null) {
                if (isChecked) {
                    Toast.makeText(this, "checked${checkedButton.text}", Toast.LENGTH_SHORT).show()
                    mainViewModel.setvalues(checkedButton.text.toString(),dat)
                }else{

                }
            }
        }
        fab_add.setOnClickListener {
            showAddDialog()
        }
        initDebugTools()
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
        investViewModel.myInvestDB.observe(this, Observer { myInvest->
            //updateInvestList(myInvest as List<MyInvestDB>)
            invest.addAll(myInvest)
        })
        mainViewModel.todaypriceInDec.observe(this, Observer {
            updateInvestList(invest,it)
        })
    }

    private fun updateInvestList(myInvestDB: List<MyInvestDB>, d: Double) {
        myInvestDB.map { myInv ->
            val my_price = d.times(myInv.unit.toDouble())
            val price_diff= myInv.invest_price!!.toDouble()?.let { my_price.minus(it) }
            val inv= MyInvest(my_price =my_price,invest_price = myInv.invest_price,invest_date = myInv.invest_date,
                    unit = myInv.unit,nav = myInv.nav,price_diff = price_diff)
            updateInvest?.add(inv)
            mAdapter.updateItems(updateInvest)
        }
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

    private fun initDebugTools() {
        val isDebug: Boolean = this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (!isDebug) return


        room_btn.setOnClickListener {
            RoomExplorer.show(this, StockDatabase::class.java, EntityDescriptions.DB_NAME)
        }
    }
}