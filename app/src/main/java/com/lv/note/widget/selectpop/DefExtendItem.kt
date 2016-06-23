package com.lv.note.widget.selectpop

/**
 * User: 吕勇
 * Date: 2016-02-18
 * Time: 12:22
 * Description:默认的选择项模式
 */
class DefExtendItem(private var index: Int,var txt: String) : ExtendItem {

    override val value: String
        get() = txt

    override val id: String
        get() ="$index"

}
