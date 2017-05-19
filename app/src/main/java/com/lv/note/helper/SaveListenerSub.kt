package com.lv.note.helper

import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.lv.note.util.CommonUtils


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 13:14
 * Description:
 */
abstract class SaveListenerSub@JvmOverloads constructor(private var mBaseView: IBaseView, private var mShowLodingView: Boolean = true) : SaveListener<String>() {
    init {
        if (mShowLodingView)
            mBaseView.showLodingView()
    }
    override fun done(p0: String?, p1: BmobException?) {
        if (p1 != null)
            mBaseView.toastError(CommonUtils.getErrorMessage(p1.errorCode))
        else
            onSuccess()
        onFinish()
    }


    abstract fun onSuccess()


    override fun onFinish() {
        if (mShowLodingView)
            mBaseView.hideLodingView()
    }
}