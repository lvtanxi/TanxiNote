package com.lv.note.widget.flowtag

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayout
import com.lv.note.widget.flowtag.TagAdapter
import com.lv.note.widget.flowtag.TagView
import java.util.*

/**
 * Created by zhy on 16/5/16.
 */
class TagFlowLayout<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet ?= null, defStyleAttr: Int = 0) : FlexboxLayout(context, attrs, defStyleAttr), TagAdapter.OnAdapterDataChanged {
    private var mAdapter: TagAdapter<T>? = null
    /**
     * 如果为false，则checkable无效
     */
    private var mCheckable = true

    private val mSelectedItem = HashSet<Int>()
    private val mPreSelectedItem = HashSet<Int>()

    init {
        flexDirection = FLEX_DIRECTION_ROW
        flexWrap = FLEX_WRAP_WRAP
    }


    fun setCheckable(isCheckable: Boolean) {
        mCheckable = isCheckable
    }

    fun setAdapter(adapter: TagAdapter<T>) {
        mAdapter = adapter
        adapterChanged()
    }

    private fun clearForDataChanged() {
        removeAllViews()
        mSelectedItem.clear()
    }


    private fun adapterChanged() {
        val adapter = mAdapter

        if (adapter == null) {
            clearForDataChanged()
            return
        }
        adapter.setOnAdapterDataChanged(this)
        dataChanged()
    }

    private fun dataChanged() {
        clearForDataChanged()
        val adapter = mAdapter ?: return
        var i = 0
        val n = adapter.count
        while (i < n) {
            val view = adapter.getView(this, i, adapter.getItem(i))
            val tagView: TagView
            if (view !is TagView && mCheckable) {
                tagView = TagView.wrap(context, view)
                addView(tagView)
                viewClickableSet(tagView, i)

                if (adapter.select(i)) {
                    mPreSelectedItem.add(i)
                    tagView.isChecked = true
                }

            } else {
                addView(view)
                viewClickableSet(view, i)

                if (adapter.select(i)) {
                    mPreSelectedItem.add(i)
                    adapter.onSelect(this, view, i)
                }
            }
            i++

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (mPreSelectedItem.size > 0) {
            mSelectedItem.addAll(mPreSelectedItem)
            mPreSelectedItem.clear()
        }

    }

    private fun viewClickableSet(view: View, position: Int) {

        if (!mAdapter!!.enabled(position)) return

        view.setOnClickListener { v ->
            doSupportCheckable(v, position)

            if (mOnTagClickListener != null) {
                mOnTagClickListener!!.onTagClick(this@TagFlowLayout, TagView.unwarp(v), position)
            }

            doCustomSelect(v, position)
        }
    }

    private fun doCustomSelect(v: View, position: Int) {
        if (!mCheckable) {
            if (mSelectedItem.contains(position)) {
                mSelectedItem.remove(position)
                mAdapter!!.onUnSelect(this@TagFlowLayout, v, position)
            } else {
                mSelectedItem.add(position)
                mAdapter!!.onSelect(this@TagFlowLayout, v, position)
            }
        }
    }

    private fun doSupportCheckable(v: View, position: Int) {
        if (v is TagView && mCheckable) {
            val checked = v.isChecked
            if (checked) {
                v.isChecked = false
                mSelectedItem.remove(position)
            } else {
                v.isChecked = true
                mSelectedItem.add(position)
            }

        }
    }

    override fun onChange() {
        dataChanged()
    }

    private var mOnTagClickListener: OnTagClickListener? = null

    interface OnTagClickListener {
        fun onTagClick(parent: ViewGroup, view: View, position: Int)
    }

    fun setOnTagClickListener(onTagClickListener: OnTagClickListener) {
        mOnTagClickListener = onTagClickListener
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState())

        var selectPos = ""
        if (mSelectedItem.size > 0) {
            for (key in mSelectedItem) {
                selectPos +=   "$key|"
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos)
        bundle.putBoolean(KEY_CHECKABLE, mCheckable)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (mAdapter == null) {
            super.onRestoreInstanceState(state)
            return
        }

        if (state is Bundle) {
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            mCheckable = state.getBoolean(KEY_CHECKABLE, true)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                for (pos in split) {
                    val index = Integer.parseInt(pos)
                    mSelectedItem.add(index)
                }

                if (mSelectedItem.size > 0) {
                    for (i in mPreSelectedItem) {
                        if (!mCheckable)
                            mAdapter!!.onUnSelect(this, getChildAt(i), i)
                        else
                            (getChildAt(i) as TagView).isChecked = false
                    }
                    mPreSelectedItem.clear()

                } else {
                    mSelectedItem.addAll(mPreSelectedItem)
                    mPreSelectedItem.clear()
                }

                for (index in mSelectedItem) {
                    if (mCheckable) {
                        val tagView = getChildAt(index) as TagView
                        if (tagView != null)
                            tagView.isChecked = true
                    } else {
                        mAdapter!!.onSelect(this, getChildAt(index), index)
                    }
                }


            }
            super.onRestoreInstanceState(state.getParcelable<Parcelable>(KEY_DEFAULT))
            return
        }
        super.onRestoreInstanceState(state)
    }

    companion object {


        private val KEY_CHOOSE_POS = "key_choose_pos"
        private val KEY_CHECKABLE = "key_checkable"
        private val KEY_DEFAULT = "key_default"
    }


}
