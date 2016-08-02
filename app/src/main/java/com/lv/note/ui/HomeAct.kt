package com.lv.note.ui

import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import br.com.mauker.materialsearchview.MaterialSearchView
import com.lv.note.R
import com.lv.note.adapter.LBaseFragmentAdapter
import com.lv.note.base.BaseActivity
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
    private var mViewPager: ViewPager? = null
    private var mSpringIndicator: SpringIndicator? = null
    private var mSearchView: MaterialSearchView? = null

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
    }

    override fun initData() {
        mSearchView?.adjustTintAlpha(0.8f)
        mActionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle?.syncState()
        mViewPager?.adapter = LBaseFragmentAdapter(supportFragmentManager, Arrays.asList(NotesFra(), WeatherFra()), arrayOf("笔记", "天气"))
        mSpringIndicator?.setViewPager(mViewPager)
        mSearchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        mSearchView?.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewOpened() {
                // Do something once the view is open.
            }

            override fun onSearchViewClosed() {
                // Do something once the view is closed.
            }
        })
    }

    override fun bindListener() {
        mDrawerLayout?.addDrawerListener(mActionBarDrawerToggle!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onBackPressed() {
        if (null != mSearchView && mSearchView!!.isOpen()) {
            mSearchView?.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_search) {
            mSearchView?.openSearch()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}