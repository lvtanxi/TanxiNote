package com.lv.note.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.lv.note.R

/**
 * User: 吕勇
 * Date: 2016-05-09
 * Time: 11:40
 * Description:针对Glide的圆角
 */
class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    val circleImage: ImageView

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
        val defColor = ContextCompat.getColor(context, android.R.color.darker_gray)
        val borderColor = array.getColor(R.styleable.CircleImageView_layoutBorderColor, defColor)
        val bgColor = array.getColor(R.styleable.CircleImageView_layoutBgColor, defColor)
        val borderWidth = array.getDimension(R.styleable.CircleImageView_layoutBorderWidth, 1f).toInt()
        val imageWidth = array.getDimension(R.styleable.CircleImageView_CircleImageWidth, ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()).toInt()
        val imageHight = array.getDimension(R.styleable.CircleImageView_CircleImageHight, ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()).toInt()
        val imagePadding = array.getDimension(R.styleable.CircleImageView_CircleImagePadding, 8f).toInt()
        val imageSrc = array.getDrawable(R.styleable.CircleImageView_CircleImageSrc)
        array.recycle()
        this.circleImage = ImageView(context)
        val myGrad = GradientDrawable()
        myGrad.setShape(GradientDrawable.OVAL)
        myGrad.setColor(bgColor)
        myGrad.setStroke(borderWidth, borderColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            background = myGrad
        else
            setBackgroundDrawable(myGrad)
        circleImage.layoutParams = ViewGroup.LayoutParams(imageWidth, imageHight)
        circleImage.setPadding(imagePadding, imagePadding, imagePadding, imagePadding)
        circleImage.scaleType= ImageView.ScaleType.FIT_XY
        circleImage.setImageDrawable(imageSrc)
        addView(circleImage)
    }
}
