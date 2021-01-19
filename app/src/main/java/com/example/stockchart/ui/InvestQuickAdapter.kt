package com.example.stockchart.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.R
import com.example.stockchart.data.model.MyInvest

class InvestQuickAdapter(data: MutableList<MyInvest>) : BaseQuickAdapter<MyInvest, BaseViewHolder>(R.layout.list_my_stock, data as MutableList<MyInvest>?)  {
    override fun convert(holder: BaseViewHolder, item: MyInvest) {
        holder.setText(R.id.invest_price,"₹"+item.invest_price)
            .setText(R.id.unit_count,item.unit)
            .setText(R.id.date_text,item.invest_date)
            .setText(R.id.nav_price,"₹"+item.nav.toString())
            .setText(R.id.new_price,"₹"+item.my_price)
    }

    fun updateItems(newItems: List<MyInvest>?) {
        this.data.clear()
        newItems?.let { this.data.addAll(it) }
        notifyDataSetChanged()
    }

}
