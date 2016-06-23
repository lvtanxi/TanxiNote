package com.lv.note.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.lv.test.ArrayUtils
import java.util.*

/**
 * User: 吕勇
 * Date: 2016-05-05
 * Time: 14:39
 * Description:FragmentStatePagerAdapter基类
 */
class LBaseFragmentAdapter : FragmentStatePagerAdapter {
    private var mFragments: List<Fragment>? = null
    private var mTitles: Array<String>? = null

    constructor(fm: FragmentManager, fragments: List<Fragment>, titles: Array<String>) : super(fm) {
        mFragments = fragments
        mTitles = titles
    }

    constructor(fm: FragmentManager, fragments: Array<Fragment>, vararg titles: String) : super(fm) {
        mFragments = Arrays.asList(*fragments)
        mTitles = titles as Array<String>
    }

    constructor(fm: FragmentManager, fragments: List<Fragment>) : super(fm) {
        mFragments = fragments
    }

    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return if (ArrayUtils.isEmpty(mFragments)) 0 else mFragments!!.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (ArrayUtils.isEmpty(mFragments)) "" else mTitles!![position]
    }
}
