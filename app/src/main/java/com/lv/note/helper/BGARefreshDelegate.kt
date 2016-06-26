package com.lv.note.helper

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder
import com.lv.note.R

/**
 * User: 吕勇
 * Date: 2016-05-04
 * Time: 09:18
 * Description:下拉刷新使用
 */
class BGARefreshDelegate : BGARefreshLayout.BGARefreshLayoutDelegate {

    private var mRefreshLayout: BGARefreshLayout? = null
    var isMore: Boolean = false
        private set
    private var mRefreshListener: BGARefreshListener? = null


    constructor(refreshLayout: BGARefreshLayout, refreshListener: BGARefreshListener, loadMore: Boolean) {
        initBga(refreshListener, refreshLayout)
        mRefreshLayout!!.setRefreshViewHolder(getRefreshViewHolder(refreshLayout.context, loadMore))
    }

    @JvmOverloads constructor(refreshLayout: BGARefreshLayout, refreshListener: BGARefreshListener, recyclerView: RecyclerView?, loadMore: Boolean, column: Int = 0) {
        initBga(refreshListener, refreshLayout)
        if (null != recyclerView)
            mRefreshLayout!!.setRefreshViewHolder(getBGARefreshViewHolder(recyclerView, loadMore, column))
        else
            mRefreshLayout!!.setRefreshViewHolder(getRefreshViewHolder(refreshLayout.context, loadMore))
    }

    private fun initBga(refreshListener: BGARefreshListener, refreshLayout: BGARefreshLayout) {
        try {
            mRefreshListener = refreshListener
            mRefreshLayout = refreshLayout
            if (mRefreshListener == null || mRefreshLayout == null)
                throw Exception("BGARefreshListener或者BGARefreshLayout为空")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout) {
        isMore = false
        mRefreshListener!!.onBGARefresh()
    }

    override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout): Boolean {
        isMore = true
        return mRefreshListener!!.onBGARefresh()
    }

    fun stopRefresh() {
        if (null == mRefreshLayout)
            return
        if (isMore)
            mRefreshLayout!!.endLoadingMore()
        else
            mRefreshLayout!!.endRefreshing()
    }


    /**
     * 获取RecyclerView的RefreshViewHolder

     * @param recyclerView RecyclerView
     * *
     * @param loadMore     是否加载更多
     */
    fun getBGARefreshViewHolder(recyclerView: RecyclerView, loadMore: Boolean, column: Int): BGARefreshViewHolder {
        if (column > 0)
            recyclerView.layoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL)
        else
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        return getRefreshViewHolder(recyclerView.context, loadMore)
    }

    /**
     * 获取其他空间的RefreshViewHolder

     * @param loadMore 是否加载更多
     */
    fun getRefreshViewHolder(context: Context, loadMore: Boolean): BGARefreshViewHolder {
        val stickinessRefreshViewHolder = BGAStickinessRefreshViewHolder(context, loadMore)
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary)
        stickinessRefreshViewHolder.setRotateImage(R.drawable.bga_refresh_stickiness)
        stickinessRefreshViewHolder.setLoadingMoreText("")
        return stickinessRefreshViewHolder
    }

    interface BGARefreshListener {
        fun onBGARefresh(): Boolean
    }

    fun cleanListener() {
        mRefreshListener = null
    }
}
