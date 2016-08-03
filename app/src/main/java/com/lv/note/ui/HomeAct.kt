package com.lv.note.ui

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import br.com.mauker.materialsearchview.MaterialSearchView
import com.lv.note.R
import com.lv.note.adapter.LBaseFragmentAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.util.LAnimUtils
import com.lv.note.util.changeTopBgColor
import com.xiaomi.market.sdk.XiaomiUpdateAgent
import github.chenupt.springindicator.SpringIndicator
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-08-02
 * Time: 13:06
 * Description:
 */
class HomeAct : BaseActivity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var mActionBarDrawerToggle: ActionBarDrawerToggle? = null;
    private var mAdapter: LBaseFragmentAdapter? = null;
    private var mViewPager: ViewPager? = null
    private var mSpringIndicator: SpringIndicator? = null
    private var mSearchView: MaterialSearchView? = null
    private var mAddBtn: FloatingActionButton? = null

    override fun loadLayoutId(): Int {
        changeTopBgColor()
        return R.layout.act_main
    }

    override fun processLogic() {
        super.processLogic()
        XiaomiUpdateAgent.update(this)
    }

    override fun initViews() {
        mDrawerLayout = fdb(R.id.mian_drawer_layout)
        mViewPager = fdb(R.id.main_view_pager)
        mSpringIndicator = fdb(R.id.indicator)
        mSearchView = fdb(R.id.main_search_view)
        mAddBtn = fdb(R.id.main_float_button)
    }

    override fun initData() {
        mSearchView?.adjustTintAlpha(0.8f)
        mSearchView?.setHint("请输入关键字(clean表示查询全部)")
        mActionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle?.syncState()
        mAdapter= LBaseFragmentAdapter(supportFragmentManager, Arrays.asList(NotesFra(), WeatherFra()), arrayOf("笔记", "天气"))
        mViewPager?.adapter =mAdapter
        mSpringIndicator?.setViewPager(mViewPager)
    }

    override fun bindListener() {
        mDrawerLayout?.addDrawerListener(mActionBarDrawerToggle!!)
        mSearchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter?.let {
                    val mNotesFra:NotesFra= mAdapter!!.getItem(0) as NotesFra
                    mNotesFra.doSearch(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mSearchView?.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewOpened() {
                LAnimUtils.hideView(mAddBtn)
            }

            override fun onSearchViewClosed() {
                LAnimUtils.showView(mAddBtn)
            }
        })

        mSearchView?.setOnItemClickListener { parent, view, position, id ->
            val suggestion =  mSearchView?.getSuggestionAtPosition(position)
            mSearchView?.setQuery(suggestion, false)
        }

        mAddBtn?.setOnClickListener {
            AddNoteAct.startAddNoteAct(this, null, mAddBtn!!)
        }
        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                LAnimUtils.hideOrShowView(mAddBtn, position == 0)
                invalidateOptionsMenu()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(mViewPager!=null&&mViewPager!!.currentItem==0)
            menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (null != mSearchView && mSearchView!!.isOpen()) {
            mSearchView?.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mSearchView?.activityResumed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_search) {
            mSearchView?.openSearch()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragment= supportFragmentManager.findFragmentById(R.id.main_nvg)
        fragment?.let {
            fragment!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}