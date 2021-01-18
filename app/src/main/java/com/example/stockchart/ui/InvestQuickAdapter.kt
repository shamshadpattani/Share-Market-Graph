package com.example.stockchart.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.stockchart.R
import com.example.stockchart.data.model.MyInvest

class InvestQuickAdapter(data: MutableList<MyInvest>) : BaseQuickAdapter<MyInvest, BaseViewHolder>(R.layout.list_my_stock, data as MutableList<MyInvest>?)  {
    override fun convert(holder: BaseViewHolder, item: MyInvest) {
        TODO("Not yet implemented")
    }

}
