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
 * Date: 2016-06-29
 * Time: 09:07
 * Description:
 */
class LSpeechSynthesizer(private val context: Context,private val baseView:IBaseView){

    private var mSpeechSynthesizer: SpeechSynthesizer? = null
    private var mProgressDialog: ColorfulRingProgressDialog? = null
    private var isPauseSpeaking=false

    fun init(): LSpeechSynthesizer {
        if (mSpeechSynthesizer == null) {
            mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, null)
            mSpeechSynthesizer!!.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan")//设置发音人
            mSpeechSynthesizer!!.setParameter(SpeechConstant.SPEED, "30")//设置语速
            mSpeechSynthesizer!!.setParameter(SpeechConstant.VOLUME, "50")//设置音量，范围0~100
            mSpeechSynthesizer!!.setParameter(SpeechConstant.PITCH, "50")
            mSpeechSynthesizer!!.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD) //设置云端
            mProgressDialog = ColorfulRingProgressDialog(context)
            mProgressDialog!!.setOnDismissListener {
                pauseSpeaking()
            }
        }
        return this
    }


    fun speak(text: String) {
        isPauseSpeaking=false
        baseView.showLodingView()
        mSpeechSynthesizer!!.startSpeaking(text, object : SynthesizerListener {
            override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {
                if(isPauseSpeaking)
                    pauseSpeaking()
                mProgressDialog!!.setPercent(p0)
            }

            override fun onCompleted(p0: SpeechError?) {
                if (mProgressDialog != null && mProgressDialog!!.isShowing)
                    mProgressDialog!!.dismiss()
            }

            override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {
            }

            override fun onSpeakBegin() {
                baseView.hideLodingView()
                if (!isPauseSpeaking&&mProgressDialog != null && !mProgressDialog!!.isShowing)
                    mProgressDialog!!.show()
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
            }

            override fun onSpeakPaused() {
            }

            override fun onSpeakResumed() {
            }

        })
    }

    fun resumeSpeaking(){
        if(isPauseSpeaking)
            mSpeechSynthesizer!!.resumeSpeaking()
        isPauseSpeaking=false
    }

    fun pauseSpeaking(){
        isPauseSpeaking=true
        if(mSpeechSynthesizer!!.isSpeaking){
            mSpeechSynthesizer!!.pauseSpeaking()
        }
    }


    fun cancel() {
        mProgressDialog =null
        mSpeechSynthesizer!!.stopSpeaking()
        mSpeechSynthesizer!!.destroy()
        mSpeechSynthesizer =null
    }

}
