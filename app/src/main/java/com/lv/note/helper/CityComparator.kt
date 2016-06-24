package com.lv.note.helper

import com.lv.note.entity.City
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-06-24
 * Time: 13:33
 * Description:
 */
class CityComparator : Comparator<City> {
    override fun compare(lhs: City?, rhs: City?): Int {
        val a = lhs!!.pinyin.substring(0, 1)
        val b = rhs!!.pinyin.substring(0, 1)
        return a.compareTo(b)
    }
}