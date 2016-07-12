package com.lv.note.util

import android.graphics.Color
import android.view.View
import com.lv.note.base.BaseActivity


/**
 * User: 吕勇
 * Date: 2016-07-07
 * Time: 09:53
 * Description:
 */

fun String?.isEmptyStr(): Boolean {
    return this == null || this.isEmpty()
}

fun String?.notEmptyStr(): Boolean {
    return this != null && !this.isEmpty()
}

fun List<*>?.isEmptyList():Boolean{
    return this == null || this.isEmpty()
}

fun List<*>?.notEmptyList():Boolean{
    return this != null || !this!!.isEmpty()
}

fun BaseActivity.changeTopBgColor(){
    if (android.os.Build.VERSION.SDK_INT >= 21) {
        window.statusBarColor=Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
