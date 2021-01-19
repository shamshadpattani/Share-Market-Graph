package com.example.stockchart.ui

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.R
import com.example.stockchart.data.model.MyInvest
import com.example.stockchart.data.model.MyInvestDB

class InvestQuickAdapter(data: MutableList<MyInvest>) : BaseQuickAdapter<MyInvest, BaseViewHolder>(R.layout.list_my_stock, data as MutableList<MyInvest>?)  {
    override fun convert(holder: BaseViewHolder, item: MyInvest) {
        holder.setText(R.id.invest_price,"₹"+item.invest_price)
            .setText(R.id.unit_count,item.unit)
            .setText(R.id.date_text,item.invest_date)
            .setText(R.id.nav_price,"₹"+item.nav.toString())
            .setText(R.id.new_price,"₹"+item.my_price.toString())
            .setText(R.id.price_diff,"₹"+item.price_diff.toString())

        if(item.invest_price?.toDouble()!! > item.my_price?.toDouble()!!){
            (holder.getView(R.id.new_price) as TextView).setTextColor(context.getColor(R.color.negative_color))
        }else{
            (holder.getView(R.id.new_price) as TextView).setTextColor(context.getColor(R.color.light_green_color))
        }

        if(item.price_diff<0){
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

}
