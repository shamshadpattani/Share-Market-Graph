package com.example.stockchart.utlis

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.google.android.material.textfield.TextInputEditText

class MultiLineImeEditText: TextInputEditText { constructor(context: Context):super(context) {}
    constructor(context: Context, attrs: AttributeSet):super(context,attrs) {}
    constructor(context: Context,attrs: AttributeSet,defStyle: Int):super(context,attrs,defStyle) {
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val conn = super.onCreateInputConnection(outAttrs)
        outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        return conn!!
    }
}