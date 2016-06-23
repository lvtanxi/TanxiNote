package com.lv.note.widget.clip


import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RelativeLayout

class ClipImageLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    private val mZoomImageView: ClipZoomImageView
    private val mClipImageView: ClipImageBorderView

    private var mHorizontalPadding = 60

    init {

        mZoomImageView = ClipZoomImageView(context)
        mClipImageView = ClipImageBorderView(context)

        val lp = RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT)

        this.addView(mZoomImageView, lp)
        this.addView(mClipImageView, lp)


        // 计算padding的px
        mHorizontalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding.toFloat(), resources.displayMetrics).toInt()
        mZoomImageView.setHorizontalPadding(mHorizontalPadding)
        mClipImageView.setHorizontalPadding(mHorizontalPadding)
    }

    /**
     * 对外公布设置边距的方法,单位为dp

     * @param mHorizontalPadding
     */
    fun setHorizontalPadding(mHorizontalPadding: Int) {
        this.mHorizontalPadding = mHorizontalPadding
    }

    /**
     * 裁切图片

     * @return
     */
    fun clip(): Bitmap {
        return mZoomImageView.clip()
    }

    fun setBitmap(bitmap: Bitmap) {
        mZoomImageView.setImageBitmap(bitmap)
    }

}
