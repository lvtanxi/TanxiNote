package com.lv.note.util

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.lv.note.R
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

fun Activity.openNewAct(mClass:Class<*>,tagView:View){
    LAnimUtils.startActivityAsCircular(this,mClass,tagView, R.color.brown)
}
fun Activity.openNewAct(mIntent:Intent,tagView:View){
    LAnimUtils.startActivityAsCircular(this,mIntent,tagView, R.color.brown)
}


fun Activity.openNewAct(mClass:Class<*>,tagView:View,requestCode:Int){
    LAnimUtils.startActivityForResultAsCircular(this,Intent(this, mClass),requestCode,tagView, R.color.brown)
}

fun Activity.openNewAct(mIntent:Intent,tagView:View,requestCode:Int){
    LAnimUtils.startActivityForResultAsCircular(this,mIntent,requestCode,tagView, R.color.brown)
}

