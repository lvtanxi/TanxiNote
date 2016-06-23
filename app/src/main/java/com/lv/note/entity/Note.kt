package com.lv.note.entity

import cn.bmob.v3.BmobObject
import java.io.Serializable

/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 15:33
 * Description:
 */
class Note : BmobObject(), Serializable {
    var userId = ""
    var note = ""
    var year =""
    var time =""
    var status="1"
}
