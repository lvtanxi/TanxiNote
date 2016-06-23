package com.lv.note.util

import android.app.Activity
import android.graphics.Color
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView

/**
 * User: 吕勇
 * Date: 2016-05-06
 * Time: 14:06
 * Description:定时器
 */
class CountDown @JvmOverloads constructor(millisInFuture: Long, private val mCountDownInterval: Long = 1000) : CountDownTimer(millisInFuture, mCountDownInterval) {
    private var view: View? = null
    private var finishStr: String? = null
    private var tickStr: String? = null
    private var finshAct: Boolean = false
    private var activity: Activity? = null
    private var downBack: CountDownBack? = null

    fun setView(view: View): CountDown {
        this.view = view
        return this
    }

    fun setFinishStr(finishStr: String): CountDown {
        this.finishStr = finishStr
        return this
    }

    fun setTickStr(tickStr: String): CountDown {
        this.tickStr = tickStr
        return this
    }

    fun setFinshAct(finshAct: Boolean): CountDown {
        this.finshAct = finshAct
        return this
    }

    fun setActivity(activity: Activity): CountDown {
        this.activity = activity
        return this
    }

    fun resetData() {
        cancel()
        setText()
    }

    fun setDownBack(downBack: CountDownBack?): CountDown {
        this.downBack = downBack
        return this
    }

    override fun onTick(millisUntilFinished: Long) {
        if (null != view && view is TextView && tickStr != null) {
            view!!.isEnabled = false
        }
    }

    override fun onFinish() {
        setText()
        if (null != downBack)
            downBack!!.countDownFinish()
        if (finshAct && activity != null)
            activity!!.finish()
    }

    private fun setText() {
        if (null != view && view is TextView && finishStr != null) {
            (view as TextView).text = finishStr
            (view as TextView).setTextColor(Color.WHITE)
            view!!.isEnabled = true
        }
    }

    interface CountDownBack {
        fun countDownFinish()
    }

}
