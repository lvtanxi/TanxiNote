package com.lv.note.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.lv.note.R


/**
 * User: 吕勇
 * Date: 2016-03-25
 * Time: 09:05
 * Description:空数据显示
 */
class EmptyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private val mTextView: TextView?
    private var mDrawable: Drawable? = null
    private var mImageResId: Int = 0
    private var messageStr: String? = "亲,暂时没有数据喔！"
    private var isOne = true

    init {
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        setLayoutParams(layoutParams)
        if (null == messageStr)
            messageStr = "亲，暂时没有数据喔！"
        if (null == mDrawable)
            mDrawable = ContextCompat.getDrawable(context, R.drawable.no_data)
        mTextView = TextView(getContext())
        mTextView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        mTextView.gravity = Gravity.CENTER_HORIZONTAL
        setMessageStr(messageStr)
        setDrawable(mDrawable!!)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        this.addView(mTextView, params)
        visibility = View.INVISIBLE
    }


    fun setImageResId(imageResId: Int): EmptyView {
        if (null != mTextView && imageResId != 0)
            setDrawable(ContextCompat.getDrawable(context, imageResId))
        mImageResId = imageResId
        return this
    }

    fun setDrawable(drawable: Drawable): EmptyView {
        if (mDrawable != null && mTextView != null)
            mTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        mDrawable = drawable
        return this
    }

    fun setTextSize(textSize: Int): EmptyView {
        if (mTextView != null)
            mTextView.textSize = textSize.toFloat()
        return this
    }

    fun setColorResId(colorResId: Int): EmptyView {
        mTextView?.setTextColor(colorResId)
        return this
    }

    fun setMessageStr(messageStr: String?): EmptyView {
        if (null != mTextView && messageStr != null)
            mTextView.text = messageStr
        this.messageStr = messageStr
        return this
    }

    fun setTopPadding(padding: Int): EmptyView {
        val dividerMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                padding.toFloat(), resources.displayMetrics).toInt()
        setPadding(0, dividerMargin, 0, 0)
        return this
    }

    fun showNetWorkError(): EmptyView {
        if (isOne) {
            isOne = false
            return this
        }
        setImageResId(R.drawable.network_disconnection)
        setMessageStr("网络故障,请检查后重新获取数据!")
        visibility = View.VISIBLE
        return this
    }

    fun showEmptyView(): EmptyView {
        if (isOne) {
            isOne = false
            return this
        }
        if (mImageResId != 0) {
            setImageResId(mImageResId)
        } else if (mDrawable != null && mTextView != null) {
            setDrawable(mDrawable!!)
        }
        setMessageStr(messageStr)
        visibility = View.VISIBLE
        return this
    }

}
