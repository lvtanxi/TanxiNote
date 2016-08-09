package com.lv.note.helper

import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.lv.note.util.CommonUtils


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 12:54
 * Description:
 */
abstract class FindListenerSub<T> @JvmOverloads constructor(private var mBaseView: IBaseView, private var mShowLodingView: Boolean = true) : FindListener<T>() {
    override fun done(p0: MutableList<T>?, p1: BmobException?) {
        if (p1 != null)
            mBaseView.toastError(CommonUtils.getErrorMessage(p1.errorCode))
        else
            onSuccess(p0!!)
        onFinish()
    }

    override fun onStart() {
        if (mShowLodingView)
            mBaseView.showLodingView()
    }

    abstract fun onSuccess(result: MutableList<T>)


    override fun onFinish() {
        if (mShowLodingView)
            mBaseView.hideLodingView()
    }
}