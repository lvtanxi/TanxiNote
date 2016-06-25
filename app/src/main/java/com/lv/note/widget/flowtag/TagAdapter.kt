package com.lv.note.widget.flowtag

import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by zhy on 16/5/16.
 */
abstract class TagAdapter<T> {
    private var mDatas: List<T>? = null

    constructor(datas: List<T>?) {
        var datas = datas
        if (datas == null)
            datas = ArrayList<T>(0)
        mDatas = datas
    }

    constructor(datas: Array<T>) {
        mDatas = ArrayList(Arrays.asList(*datas))
    }

    abstract fun getView(parent: ViewGroup, position: Int, t: T): View

    /**
     * 传入position，如果返回true则认为默认选中

     * @param position
     * *
     * @return
     */
     fun  select(position: Int): Boolean {
        return false
    }

    /**
     * 未选中->选中，可以在这里设置样式
     */
    open fun onSelect(parent: ViewGroup, view: View, position: Int) {
    }

    /**
     * 选中->未选中，可以在这里设置样式
     */
    open fun onUnSelect(parent: ViewGroup, view: View, position: Int) {
    }


    fun enabled(position: Int): Boolean {
        return true
    }


    fun getItem(position: Int): T {
        return mDatas!![position]
    }

    val count: Int
        get() = mDatas!!.size

    fun notifyDataSetChanged() {
        if (mOnAdapterDataChanged != null) {
            mOnAdapterDataChanged!!.onChange()
        }
    }

    private var mOnAdapterDataChanged: OnAdapterDataChanged? = null

    internal fun setOnAdapterDataChanged(onAdapterDataChanged: OnAdapterDataChanged) {
        mOnAdapterDataChanged = onAdapterDataChanged
    }

    internal interface OnAdapterDataChanged {
        fun onChange()
    }

}
