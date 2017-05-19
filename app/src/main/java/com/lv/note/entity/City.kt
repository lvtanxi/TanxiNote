package com.lv.note.entity


/**
 * User: 吕勇
 * Date: 2016-06-24
 * Time: 11:41
 * Description:
 */
class City @JvmOverloads constructor(val name: String = "", var pinyin: String = "", var letter: String = "", var type: Int = 0) : LMultiItem {
    override val itemType: Int
        get() = type
}
