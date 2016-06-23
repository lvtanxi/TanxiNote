package com.lv.note.helper

import cn.bmob.v3.listener.UpdateListener
import com.lv.note.util.CommonUtils


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 13:12
 * Description:
 */
abstract class UpdateListenerSub@JvmOverloads constructor(private var mBaseView: IBaseView, private var mShowLodingView: Boolean = true) : UpdateListener() {

    override fun onStart() {
        if (mShowLodingView)
            mBaseView.showLodingView()
    }


    override fun onFailure(p0: Int, p1: String?) {
        mBaseView.toastError(CommonUtils.getErrorMessage(p0))
        onFinish()
    }

    override fun onFinish() {
        if (mShowLodingView)
            mBaseView.hideLodingView()
    }

}