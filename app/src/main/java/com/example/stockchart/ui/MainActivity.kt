package com.example.stockchart.ui

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.R
import com.example.stockchart.data.model.Dataset
import com.example.stockchart.data.model.MyInvest
import com.example.stockchart.data.room.EntityDescriptions
import com.example.stockchart.data.room.StockDatabase
import com.example.stockchart.databinding.ActivityMainBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.android.material.button.MaterialButton
import com.wajahatkarim3.roomexplorer.RoomExplorer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_my_stock.view.*


class MainActivity : AppCompatActivity() {

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
        initAdapter(mutableListOf())
        setAdjustScreen();
        init()
        observer()
    }

    private fun initAdapter(myInvest:List<MyInvest>) {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        mAdapter = InvestQuickAdapter(myInvest.toMutableList())
        recycler_view.adapter = mAdapter
        mAdapter.apply {
            animationEnable = true
            adapterAnimation = SlideInBottomAnimation()
            isAnimationFirstOnly = false
         //   setDiffCallback(InvestQuickAdapter.DiffCallback())
        }
        val onItemSwipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                val holder = viewHolder as BaseViewHolder
            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                val holder = viewHolder as BaseViewHolder
            }

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
                Log.d("TAG", "View Swiped: $pos")
                val holder = viewHolder as BaseViewHolder
                mainViewModel.deleteInvest(pos,holder)
                Toast.makeText(this@MainActivity, "Delete Successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSwipeMoving(canvas: Canvas, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {
                canvas.drawColor(ContextCompat.getColor(this@MainActivity, R.color.negative_color))
            }
        }

        mAdapter.draggableModule.isSwipeEnabled = true

        mAdapter.draggableModule.setOnItemSwipeListener(onItemSwipeListener)
        mAdapter.draggableModule.itemTouchHelperCallback.setSwipeMoveFlags(ItemTouchHelper.START)
    }

    private fun showAddDialog() {
        val dialog = AddInvestFragment.newInstances()
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "TAG")
    }


    private fun init() {
        selectorGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton: MaterialButton? = group.findViewById(group.checkedButtonId)
            if (checkedButton != null) {
                if (isChecked) {
                    mainViewModel.setvalues(checkedButton.text.toString(), dat)
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
          //  showTitles(stock.dataset)
            dat = stock.dataset.data
            mainViewModel.setData(dat, 5)
            mainViewModel.savetoDB()

        })

        mainViewModel.stockvalues.observe(this, { values ->
            setmap(values.second, values.first)
        })
        mainViewModel.percentIncValYest.observe(this, Observer { py ->
            if (py < 0.0) {
                percentYstrday.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
                priceIncYday.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
            } else {
                percentYstrday.setTextColor(ContextCompat.getColor(this, R.color.light_green_color))
                priceIncYday.setTextColor(ContextCompat.getColor(this, R.color.dark_green_color))
            }
        })

        mainViewModel.todayIncVal.observe(this, Observer { ty ->
            if (ty < 0.0) {
                today_price_incr.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
                today_percentage.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
            } else {
                today_percentage.setTextColor(ContextCompat.getColor(this, R.color.light_green_color))
                today_price_incr.setTextColor(ContextCompat.getColor(this, R.color.dark_green_color))
            }
        })
        mainViewModel.liveTodayPrice.observe(this, {
            if(it!=null){
                mainViewModel.updateInvestList(it)
            }
        })

        mainViewModel.myInvestListData.observe(this, {
            if(it.isNotEmpty())
              mAdapter.updateItems(it.toMutableList().asReversed())
           // mAdapter.setDiffNewData(it.toMutableList().asReversed())
        })

        mainViewModel.totalProfit.observe(this,{profit->
            if (profit.toDouble() < 0.0) {
                current_profit.setTextColor(ContextCompat.getColor(this, R.color.negative_color))
            } else {
                current_profit.setTextColor(ContextCompat.getColor(this, R.color.dark_green_color))
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




    private fun initDebugTools() {
        val isDebug: Boolean = this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (!isDebug) return

        room_btn.setOnClickListener {
            RoomExplorer.show(this, StockDatabase::class.java, EntityDescriptions.DB_NAME)
        }
    }
}