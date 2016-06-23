package com.lv.note.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class Focusedtrue4TV(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    override fun isFocused(): Boolean {
        return true
    }

}