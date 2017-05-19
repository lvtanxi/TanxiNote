package com.lv.note.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.lv.note.R
import java.util.*

/**
 * Created by gjz on 8/23/16.
 */
class WaveSideBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var mIndexItems: MutableList<String>? = null

    /**
     * the index in [.mIndexItems] of the current selected index item,
     * it's reset to -1 when the finger up
     */
    private var mCurrentIndex = -1

    /**
     * Y coordinate of the point where finger is touching,
     * the baseline is top of [.mStartTouchingArea]
     * it's reset to -1 when the finger up
     */
    private var mCurrentY = -1f

    private var mPaint: Paint? = null
    private var mTextColor: Int = 0
    private val mTextSize: Float

    /**
     * the height of each index item
     */
    private var mIndexItemHeight: Float = 0.toFloat()

    /**
     * offset of the current selected index item
     */
    private var mMaxOffset: Float = 0.toFloat()

    /**
     * [.mStartTouching] will be set to true when [MotionEvent.ACTION_DOWN]
     * happens in this area, and the side bar should start working.
     */
    private val mStartTouchingArea = RectF()

    /**
     * height and width of [.mStartTouchingArea]
     */
    private var mBarHeight: Float = 0.toFloat()
    private var mBarWidth: Float = 0.toFloat()

    /**
     * Flag that the finger is starting touching.
     * If true, it means the [MotionEvent.ACTION_DOWN] happened but
     * [MotionEvent.ACTION_UP] not yet.
     */
    private var mStartTouching = false

    /**
     * if true, the [OnSelectIndexItemListener.onSelectIndexItem]
     * will not be called until the finger up.
     * if false, it will be called when the finger down, up and move.
     */
    private var mLazyRespond = false

    /**
     * the position of the side bar, default is [.POSITION_RIGHT].
     * You can set it to [.POSITION_LEFT] for people who use phone with left hand.
     */
    private var mSideBarPosition: Int = 0

    /**
     * observe the current selected index item
     */
    private var onSelectIndexItemListener: OnSelectIndexItemListener? = null

    /**
     * the baseline of the first index item text to draw
     */
    private var mFirstItemBaseLineY: Float = 0.toFloat()

    /**
     * for [.dp2px] and [.sp2px]
     */
    private val mDisplayMetrics: DisplayMetrics

    init {
        mDisplayMetrics = context.resources.displayMetrics

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveSideBar)
        mLazyRespond = typedArray.getBoolean(R.styleable.WaveSideBar_sidebar_lazy_respond, false)
        mTextColor = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_color, Color.GRAY)
        mMaxOffset = typedArray.getDimension(R.styleable.WaveSideBar_sidebar_max_offset, dp2px(DEFAULT_MAX_OFFSET))
        mSideBarPosition = typedArray.getInt(R.styleable.WaveSideBar_sidebar_position, POSITION_RIGHT)
        val aBoolean = typedArray.getBoolean(R.styleable.WaveSideBar_sidebar_clear, false)
        typedArray.recycle()

        if (aBoolean)
            mIndexItems = ArrayList<String>()
        else
            mIndexItems = Arrays.asList(*DEFAULT_INDEX_ITEMS)
        mTextSize = sp2px(DEFAULT_TEXT_SIZE)

        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.textAlign = Paint.Align.CENTER
        mPaint!!.color = mTextColor
        mPaint!!.textSize = mTextSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)

        val fontMetrics = mPaint!!.fontMetrics
        mIndexItemHeight = fontMetrics.bottom - fontMetrics.top
        mBarHeight = mIndexItems!!.size * mIndexItemHeight

        // calculate the width of the longest text as the width of side bar
        for (indexItem in mIndexItems!!) {
            mBarWidth = Math.max(mBarWidth, mPaint!!.measureText(indexItem))
        }

        val areaLeft: Float = if (mSideBarPosition == POSITION_LEFT) 0f else width.toFloat() - mBarWidth - paddingRight.toFloat()

        val areaRight: Float = if (mSideBarPosition == POSITION_LEFT) paddingLeft.toFloat() + areaLeft + mBarWidth else width.toFloat()
        val areaTop = height / 2 - mBarHeight / 2
        val areaBottom = areaTop + mBarHeight
        mStartTouchingArea.set(
                areaLeft,
                areaTop,
                areaRight,
                areaBottom)

        // the baseline Y of the first item' text to draw
        mFirstItemBaseLineY = height / 2 - mIndexItems!!.size * mIndexItemHeight / 2 + (mIndexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2) - fontMetrics.ascent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw each item
        var i = 0
        val mIndexItemsLength = mIndexItems!!.size
        while (i < mIndexItemsLength) {
            val baseLineY = mFirstItemBaseLineY + mIndexItemHeight * i

            // calculate the scale factor of the item to draw
            val scale = getScale(i)

            val alphaScale = if (i == mCurrentIndex) 255 else (255 * (1 - scale)).toInt()
            mPaint!!.alpha = alphaScale

            mPaint!!.textSize = mTextSize + mTextSize * scale

            val drawX = if (mSideBarPosition == POSITION_LEFT)
                paddingLeft.toFloat() + mBarWidth / 2 + mMaxOffset * scale
            else
                width.toFloat() - paddingRight.toFloat() - mBarWidth / 2 - mMaxOffset * scale

            // draw
            canvas.drawText(
                    mIndexItems!![i], //item text to draw
                    drawX, //center text X
                    baseLineY, // baseLineY
                    mPaint!!)
            i++
        }
    }

    /**
     * calculate the scale factor of the item to draw

     * @param index the index of the item in array [.mIndexItems]
     * *
     * @return the scale factor of the item to draw
     */
    private fun getScale(index: Int): Float {
        var scale = 0f
        if (mCurrentIndex != -1) {
            val distance = Math.abs(mCurrentY - (mIndexItemHeight * index + mIndexItemHeight / 2)) / mIndexItemHeight
            scale = 1 - distance * distance / 16
            scale = Math.max(scale, 0f)
            //                Log.i("scale", mIndexItems[index] + ": " + scale);
        }
        return scale
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mIndexItems!!.size == 0) {
            return super.onTouchEvent(event)
        }

        val eventY = event.y
        val eventX = event.x
        mCurrentIndex = getSelectedIndex(eventY)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mStartTouchingArea.contains(eventX, eventY)) {
                    mStartTouching = true
                    if (!mLazyRespond && onSelectIndexItemListener != null) {
                        onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems!![mCurrentIndex])
                    }
                    invalidate()
                    return true
                } else {
                    return false
                }
                if (mStartTouching && !mLazyRespond && onSelectIndexItemListener != null) {
                    onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems!![mCurrentIndex])
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (mStartTouching && !mLazyRespond && onSelectIndexItemListener != null) {
                    onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems!![mCurrentIndex])
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mLazyRespond && onSelectIndexItemListener != null) {
                    onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems!![mCurrentIndex])
                }
                mCurrentIndex = -1
                mStartTouching = false
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun getSelectedIndex(eventY: Float): Int {
        mCurrentY = eventY - (height / 2 - mBarHeight / 2)
        if (mCurrentY <= 0) {
            return 0
        }

        var index = (mCurrentY / this.mIndexItemHeight).toInt()
        if (index >= this.mIndexItems!!.size) {
            index = this.mIndexItems!!.size - 1
        }
        return index
    }

    private fun dp2px(dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), this.mDisplayMetrics)
    }

    private fun sp2px(sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), this.mDisplayMetrics)
    }

    fun setIndexItems(indexItems: MutableList<String>) {
        mIndexItems = indexItems
        requestLayout()
    }

    fun setIndexItems(vararg indexItems: String) {
        mIndexItems = Arrays.asList(*indexItems)
        requestLayout()
    }

    fun clear() {
        if (null != mIndexItems) {
            mIndexItems!!.clear()
        }
    }

    fun addItem(item: String?) {
        if (null != mIndexItems && null != item) {
            mIndexItems!!.add(item)
            requestLayout()
        }
    }


    fun setTextColor(color: Int) {
        mTextColor = color
        mPaint!!.color = color
        invalidate()
    }

    fun setPosition(position: Int) {
        if (position != POSITION_RIGHT && position != POSITION_LEFT) {
            throw IllegalArgumentException("the position must be POSITION_RIGHT or POSITION_LEFT")
        }

        mSideBarPosition = position
        requestLayout()
    }

    fun setMaxOffset(offset: Int) {
        mMaxOffset = offset.toFloat()
        invalidate()
    }

    fun setLazyRespond(lazyRespond: Boolean) {
        mLazyRespond = lazyRespond
    }

    fun setOnSelectIndexItemListener(onSelectIndexItemListener: OnSelectIndexItemListener) {
        this.onSelectIndexItemListener = onSelectIndexItemListener
    }

    interface OnSelectIndexItemListener {
        fun onSelectIndexItem(index: String)
    }

    companion object {
        private val DEFAULT_TEXT_SIZE = 14 // sp
        private val DEFAULT_MAX_OFFSET = 80 //dp

        private val DEFAULT_INDEX_ITEMS = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val POSITION_RIGHT = 0
        val POSITION_LEFT = 1
    }
}
