package com.lv.note.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle

import com.lv.note.R


/**
 * User: 吕勇
 * Date: 2016-03-17
 * Time: 17:40
 * Description:加载对话框
 */
class LoadingDialog(context: Context, themeResId: Int = R.style.loading_dialog_style) : Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)
        setCanceledOnTouchOutside(false)
    }
}
