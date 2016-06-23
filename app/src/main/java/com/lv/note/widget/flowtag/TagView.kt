package com.lv.note.widget.flowtag

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.FrameLayout

class TagView(context: Context) : FrameLayout(context), Checkable {
    private var isChecked: Boolean = false


    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }


    /**
     * Change the checked state of the view

     * @param checked The new checked state
     */
    override fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            this.isChecked = checked
            refreshDrawableState()
        }
    }

    /**
     * @return The current checked state of the view
     */
    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)

        fun unwarp(view: View?): View {
            if (view == null)
                throw IllegalArgumentException("view can not be null .")
            if (view is TagView) {
                return view.getChildAt(0)
            } else {
                return view
            }
        }


        fun wrap(context: Context, view: View?): TagView {
            if (view == null)
                throw IllegalArgumentException("view can not be null .")
            view.isDuplicateParentStateEnabled = true
            val tagView = TagView(context)
            if (view.layoutParams != null) {
                tagView.layoutParams = view.layoutParams
            } else {
                val lp = ViewGroup.MarginLayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                val defaultMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics).toInt()
                lp.setMargins(defaultMargin,
                        defaultMargin,
                        defaultMargin,
                        defaultMargin)
                tagView.layoutParams = lp
            }
            tagView.addView(view)

            return tagView
        }
    }


}