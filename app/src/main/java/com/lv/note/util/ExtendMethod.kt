package com.lv.note.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
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

fun Activity.openNewAct(mClass:Class<*>){
    startActivity(Intent(this, mClass), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
}
fun Activity.openNewAct(mIntent:Intent,mView:View,name:String){
    startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(this,mView,name).toBundle())
}

fun Activity.openNewAct(mIntent:Intent){
    startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
}

fun Activity.openNewAct(mClass:Class<*>,code:Int){
    startActivityForResult(Intent(this, mClass),code, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
}

fun Activity.openNewAct(mIntent:Intent,code:Int){
    startActivityForResult(mIntent,code, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
}

