package com.lv.note.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView.ViewHolder
import android.text.Spanned
import android.util.SparseArray
import android.view.View
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lv.note.R
import io.github.mthli.knife.KnifeText

/**
 * User: 吕勇
 * Date: 2016-03-01
 * Time: 8:39
 * Description:HeaderView的ViewHolder
 */
class BaseHolder
/**
 * Instantiates a new Base holder.

 * @param mConvertView the m convert view
 */
(private val mConvertView: View) : ViewHolder(mConvertView) {

    private val mViews: SparseArray<View>

    init {
        this.mViews = SparseArray<View>()
    }


    /**
     * Gets view.

     * @param     the type parameter
     * *
     * @param viewId the view id
     * *
     * @return the view
     */
    fun <T : View> getView(viewId: Int): T {
        var view: View? = mViews.get(viewId)
        if (null == view) {
            view = mConvertView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T
    }

    /**
     * 设置TextView的内

     * @param viewId 控件id
     * *
     * @param text   文本内容
     * *
     * @return ViewHolder text
     */
    fun setText(viewId: Int, text: String): BaseHolder {
        val view = getView<TextView>(viewId)
        view.text = text
        return this
    }

    /**
     * 设置TextView的内

     * @param viewId  控件id
     * *
     * @param spanned 文本内容
     * *
     * @return BaseHolder spanned
     */
    fun setSpanned(viewId: Int, spanned: Spanned): BaseHolder {
        val view = getView<TextView>(viewId)
        view.text = spanned
        return this
    }
    fun setKnifeTextHtml(viewId: Int, html: String): BaseHolder {
        val view= getView<KnifeText>(viewId)
        view.fromHtml(html)
        view.setOnTouchListener { view, motionEvent ->
            view.parent!!.requestDisallowInterceptTouchEvent(false)
            false
        }
        return this
    }
    fun setWebViewHtml(viewId: Int, html: String): BaseHolder {
        val view= getView<WebView>(viewId)
        view.loadData(html, "text/html", "utf-8")
        return this
    }

    /**
     * 设置TextView的内

     * @param viewId 控件id
     * *
     * @param resId  资源文件中的id
     * *
     * @return BaseHolder res id text
     */
    fun setResIdText(viewId: Int, resId: Int): BaseHolder {
        val view = getView<TextView>(viewId)
        view.setText(resId)
        return this
    }

    /**
     * Sets image resource.

     * @param viewId 控件id
     * *
     * @param resId  资源文件中的id
     * *
     * @return BaseHolder image resource
     */
    fun setImageResource(viewId: Int, resId: Int): BaseHolder {
        val view = getView<ImageView>(viewId)
        view.setImageResource(resId)
        return this
    }

    fun setImageUrl(viewId: Int, url: String): BaseHolder {
        val view = getView<ImageView>(viewId)
        Glide.with(view.context)
                .load(url)
                .placeholder(R.mipmap.ic_loading)
                .error(R.mipmap.ic_loading)
                .crossFade()
                .into(view)
        return this
    }

    /**
     * Sets image bitmap.

     * @param viewId 控件id
     * *
     * @param bitmap 图片的的Bitmap
     * *
     * @return BaseHolder image bitmap
     */
    fun setImageBitmap(viewId: Int, bitmap: Bitmap): BaseHolder {
        val view = getView<ImageView>(viewId)
        view.setImageBitmap(bitmap)
        return this
    }

    fun setRating(viewId: Int, rating: Float): BaseHolder {
        val view = getView<RatingBar>(viewId)
        view.rating=rating
        return this
    }

    /**
     * Sets image drawable.

     * @param viewId   控件id
     * *
     * @param drawable 图片的的Drawable
     * *
     * @return BaseHolder image drawable
     */
    fun setImageDrawable(viewId: Int, drawable: Drawable): BaseHolder {
        val view = getView<ImageView>(viewId)
        view.setImageDrawable(drawable)
        return this
    }

    /**
     * 给控件设置tag

     * @param viewId 控件id
     * *
     * @param obj    tag
     * *
     * @return ViewHolder tag
     */
    fun setTag(viewId: Int, obj: Any): BaseHolder {
        val view = getView<View>(viewId)
        view.tag = obj
        return this
    }

    /**
     * 给控件设置OnClickListener

     * @param viewId   控件id
     * *
     * @param listener 点击事件
     * *
     * @param tag      the tag
     * *
     * @return ViewHolder click listener
     */
    fun setClickListener(viewId: Int, listener: View.OnClickListener, tag: Any): BaseHolder {
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        view.tag = tag
        return this
    }

    /**
     * 给控件设置OnClickListener

     * @param listener 点击事件
     * *
     * @param tag      the tag
     * *
     * @param viewIds  控件ids
     * *
     * @return ViewHolder click listener
     */
    fun setClickListener(listener: View.OnClickListener, tag: Any, vararg viewIds: Int): BaseHolder {
        for (viewId in viewIds) {
            val view = getView<View>(viewId)
            view.tag = tag
            view.setOnClickListener(listener)
        }
        return this
    }

    /**
     * 设置选中

     * @param checked 是否选择
     * *
     * @param viewIds view的Id
     * *
     * @return BaseHolder checked
     */
    fun setChecked(checked: Boolean, vararg viewIds: Int): BaseHolder {
        for (viewId in viewIds) {
            val view = getView<View>(viewId)
            view.isClickable=checked
        }
        return this
    }

    /**
     * 设置view可见

     * @param visible 是否看见
     * *
     * @param viewIds view的Id
     * *
     * @return BaseHolder visibility
     */
    fun setVisibility(visible: Boolean, vararg viewIds: Int): BaseHolder {
        for (viewId in viewIds) {
            setVisible(viewId, visible)
        }
        return this
    }

    /**
     * Sets visible.

     * @param viewId  the view id
     * *
     * @param visible the visible
     * *
     * @return the visible
     */
    fun setVisible(viewId: Int, visible: Boolean): BaseHolder {
        val view = getView<View>(viewId)
        view.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    /**
     * Gets view.

     * @param     the type parameter
     * *
     * @param viewId the view id
     * *
     * @param tClass the t class
     * *
     * @return the view
     */
    fun <T : View> getView(viewId: Int, tClass: Class<T>): T {
        return getView(viewId)
    }

    /**
     * Sets visible.

     * @param viewId  the view id
     * *
     * @param visible the visible
     * *
     * @return the visible
     */
    fun setVisible(viewId: Int, visible: Int): BaseHolder {
        val view = getView<View>(viewId)
        view.visibility = visible
        return this
    }


    /**
     * Sets alpha.

     * @param value   the value
     * *
     * @param viewIds the view ids
     * *
     * @return the alpha
     */
    fun setAlpha(value: Float, vararg viewIds: Int): BaseHolder {
        for (viewId in viewIds) {
            setAlpha(viewId, value)
        }
        return this
    }

    /**
     * Sets alpha.

     * @param viewId the view id
     * *
     * @param value  the value
     * *
     * @return the alpha
     */
    fun setAlpha(viewId: Int, value: Float): BaseHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId).alpha = value
        } else {
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId).startAnimation(alpha)
        }
        return this
    }

    /**
     * Sets image level.

     * @param viewId the view id
     * *
     * @param level  the level
     * *
     * @return the image level
     */
    fun setImageLevel(viewId: Int, level: Int): BaseHolder {
        val view = getView<ImageView>(viewId)
        view.setImageLevel(level)
        return this
    }

    /**
     * Sets selected.

     * @param viewId   the view id
     * *
     * @param selected the selected
     * *
     * @return the selected
     */
    fun setSelected(viewId: Int, selected: Boolean): BaseHolder {
        val view = getView<CheckedTextView>(viewId)
        view.isSelected = selected
        return this
    }

    /**
     * Sets selected.

     * @param selected the selected
     * *
     * @param viewIds  the view ids
     * *
     * @return the selected
     */
    fun setSelected(selected: Boolean, vararg viewIds: Int): BaseHolder {
        for (viewId in viewIds) {
            val view = getView<CheckedTextView>(viewId)
            view.isSelected = selected
        }
        return this
    }


    /**
     * 給item的子控件设置点击事件
     * @param listener the OnItemChildClickListener
     * *
     * @param viewIds the viewIds
     */
    fun setOnItemChildClickListener(listener: LBaseAdapter<*>.OnItemChildClickListener, vararg viewIds: Int): BaseHolder {
        listener.position = adapterPosition
        for (viewId in viewIds) {
            val view = getView<View>(viewId)
            view.setOnClickListener(listener)
        }
        return this
    }
}
