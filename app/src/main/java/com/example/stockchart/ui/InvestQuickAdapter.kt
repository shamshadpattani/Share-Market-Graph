package com.example.stockchart.ui

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.R
import com.example.stockchart.data.model.MyInvest
import com.example.stockchart.data.model.MyInvestDB
import com.example.stockchart.databinding.ListMyStockBinding
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate

class InvestQuickAdapter(data: MutableList<MyInvest>)
    : BaseQuickAdapter<MyInvest, BaseDataBindingHolder<ListMyStockBinding>>(R.layout.list_my_stock, data) , DraggableModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ListMyStockBinding>, item: MyInvest) {

        holder.dataBinding?.apply {
            invest = item
            investPrice.text= "₹${item.invest_price}"
            priceDiff.text= "₹${item.price_diff}"
            newPrice.text= "₹${item.my_price}"
            dateText.text=item.invest_date
            unitCount.text=item.unit
            navPrice.text="₹${item.nav}"
            val dates = SimpleDateFormat("yyyy-MM-dd")
             val date=dates.parse(item.invest_date)
            val date2=dates.parse(LocalDate.now().toString())
            val difference: Long = abs(date2.time - date.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            val dayDifference = differenceDates.toString()
            daysDiff.text="$dayDifference Days"
        }
        if(item.invest_price?.toDouble()!! > item.my_price?.toDouble()!!){
            (holder.getView(R.id.new_price) as TextView).setTextColor(context.getColor(R.color.negative_color))
        }else{
            (holder.getView(R.id.new_price) as TextView).setTextColor(context.getColor(R.color.light_green_color))
        }

        if(item.price_diff!!<0.toString()){
            (holder.getView(R.id.price_diff) as TextView).setTextColor(context.getColor(R.color.negative_color))
        }else{
            (holder.getView(R.id.price_diff) as TextView).setTextColor(context.getColor(R.color.light_green_color))
        }
    }


    fun updateItems(newItems: MutableList<MyInvest>?) {
        this.data.clear()
        newItems?.let { this.data.addAll(it) }
        notifyDataSetChanged()
    }

    class DiffCallback: DiffUtil.ItemCallback<MyInvest>() {
        override fun areItemsTheSame(oldItem: MyInvest, newItem: MyInvest): Boolean {
            return oldItem.invest_price == newItem.invest_price && oldItem.invest_date == newItem.invest_date
        }
        override fun areContentsTheSame(oldItem: MyInvest, newItem: MyInvest): Boolean {
            return false
        }
    }


}
