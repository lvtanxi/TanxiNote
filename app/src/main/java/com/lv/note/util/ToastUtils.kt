package com.lv.note.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.lv.note.R
import com.lv.test.StrUtils

/**
* User: 吕勇 
* Date: 2016-06-13 
* Time: 10:01
* Description:
*/  
class ToastUtils(context: Context) {
    private var layout: View? = null
    private var tv: TextView? = null
    private var mImageView: ImageView? = null

    init {
        mContext = context.applicationContext
    }

    companion object {
        private var mContext: Context?=null
        @Volatile private var mInstance: ToastUtils? = null
        private var mToast: Toast? = null

        /**
         * 单例模式

         * @param context 传入的上下文
         * *
         * @return TabToast实例
         */
        private fun getInstance(context: Context): ToastUtils {
            if (mInstance == null) {
                synchronized (ToastUtils::class.java) {
                    if (mInstance == null) {
                        mInstance = ToastUtils(context)
                    }
                }
            }
            return mInstance as ToastUtils
        }


        private fun getToast(duration: Int) {
            if (mToast == null) {
                mToast = Toast(mContext)
                mToast!!.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                mToast!!.duration = duration
            }
        }

        fun textToast(context: Context, text: String) {
            if (!StrUtils.isEmpty(text))
                textToast(context, text, 0, Toast.LENGTH_SHORT)
        }

        fun textToastError(context: Context, text: String) {
            textToast(context, text, R.drawable.toast_error)
        }

        fun textToast(context: Context, text: String, imageId: Int) {
            if (!StrUtils.isEmpty(text))
                textToast(context, text, imageId, Toast.LENGTH_SHORT)
        }


        fun textToast(context: Context, text: String, resId: Int, duration: Int) {
            var resId = resId
            getInstance(context)
            getToast(duration)
            if (mInstance!!.layout == null || mInstance!!.tv == null) {
                mInstance!!.layout = LayoutInflater.from(mContext).inflate(R.layout.toast_layout, null)
                mInstance!!.tv = mInstance!!.layout!!.findViewById(R.id.toast_text) as TextView
                mInstance!!.mImageView = mInstance!!.layout!!.findViewById(R.id.toast_image) as ImageView
                mToast!!.view = mInstance!!.layout
            }
            mInstance!!.tv!!.text = text
            if (resId == 0)
                resId = R.drawable.toast_success
            mInstance!!.mImageView!!.setImageResource(resId)
            mToast!!.show()
        }
    }
}
