package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.widget.WuWebView


/**
 * User: 吕勇
 * Date: 2016-06-14
 * Time: 17:31
 * Description:
 */
class WebViewAct : BaseActivity() {

    private var mWebView: WuWebView? = null

    companion object {
        val URL_PARAM = "URL_PARAM"
        val TITLE_PARAM = "TITLE_PARAM"
        fun startWebViewAct(actvity: Activity, url: String,title:String) {
            actvity.startActivity(Intent(actvity, WebViewAct::class.java)
                    .putExtra(URL_PARAM, url)
                    .putExtra(TITLE_PARAM,title))
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_webview
    }

    override fun initViews() {
        mWebView = fdb(R.id.web_web_view);
    }

    override fun initData() {
        mToolbar!!.title=intent.getStringExtra(TITLE_PARAM)
    }

    override fun processLogic() {
        mWebView!!.loadUrl(intent.getStringExtra(URL_PARAM))
    }


    override fun onDestroy() {
        mWebView!!.clearHistory()
        mWebView!!.destroy()
        super.onDestroy()
    }
}