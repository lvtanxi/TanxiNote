package com.lv.note.util

import android.content.Context
import android.os.Bundle
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SpeechSynthesizer
import com.iflytek.cloud.SynthesizerListener
import com.lv.note.helper.IBaseView
import com.lv.note.widget.progressview.ColorfulRingProgressDialog


/**
 * User: 吕勇
 * Date: 2016-06-27
 * Time: 16:48
 * Description:
 */
object SpeechSynthesizerUtils {
    private var mSpeechSynthesizer: SpeechSynthesizer? = null
    private var mProgressDialog: ColorfulRingProgressDialog? = null
    fun initSpeechSynthesizer(context: Context): SpeechSynthesizerUtils {
        if (mSpeechSynthesizer == null) {
            mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context.applicationContext, null)
            mSpeechSynthesizer!!.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan")//设置发音人
            mSpeechSynthesizer!!.setParameter(SpeechConstant.SPEED, "30")//设置语速
            mSpeechSynthesizer!!.setParameter(SpeechConstant.VOLUME, "50")//设置音量，范围0~100
            mSpeechSynthesizer!!.setParameter(SpeechConstant.PITCH, "50")
            mSpeechSynthesizer!!.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD) //设置云端
            mProgressDialog = ColorfulRingProgressDialog(context)
            mProgressDialog!!.setOnDismissListener {
                mSpeechSynthesizer!!.pauseSpeaking()
            }
        }
        return this
    }

    fun speechSynthesizer(baseView:IBaseView,text: String) {
        baseView.showLodingView()
        mSpeechSynthesizer!!.startSpeaking(text, object : SynthesizerListener {
            override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {
                mProgressDialog?.setPercent(p0)
            }

            override fun onCompleted(p0: SpeechError?) {
                if (mProgressDialog != null && mProgressDialog!!.isShowing)
                    mProgressDialog!!.dismiss()
            }

            override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {
            }

            override fun onSpeakBegin() {
                baseView.hideLodingView()
                if (mProgressDialog != null && !mProgressDialog!!.isShowing)
                    mProgressDialog!!.show()
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
                baseView.hideLodingView()
            }

            override fun onSpeakPaused() {
            }

            override fun onSpeakResumed() {
            }

        })
    }

    fun cancel() {
        mProgressDialog=null
        mSpeechSynthesizer=null
        mSpeechSynthesizer?.stopSpeaking()
        mSpeechSynthesizer?.destroy()
    }
}