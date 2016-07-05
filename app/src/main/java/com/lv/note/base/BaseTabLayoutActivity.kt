package com.lv.note.base

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import com.lv.note.R

import com.lv.note.adapter.LBaseFragmentAdapter
import com.lv.note.base.BaseActivity

/**
 * User: 吕勇
 * Date: 2016-05-09
 * Time: 14:15
 * Description:公用TabLayout的基类
 */
abstract class BaseTabLayoutActivity : BaseActivity() {
    protected var mTabLayout: TabLayout? = null
    protected var mViewPager: ViewPager? = null
    protected var mTitles: Array<String>? = null
    protected var mFragmentAdapter: LBaseFragmentAdapter? = null


    override fun initViews() {
        mTabLayout = fdb(R.id.common_tab_layout);
        mViewPager = fdb(R.id.common_view_pager);
    }

    override fun initData() {
        addTbs()
        mFragmentAdapter?.let {
            mViewPager?.adapter = mFragmentAdapter
            mTabLayout?.setupWithViewPager(mViewPager)
            mTabLayout?.setTabsFromPagerAdapter(mFragmentAdapter)
        }
    }

    protected fun addTbs() {
        mTitles?.let {
            for (title in mTitles!!) {
                mTabLayout!!.addTab(mTabLayout!!.newTab().setText(title))
            }
        }

    }

    override fun onDestroy() {
        mTabLayout = null
        mViewPager = null
        mTitles = null
        mFragmentAdapter = null
        super.onDestroy()
    }
}
