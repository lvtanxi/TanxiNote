package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewPager
import android.view.View
import com.ashokvarma.bottomnavigation.BadgeItem
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.lv.note.R
import com.lv.note.adapter.LBaseFragmentAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.util.changeTopBgColor


/**
 * User: 吕勇
 * Date: 2016-05-24
 * Time: 09:45
 * Description:
 */
class NewMainAct : BaseActivity() {

    companion object{
            val IS_NEW="IS_NEW"
            fun startNewMainAct(actvity: Activity){
                actvity.startActivity(Intent(actvity,NewMainAct::class.java))
            }
        }
    private var mViewPager: ViewPager? = null
    private var mBottomBar: BottomNavigationBar? = null
    private var mAddBtn: FloatingActionButton? = null

    override fun loadLayoutId(): Int {
        changeTopBgColor()
       return R.layout.act_new_main
    }

    override fun initViews() {
        mBottomBar = fdb(R.id.newmain_navigation_bar)
        mViewPager = fdb(R.id.newmain_view_pager)
        mAddBtn = fdb(R.id.newmain_float_button)
    }

    override fun initData() {
        val numberBadgeItem = BadgeItem().setBorderWidth(4).setBackgroundColorResource(R.color.brown).setText("10").setHideOnSelect(false)
        mBottomBar!!.addItem(BottomNavigationItem(R.drawable.ic_tab_home_normal, "主页").setBadgeItem(numberBadgeItem))
                .addItem(BottomNavigationItem(R.drawable.ic_tab_home_normal, "天气"))
                .addItem(BottomNavigationItem(R.drawable.ic_tab_home_normal, "我的"))
                .initialise()
        val adapter= LBaseFragmentAdapter(supportFragmentManager, arrayListOf(NotesFra(),WeatherFra(),NavigationFra()))
        mViewPager?.adapter=adapter
    }

    override fun bindListener() {
        mBottomBar?.setTabSelectedListener(object :BottomNavigationBar.OnTabSelectedListener{
            override fun onTabUnselected(position: Int) {
            }

            override fun onTabSelected(position: Int) {
                mViewPager?.setCurrentItem(position)
            }

            override fun onTabReselected(position: Int) {
            }
        })
        mViewPager?.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mAddBtn?.visibility=if(position==0) View.VISIBLE else View.GONE
            }

            override fun onPageSelected(position: Int) {
                mBottomBar?.selectTab(position,false)
            }

        })
    }



}
