package com.lv.note.widget.clip


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class ClipImageBorderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    /**
     * 水平方向与View的边距
     */
    private var mHorizontalPadding: Int = 0
    /**
     * 边框的宽度 单位dp
     */
    private var mBorderWidth = 2

    private val mPaint: Paint

    init {

        mBorderWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mBorderWidth.toFloat(), resources.displayMetrics).toInt()
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制边框
        mPaint.color = Color.parseColor("#FFFFFF")
        mPaint.strokeWidth = mBorderWidth.toFloat()
        mPaint.style = Style.STROKE
        //方形边框
        //		canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);
        //圆形边框
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2 - mHorizontalPadding).toFloat(), mPaint)

    }

    fun setHorizontalPadding(mHorizontalPadding: Int) {
        this.mHorizontalPadding = mHorizontalPadding
    }

}
