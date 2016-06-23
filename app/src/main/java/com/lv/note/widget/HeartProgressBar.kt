package com.lv.note.widget

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.AnticipateOvershootInterpolator
import com.lv.note.R

/**
 * Created by Ahmed on 7/19/14.
 */
class HeartProgressBar : View {

    // #MARK - Constants

    // view property
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var heartColor: Int = 0

    var isStopped: Boolean = false
        private set
    private var animatorLeftHeart: ValueAnimator? = null
    private var animatorRightHeart: ValueAnimator? = null

    private var leftHeartX: Float = 0.toFloat()
    private var leftHeartY: Float = 0.toFloat()
    private var rightHeartX: Float = 0.toFloat()
    private var rightHeartY: Float = 0.toFloat()

    // #MARK - Constructors

    constructor(context: Context) : super(context) {
        init(Color.parseColor("#FF4351"))
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.HeartProgressBar, 0, 0)
        try {
            val heartColor = typedArray.getColor(R.styleable.HeartProgressBar_heartColor, Color.parseColor("#FF4351"))
            init(heartColor)
        } finally {
            typedArray.recycle()
        }
    }

    private fun init(heartColor: Int) {
        this.heartColor = heartColor
        this.isStopped = true
    }

    // #MARK - User's Methods

    fun start() {
        if(!isStopped)
            return
        this.isStopped = false

        leftHeartX = (width / 4 + width / 8).toFloat()
        leftHeartY = (height / 4 + height / 8).toFloat()

        var widthPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_X,(width / 4 + width / 8).toFloat(), (width / 2 + width / 8).toFloat())
        var heightPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_Y, (height / 4 + height / 8).toFloat(),( height / 2 + height / 8).toFloat())
        animatorLeftHeart = ValueAnimator.ofPropertyValuesHolder(widthPropertyHolder, heightPropertyHolder)
        animatorLeftHeart!!.duration = 2000
        animatorLeftHeart!!.startDelay = 1000
        animatorLeftHeart!!.interpolator = AnticipateOvershootInterpolator()
        animatorLeftHeart!!.addUpdateListener(leftHeartAnimationUpdateListener)
        animatorLeftHeart!!.repeatMode = ValueAnimator.REVERSE
        animatorLeftHeart!!.repeatCount = ValueAnimator.INFINITE

        widthPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_X, (width / 2 + width / 8).toFloat(),( width / 4 + width / 8).toFloat())
        heightPropertyHolder = PropertyValuesHolder.ofFloat(POSITION_Y, (height / 4 + height / 8).toFloat(), (height / 2 + height / 8).toFloat())
        animatorRightHeart = ValueAnimator.ofPropertyValuesHolder(widthPropertyHolder, heightPropertyHolder)
        animatorRightHeart!!.duration = 2000
        animatorRightHeart!!.interpolator = AnticipateOvershootInterpolator()
        animatorRightHeart!!.addUpdateListener(rightHeartAnimationUpdateListener)
        animatorRightHeart!!.repeatCount = ValueAnimator.INFINITE
        animatorRightHeart!!.repeatMode = ValueAnimator.REVERSE

        animatorRightHeart!!.start()
        animatorLeftHeart!!.start()

        invalidate()
    }

    fun dismiss() {
        this.isStopped = true
        animatorLeftHeart!!.cancel()
        animatorRightHeart!!.cancel()
        invalidate()
    }

    fun setHeartColor(color: Int) {
        this.heartColor = color
        invalidate()
    }

    // #MARK - Utility Methods

    private fun measureWidth(widthMeasureSpec: Int): Int {
        var result = 0

        val specMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val specSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            //            result = screenWidth;
            result = 200
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        this.mWidth = result
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        var result = 0

        val specMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val specSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            //            result = screenHeight;
            result = 200
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        this.mHeight = result
        return result
    }

    private fun measureCircleRadius(width: Int, height: Int): Float {
        val radius = Math.sqrt(Math.pow((width / 2).toDouble(), 2.0) + Math.pow((height / 2).toDouble(), 2.0)).toFloat() / 4
        return radius + 2
    }

    // #MARK - Listeners Methods

    internal var leftHeartAnimationUpdateListener: ValueAnimator.AnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        if (!isStopped) {
            leftHeartX = animation.getAnimatedValue(POSITION_X) as Float
            leftHeartY = animation.getAnimatedValue(POSITION_Y) as Float
            invalidate()
        }
    }

    internal var rightHeartAnimationUpdateListener: ValueAnimator.AnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        if (!isStopped) {
            rightHeartX = animation.getAnimatedValue(POSITION_X) as Float
            rightHeartY = animation.getAnimatedValue(POSITION_Y) as Float
            invalidate()
        }
    }

    // #MARK - Override Methods

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        if (this.width != this.height) {
            return
        }
        if (!this.isStopped) {
            drawRhombus(canvas)
            drawCircle(canvas, rightHeartX, rightHeartY)
            drawCircle(canvas, leftHeartX, leftHeartY)
        }
        canvas.restore()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        this.mWidth = width
        this.mHeight = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    // #MARK - Drawing Methods

    private fun drawRhombus(canvas: Canvas) {
        val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint.color = this.heartColor
        val rect = RectF()
        val sizeOffset = (width * 0.145).toFloat()
        val xOffset = (width * 0.075).toFloat()
        val yOffset = (height * 0.075).toFloat()
        rect.set(width / 4 + xOffset, height / 4 + sizeOffset - yOffset, width.toFloat() - (width / 4).toFloat() - sizeOffset + xOffset, height.toFloat() - (height / 4).toFloat() - yOffset)
        canvas.rotate(-45f, rect.centerX(), rect.centerY())
        canvas.drawRect(rect, rectPaint)
        canvas.rotate(45f, rect.centerX(), rect.centerY())

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = this.heartColor
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isDither = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 3f

        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo((width / 2).toFloat(), (height / 4).toFloat())
        path.lineTo((width / 4).toFloat(), (height / 2).toFloat())
        path.moveTo((width / 4).toFloat(), (height / 2).toFloat())
        path.lineTo((width / 2).toFloat(), (height - height / 4).toFloat())
        path.moveTo((width / 2).toFloat(), (height - height / 4).toFloat())
        path.lineTo((width - width / 4).toFloat(), (height / 2).toFloat())
        path.moveTo((width - width / 4).toFloat(), (height / 2).toFloat())
        path.lineTo((width / 2).toFloat(), (height / 4).toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawCircle(canvas: Canvas, x: Float, y: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = this.heartColor
        val circleRadius = measureCircleRadius(this.width, this.height)
        canvas.drawCircle(x, y, circleRadius, paint)
    }

    companion object {
        private val POSITION_X = "positionX"
        private val POSITION_Y = "positionY"
    }

}
