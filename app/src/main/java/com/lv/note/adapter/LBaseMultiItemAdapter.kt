package com.lv.note.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.lv.note.entity.LMultiItem
import com.lv.note.util.isEmptyList

/**
 * User: 吕勇
 * Date: 2016-07-29
 * Time: 12:54
 * Description:多布局Adapter
 */
abstract class LBaseMultiItemAdapter<T : LMultiItem> @JvmOverloads constructor(protected var addClickToHeaderLayout: Boolean = false) : LBaseAdapter<T>() {


    protected var layouts: SparseArray<Int>? = null

    init {
        addMultiItem()
    }



    override fun getDefItemViewType(position: Int): Int {
        return mDatas[position].itemType
    }


    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return createBaseViewHolder(parent, getLayoutId(viewType))
    }

    private fun getLayoutId(viewType: Int): Int {
        return layouts!!.get(viewType)
    }

    protected fun addItmeType(type: Int, layoutResId: Int) {
        if (layouts == null)
            layouts = SparseArray<Int>()
        layouts!!.put(type, layoutResId)
    }

    override fun onBindItem(itemView: View, realPosition: Int, item: T) {
        if (getItemViewType(realPosition) == SECTION_HEADER_VIEW ) {
            if (addClickToHeaderLayout) {
                itemView.setOnClickListener { view -> onItemClick(view, getItem(realPosition)) }
            }
            onBindHeaderItem(itemView, realPosition, item)
            return
        }
        onBindNormalItem(itemView, realPosition, item)
    }

    override fun getSpanCount(position: Int, defCount: Int): Int {
        return if (getItemViewType(position) == SECTION_HEADER_VIEW || isHeaderView(position) || isBottomView(position) || mDatas.isEmptyList()) defCount else 1
    }


    /**
     * 调用addItmeType添加多布局
     */
    protected abstract fun addMultiItem()

    protected open fun onBindHeaderItem(itemView: View, realPosition: Int, item: T) {

    }

    protected open fun onBindNormalItem(itemView: View, realPosition: Int, item: T) {

    }

    companion object {
        val SECTION_HEADER_VIEW = 0x00000444
    }


}
