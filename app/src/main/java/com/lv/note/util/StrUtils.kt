package com.lv.test


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 09:04
 * Description:字符串操作类
 */
object StrUtils {

    fun isEmpty(str: String?): Boolean {
        if (null == str || str.trim().length == 0)
            return true
        return false
    }

    fun notEmpty(str: String?): Boolean {
        if (null != str && str.trim().length > 0)
            return true
        return false
    }
}