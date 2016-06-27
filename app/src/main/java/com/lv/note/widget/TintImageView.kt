package com.lv.note.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.lv.note.util.ThemeUtils


/**
 * User: 吕勇
 * Date: 2016-06-27
 * Time: 11:02
 * Description:
 */
class TintImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {
    init {
        doDra()
    }

    private fun doDra() {
        drawable?.let {
            setImageBitmap(ThemeUtils.getAlphaBitmap(context,ThemeUtils.drawable2Bitmap(drawable)))
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        doDra()
    }


    fun changeDrawable(drawable: Drawable) {
        setImageDrawable(drawable)
        doDra()
    }



}