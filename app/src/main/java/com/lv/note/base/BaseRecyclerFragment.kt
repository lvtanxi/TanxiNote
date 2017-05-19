package com.lv.note.base

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import com.lv.note.R
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.helper.BGARefreshDelegate
import com.lv.note.widget.EmptyView

/**
 * User: 吕勇
 * Date: 2016-03-28
 * Time: 17:52
 * Description:列表基类
 */
abstract class BaseRecyclerFragment<T> : BaseFragment(), BGARefreshDelegate.BGARefreshListener {
    protected var commonRefresh: BGARefreshLayout? = null
    protected var commonRecycler: RecyclerView? = null
    protected var mBaseAdapter: LBaseAdapter<T>? = null
    protected var emptyView: EmptyView? = null
    protected var mDelegate: BGARefreshDelegate? = null
    protected var loadMore = false
    protected var column = 0//在使用GridLayoutManager的时候请设置

    override fun loadLayoutId(): Int {
        seavStatus = true
        return R.layout.common_recyclerview
    }

    override fun initViews() {
        commonRefresh = fdb(R.id.common_recyclerview_refresh)
        commonRecycler = fdb(R.id.common_recyclerview)
    }

    override fun bindListener() {
        commonRefresh?.setDelegate(mDelegate)
    }

    override fun processLogic() {
        commonRefresh?.beginRefreshing()
    }

    override fun initData() {
        mDelegate = BGARefreshDelegate(commonRefresh!!, this, commonRecycler, loadMore, column)
        mBaseAdapter = lBaseAdapter
        commonRecycler?.itemAnimator = DefaultItemAnimator()
        commonRecycler?.adapter = mBaseAdapter
        emptyView = EmptyView(activity)
        mBaseAdapter?.emptyView = emptyView
    }

    protected abstract val lBaseAdapter: LBaseAdapter<T>


    protected fun addItems(items: List<T>) {
        addItems(items, true)
    }

    protected fun addItems(items: List<T>,isRefresh: Boolean) {
        mBaseAdapter?.addItems(items, isRefresh)
    }

    protected fun stopRefreshing() {
        mDelegate?.stopRefresh()
        mBaseAdapter?.let {
            if (mBaseAdapter!!.isEmpty)
                mBaseAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        mDelegate?.cleanListener()
        commonRefresh = null
        commonRecycler = null
        mBaseAdapter = null
        emptyView = null
        mDelegate = null
        super.onDestroyView()
    }
}
