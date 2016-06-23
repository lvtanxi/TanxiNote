package com.lv.test

import com.lv.note.BuildConfig
import com.orhanobut.logger.Logger


/**
 * 日志打印
 */
object DLog {

    private val sDebug = BuildConfig.DEBUG

    fun d(obj: Any?) {
        if (sDebug) {
            var message = obj
            if (message == null)
                message = "打印了空消息"
            Logger.d(message.toString())
        }
    }

}
