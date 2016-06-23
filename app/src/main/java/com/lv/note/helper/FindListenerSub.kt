package com.lv.note.helper

import cn.bmob.v3.listener.FindListener
import com.lv.note.util.CommonUtils


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 12:54
 * Description:
 */
abstract class FindListenerSub<T> @JvmOverloads constructor(private var mBaseView: IBaseView, private var mShowLodingView: Boolean = true) : FindListener<T>() {

    override fun onStart() {
        if (mShowLodingView)
            mBaseView.showLodingView()
    }

    override fun onError(p0: Int, p1: String?) {
        mBaseView.toastError(CommonUtils.getErrorMessage(p0))
        onFinish()
    }

    override fun onFinish() {
        if (mShowLodingView)
            mBaseView.hideLodingView()
    }
}