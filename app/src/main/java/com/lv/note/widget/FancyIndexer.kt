package com.lv.note.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.PointF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.lv.note.R


class FancyIndexer : View {


    interface OnTouchLetterChangedListener {
        fun onTouchLetterChanged(s: String)
    }

    /////////////////////////////////////////////////////////////////////////

    //Properties
    // 向右偏移多少画字符， default 30
    internal var mWidthOffset = 30.0f

    // 最小字体大小
    internal var mMinFontSize = 24

    // 最大字体大小
    internal var mMaxFontSize = 48

    // 提示字体大小
    internal var mTipFontSize = 52

    // 提示字符的额外偏移
    internal var mAdditionalTipOffset = 20.0f

    // 贝塞尔曲线控制的高度
    internal var mMaxBezierHeight = 150.0f

    // 贝塞尔曲线单侧宽度
    internal var mMaxBezierWidth = 240.0f

    // 贝塞尔曲线单侧模拟线量
    internal var mMaxBezierLines = 32

    // 列表字符颜色
    internal var mFontColor = 0xffffffff.toInt()

    // 提示字符颜色
    //	int  mTipFontColor = 0xff3399ff;
    internal var mTipFontColor = 0xffd33e48.toInt()

    /////////////////////////////////////////////////////////////////////////

    private var mListener: OnTouchLetterChangedListener? = null

    private val ConstChar = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "", "", "")

    internal var mChooseIndex = -1
    internal var mPaint = Paint()
    internal var mTouch = PointF()

    internal var mBezier1: Array<PointF?>?=null
    internal var mBezier2: Array<PointF?>?=null

    internal var mLastOffset = FloatArray(ConstChar.size) // 记录每一个字母的x方向偏移量, 数字<=0
    internal var mLastFucusPostion = PointF()

    internal var mScroller: Scroller?=null
    internal var mAnimating = false
    internal var mAnimationOffset: Float = 0.toFloat()

    internal var mHideAnimation = false
    internal var mAlpha = 255

    internal var mHideWaitingHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                //				mScroller.startScroll(0, 0, 255, 0, 1000);
                mHideAnimation = true
                mAnimating = false
                this@FancyIndexer.invalidate()
                return
            }
            super.handleMessage(msg)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initData(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData(context, attrs)
    }

    constructor(context: Context) : super(context) {
        initData(null, null)
    }

    private fun initData(context: Context?, attrs: AttributeSet?) {

        if (context != null && attrs != null) {

            val a = context.obtainStyledAttributes(attrs, R.styleable.FancyIndexer, 0, 0)

            mWidthOffset = a.getDimension(R.styleable.FancyIndexer_widthOffset, mWidthOffset)
            mMinFontSize = a.getInteger(R.styleable.FancyIndexer_minFontSize, mMinFontSize)
            mMaxFontSize = a.getInteger(R.styleable.FancyIndexer_maxFontSize, mMaxFontSize)
            mTipFontSize = a.getInteger(R.styleable.FancyIndexer_tipFontSize, mTipFontSize)
            mMaxBezierHeight = a.getDimension(R.styleable.FancyIndexer_maxBezierHeight, mMaxBezierHeight)
            mMaxBezierWidth = a.getDimension(R.styleable.FancyIndexer_maxBezierWidth, mMaxBezierWidth)
            mMaxBezierLines = a.getInteger(R.styleable.FancyIndexer_maxBezierLines, mMaxBezierLines)
            mAdditionalTipOffset = a.getDimension(R.styleable.FancyIndexer_additionalTipOffset, mAdditionalTipOffset)
            mFontColor = a.getColor(R.styleable.FancyIndexer_fontColor, mFontColor)
            mTipFontColor = a.getColor(R.styleable.FancyIndexer_tipFontColor, mTipFontColor)
            a.recycle()
        }
        mScroller = Scroller(getContext())
        mTouch.x = 0f
        mTouch.y = -10 * mMaxBezierWidth

        mBezier1 = arrayOfNulls<PointF>(mMaxBezierLines)
        mBezier2 = arrayOfNulls<PointF>(mMaxBezierLines)

        calculateBezierPoints()

    }

    override fun onDraw(canvas: Canvas) {

        // 控件宽高
        val height = height
        val width = width

        // 单个字母高度
        val singleHeight = height / ConstChar.size.toFloat()

        var workHeight = 0

        if (mAlpha == 0)
            return

        mPaint.reset()

        var saveCount = 0

        if (mHideAnimation) {
            saveCount = canvas.save()
            canvas.saveLayerAlpha(0f, 0f, width.toFloat(), height.toFloat(), mAlpha, Canvas.ALL_SAVE_FLAG)
        }

        for (i in ConstChar.indices) {

            mPaint.color = mFontColor
            mPaint.isAntiAlias = true

            val xPos = width - mWidthOffset
            var yPos = workHeight + singleHeight / 2

            //float adjustX = adjustXPos( yPos, i == mChooseIndex );
            // 根据当前字母y的位置计算得到字体大小
            val fontSize = adjustFontSize(i, yPos)
            mPaint.textSize = fontSize.toFloat()

            // 添加一个字母的高度
            workHeight += singleHeight.toInt()

            // 绘制字母
            drawTextInCenter(canvas, ConstChar[i], xPos + ajustXPosAnimation(i, yPos), yPos)

            // 绘制的字母和当前触摸到的一致, 绘制红色被选中字母
            if (i == mChooseIndex) {
                mPaint.color = mTipFontColor
                mPaint.isFakeBoldText = true
                mPaint.textSize = mTipFontSize.toFloat()
                yPos = mTouch.y

                var pos = 0f

                if (mAnimating || mHideAnimation) {
                    pos = mLastFucusPostion.x
                    yPos = mLastFucusPostion.y
                } else {
                    pos = xPos + ajustXPosAnimation(i, yPos) - mAdditionalTipOffset
                    mLastFucusPostion.x = pos
                    mLastFucusPostion.y = yPos
                }

                drawTextInCenter(canvas, ConstChar[i], pos, yPos)
                //	    	   mPaint.setStrokeWidth(5);
                //	    	   canvas.drawLine(0, yPos, width, yPos, mPaint);
            }
            mPaint.reset()
        }

        if (mHideAnimation) {
            canvas.restoreToCount(saveCount)
        }

    }

    /**
     * @param canvas 画板
     * *
     * @param string 被绘制的字母
     * *
     * @param xCenter 字母的中心x方向位置
     * *
     * @param yCenter 字母的中心y方向位置
     */
    private fun drawTextInCenter(canvas: Canvas, string: String, xCenter: Float, yCenter: Float) {

        val fm = mPaint.fontMetrics
        //float fontWidth = paint.measureText(string);
        val fontHeight = mPaint.fontSpacing

        var drawY = yCenter + fontHeight / 2 - fm.descent

        if (drawY < -fm.ascent - fm.descent)
            drawY = -fm.ascent - fm.descent

        if (drawY > height)
            drawY = height.toFloat()

        mPaint.textAlign = Align.CENTER

        canvas.drawText(string, xCenter, drawY, mPaint)
    }

    private fun adjustFontSize(i: Int, yPos: Float): Int {

        // 根据水平方向偏移量计算出一个放大的字号
        val adjustX = Math.abs(ajustXPosAnimation(i, yPos))

        val adjustSize = ((mMaxFontSize - mMinFontSize) * adjustX / mMaxBezierHeight.toFloat()).toInt() + mMinFontSize

        return adjustSize
    }

    /**
     * x 方向的向左偏移量
     * @param i    当前字母的索引
     * *
     * @param yPos y方向的初始位置
     * *
     * @return
     */
    private fun ajustXPosAnimation(i: Int, yPos: Float): Float {

        var offset: Float
        if (this.mAnimating || this.mHideAnimation) {
            // 正在动画中或在做隐藏动画
            offset = mLastOffset[i]
            if (offset != 0.0f) {
                offset += this.mAnimationOffset
                if (offset > 0)
                    offset = 0f
            }
        } else {

            // 根据当前字母y方向位置, 计算水平方向偏移量
            offset = adjustXPos(yPos)

            // 当前触摸的x方向位置
            val xPos = mTouch.x

            var width = width - mWidthOffset
            width = width - 60

            // 字母绘制时向左偏移量 进行修正, offset需要是<=0的值
            if (offset != 0.0f && xPos > width)
                offset += xPos - width
            if (offset > 0)
                offset = 0f

            mLastOffset[i] = offset
        }
        return offset
    }

    private fun adjustXPos(yPos: Float): Float {

        val dis = yPos - mTouch.y // 字母y方向位置和触摸时y值坐标的差值, 距离越小, 得到的水平方向偏差越大
        if (dis > -mMaxBezierWidth && dis < mMaxBezierWidth) {
            // 在2个贝赛尔曲线宽度范围以内 (一个贝赛尔曲线宽度是指一个山峰的一边)

            // 第一段 曲线
            if (dis > mMaxBezierWidth / 4) {
                for (i in mMaxBezierLines - 1 downTo 1) {
                    // 从下到上, 逐个计算

                    if (dis == -mBezier1!![i]!!.y)
                    // 落在点上
                        return mBezier1!![i]!!.x

                    // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                    if (dis > -mBezier1!![i]!!.y && dis < -mBezier1!![i - 1]!!.y) {
                        return (dis + mBezier1!![i]!!.y) * (mBezier1!![i - 1]!!.x - mBezier1!![i]!!.x) / (-mBezier1!![i - 1]!!.y + mBezier1!![i]!!.y) + mBezier1!![i]!!.x
                    }
                }
                return mBezier1!![0]!!.x
            }

            // 第三段 曲线, 和第一段曲线对称
            if (dis < -mMaxBezierWidth / 4) {
                for (i in 0..mMaxBezierLines - 1 - 1) {
                    // 从上到下

                    if (dis == mBezier1!![i]!!.y)
                    // 落在点上
                        return mBezier1!![i]!!.x

                    // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                    if (dis > mBezier1!![i]!!.y && dis < mBezier1!![i + 1]!!.y) {
                        return (dis - mBezier1!![i]!!.y) * (mBezier1!![i + 1]!!.x - mBezier1!![i]!!.x) / (mBezier1!![i + 1]!!.y - mBezier1!![i]!!.y) + mBezier1!![i]!!.x
                    }
                }
                return mBezier1!![mMaxBezierLines - 1]!!.x
            }

            // 第二段 峰顶曲线
            for (i in 0..mMaxBezierLines - 1 - 1) {

                if (dis == mBezier2!![i]!!.y)
                    return mBezier2!![i]!!.x

                // 如果距离dis落在两个贝塞尔曲线模拟点之间, 通过三角函数计算得到当前dis对应的x方向偏移量
                if (dis > mBezier2!![i]!!.y && dis < mBezier2!![i + 1]!!.y) {
                    return (dis - mBezier2!![i]!!.y) * (mBezier2!![i + 1]!!.x - mBezier2!![i]!!.x) / (mBezier2!![i + 1]!!.y - mBezier2!![i]!!.y) + mBezier2!![i]!!.x
                }
            }
            return mBezier2!![mMaxBezierLines - 1]!!.x

        }

        return 0.0f
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        val oldmChooseIndex = mChooseIndex
        val listener = mListener
        val c = (y / height * ConstChar.size).toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {

                if (this.width > mWidthOffset) {
                    if (event.x < this.width - mWidthOffset)
                        return false
                }

                mHideWaitingHandler.removeMessages(1)

                mScroller!!.abortAnimation()
                mAnimating = false
                mHideAnimation = false
                mAlpha = 255

                mTouch.x = event.x
                mTouch.y = event.y

                if (oldmChooseIndex != c && listener != null) {
                    if (c > 0 && c < ConstChar.size) {
                        listener.onTouchLetterChanged(ConstChar[c])
                        mChooseIndex = c
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                mTouch.x = event.x
                mTouch.y = event.y
                invalidate()
                if (oldmChooseIndex != c && listener != null) {

                    if (c >= 0 && c < ConstChar.size) {
                        listener.onTouchLetterChanged(ConstChar[c])
                        mChooseIndex = c
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                mTouch.x = event.x
                mTouch.y = event.y

                //this.mChooseIndex = -1;

                mScroller!!.startScroll(0, 0, mMaxBezierHeight.toInt(), 0, 2000)
                mAnimating = true
                postInvalidate()
            }
        }
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {
            if (mAnimating) {
                val x = mScroller!!.currX.toFloat()
                mAnimationOffset = x
            } else if (mHideAnimation) {
                mAlpha = 255 - mScroller!!.currX.toInt()
            }
            invalidate()
        } else if (mScroller!!.isFinished) {
            if (mAnimating) {
                mHideWaitingHandler.sendEmptyMessage(1)
            } else if (mHideAnimation) {
                mHideAnimation = false
                this.mChooseIndex = -1
                mTouch.x = -10000f
                mTouch.y = -10000f
            }

        }
    }

    fun setOnTouchLetterChangedListener(listener: OnTouchLetterChangedListener) {
        this.mListener = listener
    }

    /**
     * 计算出所有贝塞尔曲线上的点
     * 个数为 mMaxBezierLines * 2 = 64
     */
    private fun calculateBezierPoints() {

        val mStart = PointF()   // 开始点
        val mEnd = PointF()        // 结束点
        val mControl = PointF() // 控制点


        // 计算第一段红色部分 贝赛尔曲线的点
        // 开始点
        mStart.x = 0.0f
        mStart.y = -mMaxBezierWidth

        // 控制点
        mControl.x = 0.0f
        mControl.y = -mMaxBezierWidth / 2

        // 结束点
        mEnd.x = -mMaxBezierHeight / 2
        mEnd.y = -mMaxBezierWidth / 4

        mBezier1!![0] = PointF()
        mBezier1!![mMaxBezierLines - 1] = PointF()

        mBezier1!![0]?.set(mStart)
        mBezier1!![mMaxBezierLines - 1]?.set(mEnd)

        for (i in 1..mMaxBezierLines - 1 - 1) {

            mBezier1!![i] = PointF()

            mBezier1!![i]?.x = calculateBezier(mStart.x, mEnd.x, mControl.x, i / mMaxBezierLines.toFloat())
            mBezier1!![i]?.y = calculateBezier(mStart.y, mEnd.y, mControl.y, i / mMaxBezierLines.toFloat())

        }

        // 计算第二段蓝色部分 贝赛尔曲线的点
        mStart.y = -mMaxBezierWidth / 4
        mStart.x = -mMaxBezierHeight / 2

        mControl.y = 0.0f
        mControl.x = -mMaxBezierHeight

        mEnd.y = mMaxBezierWidth / 4
        mEnd.x = -mMaxBezierHeight / 2

        mBezier2!![0] = PointF()
        mBezier2!![mMaxBezierLines - 1] = PointF()

        mBezier2!![0]?.set(mStart)
        mBezier2!![mMaxBezierLines - 1]?.set(mEnd)

        for (i in 1..mMaxBezierLines - 1 - 1) {

            mBezier2!![i] = PointF()
            mBezier2!![i]?.x = calculateBezier(mStart.x, mEnd.x, mControl.x, i / mMaxBezierLines.toFloat())
            mBezier2!![i]?.y = calculateBezier(mStart.y, mEnd.y, mControl.y, i / mMaxBezierLines.toFloat())
        }
    }

    /**
     * 贝塞尔曲线核心算法
     * @param start
     * *
     * @param end
     * *
     * @param control
     * *
     * @param val
     * *
     * @return
     * * 公式及动图, 维基百科: https://en.wikipedia.org/wiki/B%C3%A9zier_curve
     * * 中文可参考此网站: http://blog.csdn.net/likendsl/article/details/7852658
     */
    private fun calculateBezier(start: Float, end: Float, control: Float, `val`: Float): Float {

        val t = `val`
        val s = 1 - t

        val ret = start * s * s + 2f * control * s * t + end * t * t

        return ret
    }

    companion object {

        private val TAG = "FancyIndexer"
    }
}
