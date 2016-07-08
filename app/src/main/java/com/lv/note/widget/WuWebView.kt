package com.lv.note.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.AbsoluteLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.lv.note.R
import com.lv.note.util.ToastUtils


/**
 * User: 吕勇
 * Date: 2016-04-29
 * Time: 12:39
 * Description:WuWebView
 */
class WuWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {
    private var progressBar: ProgressBar? = null
    private var titleTv: TextView? = null
    private var mMap: Map<String, String>? = null
    private var mLoadFinish: WebViewLoadInterface? = null
    private var mBuilder: AlertDialog.Builder? = null

    init {
        initWebView()
    }

    /**
     * 初始化WebView
     */
    private fun initWebView() {
        clearCache(true)
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBar!!.progressDrawable = ContextCompat.getDrawable(context, R.drawable.webview_progress_bar)
        progressBar!!.layoutParams = AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 5, 0, 0)
        addView(progressBar)
        val webSettings = this.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.useWideViewPort = true//关键点
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.allowFileAccess = true
        settings.useWideViewPort=true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        requestFocus()
        requestFocusFromTouch()
        setWebChromeClient(MyWebChromeClient())
        setWebViewClient(MyWebViewClient())
        setDownloadListener(MyWebViewDownLoadListener())
    }

    override fun onDetachedFromWindow() {
        mMap = null
        mLoadFinish = null
        progressBar = null
        super.onDetachedFromWindow()
    }

    override fun loadUrl(url: String) {
        super.loadUrl(url, mMap)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        super.loadUrl(url, additionalHttpHeaders)
        this.mMap = additionalHttpHeaders
    }


    /**
     * 显示标题的view

     * @param title textview
     */
    fun setTitleView(title: TextView) {
        this.titleTv = title
    }

    /**
     * 设置加载字符串

     * @param data
     */
    fun loadTextData(data: String) {
        loadData(data, "text/html", "utf-8")
    }


    inner class MyWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            if (null != titleTv)
                titleTv!!.text = title
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progressBar?.let {
                if (newProgress == 100) {
                    progressBar!!.visibility = View.GONE
                } else {
                    if (progressBar!!.visibility == View.GONE)
                        progressBar!!.visibility = View.VISIBLE
                    progressBar!!.progress = newProgress
                }
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            if (null == mBuilder) {
                mBuilder = AlertDialog.Builder(context)
                mBuilder!!.setTitle("檀溪提示您:")
                mBuilder!!.setCancelable(false)
                mBuilder!!.setPositiveButton("确定") { dialog, which -> result!!.confirm() }
                mBuilder!!.setNegativeButton("取消") { dialog, which -> result!!.cancel() }
                mBuilder!!.setOnKeyListener { dialogInterface, i, keyEvent -> true}
            }
            mBuilder!!.setMessage(message)
            mBuilder!!.create()
            mBuilder!!.show()
            return true
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            ToastUtils.textToastError(view.context, message)
            result.confirm()
            return true
        }
    }

    private inner class MyWebViewDownLoadListener : DownloadListener {

        override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }

    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()//接受证书
        }


        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (null != mLoadFinish)
                mLoadFinish!!.onLoadFinish()
        }

        override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
            resend?.sendToTarget()
            super.onFormResubmission(view, dontResend, resend)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("tel:")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                return true
            } else if (url.startsWith("http") && view.hitTestResult == null) {
                view.loadUrl(url);// 自身加载新连接不做外部跳转
                return true;
            }
            return false
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val lp = progressBar!!.layoutParams as AbsoluteLayout.LayoutParams
        lp.x = l
        lp.y = t
        progressBar!!.layoutParams = lp
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun setLoadFinish(loadInterface: WebViewLoadInterface) {
        mLoadFinish = loadInterface
    }

    /**
     * 返回按键监听
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.canGoBack()) {
            this.goBack()
            if (null != mLoadFinish)
                mLoadFinish!!.onBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    interface WebViewLoadInterface {
        fun onLoadFinish()
        fun onBack()
    }

}
