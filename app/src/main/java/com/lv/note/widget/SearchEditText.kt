package com.lv.note.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.lv.note.R


/**
 * 自定义搜索框
 * 张肖换
 * Created by Administrator on 2016/5/11.
 */
class SearchEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : EditText(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextWatcher {
    /**
     * 图标是否默认在左边
     */
    private var isIconLeft = false
    /**
     * 是否点击软键盘搜索
     */
    private var pressSearch = false
    /**
     * 软键盘搜索键监听
     */
    private var listener: OnSearchListener? = null

    private var drawables: Array<Drawable>? = null // 控件的图片资源
    private var drawableLeft: Drawable? = null
    private var drawableDel: Drawable? = null // 搜索图标和删除按钮图标
    private var eventX: Int = 0
    private var eventY: Int = 0 // 记录点击坐标
    private var rect: Rect? = null // 控件区域

    fun setOnSearchClickListener(listener: OnSearchListener) {
        this.listener = listener
    }

    interface OnSearchListener {
        fun onSearchClick(str: String)
    }


    init {
        init()
    }

    private fun init() {
        onFocusChangeListener = this
        addTextChangedListener(this)
    }


    override fun onDraw(canvas: Canvas) {
        if (isIconLeft) { // 如果是默认样式，直接绘制
            if (length() < 1) {
                drawableDel = null
            }
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null)
            super.onDraw(canvas)
        } else { // 如果不是默认样式，需要将图标绘制在中间
            if (drawables == null) drawables = compoundDrawables
            if (drawableLeft == null) drawableLeft = drawables!![0]
            val textWidth = paint.measureText(hint.toString())
            val drawablePadding = compoundDrawablePadding
            val drawableWidth = drawableLeft!!.intrinsicWidth
            val bodyWidth = textWidth + drawableWidth.toFloat() + drawablePadding.toFloat()
            canvas.translate((width.toFloat() - bodyWidth - paddingLeft.toFloat() - paddingRight.toFloat()) / 2, 0f)
            super.onDraw(canvas)
        }
    }


    override fun onFocusChange(v: View, hasFocus: Boolean) {
        // 被点击时，恢复默认样式
        if (!pressSearch && TextUtils.isEmpty(text.toString())) {
            isIconLeft = hasFocus
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 清空edit内容
        if (drawableDel != null && event.action == MotionEvent.ACTION_UP) {
            eventX = event.rawX.toInt()
            eventY = event.rawY.toInt()
            if (rect == null) rect = Rect()
            getGlobalVisibleRect(rect)
            rect!!.left = rect!!.right - drawableDel!!.intrinsicWidth
            if (rect!!.contains(eventX, eventY)) {
                setText("")
            }
        }
        // 删除按钮被按下时改变图标样式
        if (drawableDel != null && event.action == MotionEvent.ACTION_DOWN) {
            eventX = event.rawX.toInt()
            eventY = event.rawY.toInt()
            if (rect == null) rect = Rect()
            getGlobalVisibleRect(rect)
            rect!!.left = rect!!.right - drawableDel!!.intrinsicWidth
        } else {
            drawableDel = ContextCompat.getDrawable(context, R.mipmap.delete)
        }
        return super.onTouchEvent(event)
    }


    override fun afterTextChanged(arg0: Editable) {
        if (this.length() < 1) {
            drawableDel = null
        } else {
            drawableDel = ContextCompat.getDrawable(context, R.mipmap.delete)
        }
        listener?.onSearchClick(arg0.toString())
    }


    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                                   arg3: Int) {
    }

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                               arg3: Int) {
    }
}
