package com.lv.note.adapter


import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.lv.note.anim.BaseAnimation
import com.lv.note.anim.SlideInBottomAnimation
import com.lv.note.util.CommonUtils
import com.lv.note.util.isEmptyList
import com.lv.note.widget.EmptyView
import java.util.*

/**
 * User: 吕勇
 * Date: 2016-03-01
 * Time: 8:39
 * Description:可以爲RecyclerView添加HeaderView
 */

abstract class LBaseAdapter<T> @JvmOverloads constructor(protected var mLayoutResId: Int=0, protected  var mDatas: MutableList<T> = ArrayList<T>()) : RecyclerView.Adapter<BaseHolder>() {

    private var mOpenAnimationEnable = true
    private val mInterpolator = LinearInterpolator()
    private val mDuration = 300
    private var mLastPosition = -1
    private var mBaseAnimation: BaseAnimation? = null
    private var mHeaderView: View? = null
    private var mFooterView: View? = null
    var emptyView: EmptyView? = null
    protected var mFirstOnlyEnable = true
    protected var mLayoutInflater: LayoutInflater? = null
    protected var layoutParams: RelativeLayout.LayoutParams? = null

    private var mChildClickListener: OnRecyclerItemChildClickListener? = null

    /**
     * 设置每个Item中子View的点击事件
     */
    fun setOnRecyclerItemChildClickListener(childClickListener: OnRecyclerItemChildClickListener) {
        this.mChildClickListener = childClickListener
    }

    interface OnRecyclerItemChildClickListener {
        fun onItemChildClick(view: View, position: Int)
    }

    inner class OnItemChildClickListener : View.OnClickListener {
        var position: Int = 0

        override fun onClick(v: View) {
            if (mChildClickListener != null)
                mChildClickListener!!.onItemChildClick(v, position - headerViewsCount)
        }
    }


    init {
        this.mBaseAnimation = SlideInBottomAnimation()
    }


    fun addItems(items: List<T>?) {
        if (null != items) {
            val oldCont = mDatas.size
            mDatas.addAll(items)
            if (oldCont == 0)
                notifyDataSetChanged()
            else
                notifyItemRangeChanged(oldCont, mDatas.size)
        }
    }

    fun addItem(item: T) {
        mDatas.add(item)
        notifyDataSetChanged()
    }

    fun addItems(items: List<T>?, isRefresh: Boolean) {
        if (isRefresh) {
            mDatas.clear()
            notifyDataSetChanged()
        }
        addItems(items)
    }


    fun getItem(position: Int): T {
        return mDatas[position]
    }

    val headerViewsCount: Int
        get() = if (mHeaderView == null) 0 else 1

    val footerViewsCount: Int
        get() = if (mFooterView == null) 0 else 1

    fun getmEmptyViewCount(): Int {
        return if (emptyView == null) 0 else 1
    }

    override fun getItemCount(): Int {
        var count = mDatas.size + headerViewsCount + footerViewsCount
        if (headerViewsCount == 1 && count == 1 || count == 0)
            count += getmEmptyViewCount()
        return count
    }


    override fun getItemViewType(position: Int): Int {
        if (mHeaderView != null && position == 0) {
            return HEADER_VIEW
        } else if (emptyView != null && mDatas.size == 0) {
            return EMPTY_VIEW
        } else if (position == mDatas.size + headerViewsCount) {
            return FOOTER_VIEW
        }
        return getDefItemViewType(position)
    }

    protected open fun getDefItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val baseViewHolder: BaseHolder
        when (viewType) {
            HEADER_VIEW -> baseViewHolder = BaseHolder(mHeaderView!!)
            EMPTY_VIEW -> baseViewHolder = BaseHolder(emptyView!!)
            FOOTER_VIEW -> baseViewHolder = BaseHolder(mFooterView!!)
            else -> baseViewHolder = onCreateDefViewHolder(parent,viewType)
        }
        return baseViewHolder

    }


    override fun onBindViewHolder(baseHolder: BaseHolder, positions: Int) {
        when (baseHolder.itemViewType) {
            EMPTY_VIEW -> {
                if (layoutParams == null && mHeaderView != null && mHeaderView!!.parent is RecyclerView) {
                    val recyclerView = mHeaderView!!.parent as RecyclerView
                    layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, recyclerView.height - mHeaderView!!.height)
                    emptyView?.layoutParams=layoutParams
                }
                val networkAvailable = CommonUtils.isNetworkAvailable(emptyView!!.context)
                if (networkAvailable)
                    emptyView?.showEmptyView()
                else
                    emptyView?.showNetWorkError()
            }
            HEADER_VIEW, FOOTER_VIEW -> {
            }
            else -> {
                val pos = getRealPosition(baseHolder)
                baseHolder.itemView.tag = pos
                onBindItem(baseHolder.itemView, pos, mDatas[pos])
                addAnimation(baseHolder)
            }
        }

    }


    fun getRealPosition(holder: RecyclerView.ViewHolder): Int {
        return holder.layoutPosition - headerViewsCount
    }

    protected open fun onCreateDefViewHolder(parent: ViewGroup,viewType: Int): BaseHolder {
        return createBaseViewHolder(parent, mLayoutResId)
    }

    protected fun createBaseViewHolder(parent: ViewGroup, layoutResId: Int): BaseHolder {
        val baseViewHolder = BaseHolder(getItemView(layoutResId, parent))
        baseViewHolder.itemView.setOnClickListener { view ->
            val vTag = view.tag
            if (vTag != null && vTag is Int)
                onItemClick(view,mDatas[Integer.valueOf(vTag.toString())!!])
        }
        return baseViewHolder
    }

    protected open fun onItemClick(view: View,item: T) {

    }


    fun addHeaderView(header: View?) {
        if (header == null) {
            throw RuntimeException("header is null")
        }
        this.mHeaderView = header
        this.notifyDataSetChanged()
    }

    fun addFooterView(footer: View?) {
        if (footer == null) {
            throw RuntimeException("footer is null")
        }
        this.mFooterView = footer
        this.notifyDataSetChanged()
    }

    fun setFirstOnlyEnable(firstOnlyEnable: Boolean) {
        mFirstOnlyEnable = firstOnlyEnable
    }


    private fun addAnimation(holder: RecyclerView.ViewHolder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.layoutPosition > mLastPosition) {
                for (anim in mBaseAnimation!!.getAnimators(holder.itemView)) {
                    anim.setDuration(mDuration.toLong()).start()
                    anim.interpolator=mInterpolator
                }
                mLastPosition = holder.layoutPosition
            }
        }
    }

    fun remove(position:Int){
        mDatas.removeAt(position)
        notifyItemRemoved(position);
    }

    protected fun getItemView(layoutResId: Int, parent: ViewGroup): View {
        if (mLayoutInflater == null)
            mLayoutInflater = LayoutInflater.from(parent.context)
        return mLayoutInflater!!.inflate(layoutResId, parent, false)
    }


    fun setBaseAnimation(baseAnimation: BaseAnimation) {
        this.mOpenAnimationEnable = true
        this.mBaseAnimation = baseAnimation
    }

    fun setOpenAnimationEnable(openAnimationEnable: Boolean) {
        mOpenAnimationEnable = openAnimationEnable
    }

    fun isFirstOnly(firstOnly: Boolean) {
        this.mFirstOnlyEnable = firstOnly
    }

    abstract fun onBindItem(itemView: View, realPosition: Int, item: T)

    //需要处理瀑布流的时候再放开
    override fun onViewAttachedToWindow(baseHolder: BaseHolder?) {
        super.onViewAttachedToWindow(baseHolder)
        val lp = baseHolder!!.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
            val position = baseHolder.layoutPosition
            if (isHeaderView(position) || isBottomView(position)|| mDatas.isEmptyList()) {
                lp.isFullSpan = true
            }
        }
    }

    /*@Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isBottomView(position) || ArrayUtils.isEmpty(mDatas)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }*/

    //判断当前item是否是HeadView
    fun isHeaderView(position: Int): Boolean {
        return headerViewsCount != 0 && position < headerViewsCount
    }

    //判断当前item是否是FooterView
    fun isBottomView(position: Int): Boolean {
        return footerViewsCount != 0 && position >= headerViewsCount + mDatas.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    val isEmpty: Boolean
        get() = mDatas.isEmptyList()

    fun clearDatas() {
        if (!isEmpty) {
            mDatas.clear()
            notifyDataSetChanged()
        }

    }

    fun isLast(position: Int): Boolean {
        return mDatas.size - 1 == position
    }

    companion object {
        protected val HEADER_VIEW = 0x00000111
        protected val FOOTER_VIEW = 0x00000333
        protected val EMPTY_VIEW = 0x00000555
    }

}