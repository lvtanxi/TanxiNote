package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.util.openNewAct
import kotlinx.android.synthetic.main.act_webview.*


/**
 * User: 吕勇
 * Date: 2016-06-14
 * Time: 17:31
 * Description:
 */
class WebViewAct : BaseActivity() {


    companion object {
        val URL_PARAM = "URL_PARAM"
        val TITLE_PARAM = "TITLE_PARAM"
        fun startWebViewAct(actvity: Activity, tagView: View,url: String, title:String) {
            actvity.openNewAct(Intent(actvity, WebViewAct::class.java)
                    .putExtra(URL_PARAM, url)
                    .putExtra(TITLE_PARAM,title),tagView)
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_webview
    }


    override fun initData() {
        mToolbar!!.title=intent.getStringExtra(TITLE_PARAM)
    }

    override fun processLogic() {
        web_web_view.loadUrl(intent.getStringExtra(URL_PARAM))
    }


    override fun onDestroy() {
        web_web_view.clearHistory()
        web_web_view.destroy()
        super.onDestroy()
    }
}