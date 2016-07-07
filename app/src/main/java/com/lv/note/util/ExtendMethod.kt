package com.lv.note.util


/**
 * User: 吕勇
 * Date: 2016-07-07
 * Time: 09:53
 * Description:
 */

fun String?.isEmptyStr(): Boolean {
    return this == null || this.isEmpty()
}

fun String?.notEmptyStr(): Boolean {
    return this != null && !this.isEmpty()
}

fun List<*>?.isEmptyList():Boolean{
    return this == null || this.isEmpty()
}

fun List<*>?.notEmptyList():Boolean{
    return this != null || !this!!.isEmpty()
}

