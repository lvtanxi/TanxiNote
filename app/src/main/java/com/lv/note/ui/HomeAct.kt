package com.lv.note.ui

import android.content.Intent
import android.support.v4.view.ViewPager
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
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-08-02
 * Time: 13:06
 * Description:
 */
class HomeAct : BaseActivity() {

    private var mActionBarDrawerToggle: ActionBarDrawerToggle? = null;
    private var mAdapter: LBaseFragmentAdapter? = null;

    override fun loadLayoutId(): Int {
        changeTopBgColor()
        return R.layout.act_main
    }

    override fun processLogic() {
        super.processLogic()
        XiaomiUpdateAgent.update(this)
    }

    override fun initData() {
        main_search_view.adjustTintAlpha(0.8f)
        main_search_view.setHint("请输入关键字")
        mActionBarDrawerToggle = ActionBarDrawerToggle(this, mian_drawer_layout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle?.syncState()
        mAdapter = LBaseFragmentAdapter(supportFragmentManager, Arrays.asList(NotesFra(), WeatherFra()), arrayOf("笔记", "天气"))
        main_view_pager.adapter = mAdapter
        indicator.setViewPager(main_view_pager)
    }

    override fun bindListener() {
        mian_drawer_layout.addDrawerListener(mActionBarDrawerToggle!!)
        main_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter?.let {
                    val mNotesFra: NotesFra = mAdapter!!.getItem(0) as NotesFra
                    mNotesFra.doSearch(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        main_search_view.setSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewOpened() {
                LAnimUtils.hideView(main_float_button)
            }

            override fun onSearchViewClosed() {
                LAnimUtils.showView(main_float_button)
            }
        })

        main_search_view.setOnItemClickListener { parent, view, position, id ->
            val suggestion = main_search_view.getSuggestionAtPosition(position)
            main_search_view.setQuery(suggestion, false)
        }

        main_float_button.setOnClickListener {
            AddNoteAct.startAddNoteAct(this, null, main_float_button)
        }
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                LAnimUtils.hideOrShowView(main_float_button, position == 0)
                invalidateOptionsMenu()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (main_view_pager != null && main_view_pager.currentItem == 0)
            menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (null != main_search_view && main_search_view!!.isOpen()) {
            main_search_view.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        main_search_view.activityResumed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_search) {
            main_search_view.openSearch()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_nvg)
        fragment?.let {
            fragment!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}