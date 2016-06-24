package com.lv.note.base

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import com.lv.note.R
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.helper.BGARefreshDelegate
import com.lv.note.util.CommonUtils
import com.lv.note.widget.EmptyView
import com.lv.test.BaseActivity

/**
 * User: 吕勇
 * Date: 2016-05-04
 * Time: 14:52
 * Description:列表基类(layout中请包含common_recyclerview中的相关id)
 */
abstract class BaseRecyclerActivity<T> : BaseActivity(), BGARefreshDelegate.BGARefreshListener {

    protected var commonRefresh: BGARefreshLayout? = null
    protected var commonRecycler: RecyclerView? = null
    protected var mBaseAdapter: LBaseAdapter<T>? = null
    protected var emptyView: EmptyView? = null
    protected var mDelegate: BGARefreshDelegate? = null
    protected var loadMore = false
    protected var column = 0//在使用GridLayoutManager的时候请设置

    override fun loadLayoutId(): Int {
        return R.layout.common_title_recyclerview
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
        emptyView = EmptyView(this)
        mBaseAdapter?.emptyView = emptyView
    }

    protected abstract val lBaseAdapter: LBaseAdapter<T>


    protected fun addItems(items: List<T>?) {
        mBaseAdapter?.addItems(items, true)
    }

    protected fun stopRefreshing() {
        commonRefresh?.let {
            CommonUtils.showSuccess(this@BaseRecyclerActivity, commonRefresh!!, null)
        }
        mDelegate?.stopRefresh()
        mBaseAdapter?.let {
            if (mBaseAdapter!!.isEmpty)
                mBaseAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        mDelegate?.cleanListener()
        commonRefresh = null
        commonRecycler = null
        mBaseAdapter = null
        emptyView = null
        mDelegate = null
        super.onDestroy()
    }
}
