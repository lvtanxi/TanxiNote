package com.lv.note.widget.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.lv.note.util.ThemeUtils
import java.util.*

class WeatherChartView<T : WeatherChartItem> : View {
    var items: List<T>? = null
    private var unit: String? = null
    private var yFormat = "0.#"


    fun setTuView(list: List<T>, unitInfo: String) {
        this.items = list
        this.unit = unitInfo
    }

    constructor(ct: Context) : super(ct) {
    }

    constructor(ct: Context, attrs: AttributeSet) : super(ct, attrs) {
    }

    constructor(ct: Context, attrs: AttributeSet, defStyle: Int) : super(ct, attrs, defStyle) {
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (items == null) {
            return
        }
        val mColor=ContextCompat.getColor(context,ThemeUtils.obtainThemeColor())
        val height = height
        val width = width

        val split = dip2px(context, 8f)
        val marginl = width / 12
        val margint = dip2px(context, 60f)
        val margint2 = dip2px(context, 25f)
        val bheight = height - margint - 2 * split

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color =  Color.WHITE
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(split.toFloat(), margint2.toFloat(), (width - split).toFloat(), margint2.toFloat(), paint)
        canvas.drawLine(split.toFloat(), (height - split).toFloat(), (width - split).toFloat(), (height - split).toFloat(),
                paint)
        // 画单位
        val p = Paint()
        p.alpha = 0x0000ff
        p.color = Color.WHITE
        p.textSize = sp2px(context, 10f).toFloat()
        canvas.drawText(unit!!, split.toFloat(), (margint2 + split * 2).toFloat(), p)
        // 画X坐标
        val xlist = ArrayList<Int>()
        paint.color = Color.WHITE
        for (i in items!!.indices) {
            val span = (width - 2 * marginl) / items!!.size
            val x = marginl + span / 2 + span * i
            xlist.add(x)
            drawText(items!![i].x, x, split * 2, canvas)
        }
        var max = java.lang.Float.MIN_VALUE
        var min = java.lang.Float.MAX_VALUE
        for (i in items!!.indices) {
            val y = items!![i].y
            if (y > max) {
                max = y
            }
            if (y < min) {
                min = y
            }
        }
        var span = max - min
        if (span == 0f) {
            span = 6.0f
        }
        max += span / 6.0f
        min -= span / 6.0f
        // 获取点集合
        val mPoints = getPoints(xlist, max, min, bheight, margint)
        // 画线
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        drawLine(mPoints, canvas, paint)
        // 画点
        paint.color =mColor
        paint.style = Paint.Style.FILL
        for (i in mPoints.indices) {
            canvas.drawCircle(mPoints[i].x.toFloat(), mPoints[i].y.toFloat(), 12f, paint)
            val yText = java.text.DecimalFormat(yFormat).format(items!![i].y.toDouble())
            drawText(yText, mPoints[i].x,
                    mPoints[i].y - dip2px(context, 12f), canvas)
        }
    }

    private fun getPoints(xlist: ArrayList<Int>, max: Float, min: Float,
                          h: Int, top: Int): Array<Point> {
        val points = arrayOfNulls<Point>(items!!.size)
        for (i in items!!.indices) {
            val ph = top + h - (h * ((items!![i].y - min) / (max - min))).toInt()
            points[i] = Point(xlist[i], ph)
        }
        return points as Array<Point>
    }

    private fun drawLine(ps: Array<Point>, canvas: Canvas, paint: Paint) {
        var startp = Point()
        var endp = Point()
        for (i in 0..ps.size - 1 - 1) {
            startp = ps[i]
            endp = ps[i + 1]
            canvas.drawLine(startp.x.toFloat(), startp.y.toFloat(), endp.x.toFloat(), endp.y.toFloat(), paint)
        }
    }

    private fun drawText(text: String, x: Int, y: Int, canvas: Canvas) {
        val p = Paint()
        p.alpha = 0x0000ff
        p.textSize = sp2px(context, 14f).toFloat()
        p.textAlign = Paint.Align.CENTER
        p.color = Color.WHITE
        canvas.drawText(text, x.toFloat(), y.toFloat(), p)
    }

    fun getyFormat(): String {
        return yFormat
    }

    fun setyFormat(yFormat: String) {
        this.yFormat = yFormat
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}