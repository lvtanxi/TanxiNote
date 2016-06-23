package com.lv.note.helper

import android.text.TextWatcher

/**
 * User: 吕勇
 * Date: 2016-06-21
 * Time: 10:46
 * Description:
 */
abstract class CustTextWatcher: TextWatcher{



    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}
