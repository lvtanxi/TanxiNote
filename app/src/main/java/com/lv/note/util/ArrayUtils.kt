package com.lv.test


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 08:49
 * Description:集合操作
 */
object ArrayUtils {

    fun isEmpty(array: List<*>?): Boolean {
        if (array == null || array.size == 0)
            return true
        return false
    }

    fun isNotEmpty(array: List<*>?): Boolean {
        if (array != null && array.size > 0)
            return true
        return false
    }

    fun arrayEmpty(array: Array<Any>?): Boolean {
        if (array == null || array.size == 0)
            return true
        return false
    }

    fun arrayNotEmpty(array: Array<Any>?): Boolean {
        if (array != null && array.size > 0)
            return true
        return false
    }

    fun mapEmpty(map:Map<*,*>?):Boolean{
        if (map == null || map.size == 0)
            return true
        return false
    }

    fun mapNotEmpty(map:Map<*,*>?):Boolean{
        if (map != null && map.size > 0)
            return true
        return false
    }


}