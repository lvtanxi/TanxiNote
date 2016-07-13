package com.lv.note.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.lv.note.R
import com.lv.note.helper.IBaseView
import com.lv.note.util.CommonUtils
import com.lv.note.util.ThemeUtils
import com.lv.note.util.ToastUtils
import com.lv.note.widget.LoadingDialog


/**
 * User: 吕勇
 * Date: 2016-06-12
 * Time: 15:06
 * Description:
 */
abstract class BaseActivity : AppCompatActivity(), IBaseView {
    protected var mLodingView: LoadingDialog? = null
    protected var mToolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeUtils.obtainCurrentTheme())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(loadLayoutId())
        initToolbar()
        initViews()
        initData()
        bindListener()
        processLogic()
    }


    protected fun initToolbar() {
        mToolbar = fdb(R.id.comm_toobar)
        if (mToolbar == null)
            return
        setSupportActionBar(mToolbar)
    }


    /**
     * 为Activity加载布局文件
     */
    protected abstract fun loadLayoutId(): Int

    /**
     * 初始化控件
     */
    protected abstract fun initViews()

    /**
     * 初始化数剧
     */
    protected open fun initData() {

    }

    /**
     * 为控件设置监听
     */
    protected open fun bindListener() {

    }

    /**
     * 逻辑操作，网络请求
     */
    protected open fun processLogic() {

    }

    protected fun <T : View> fdb(@IdRes viewId: Int): T? {
        val mView: View? = findViewById(viewId)
        return mView as T?
    }


    override fun onDestroy() {
        super.onDestroy()
        mToolbar = null
        mLodingView = null
    }

    override fun showLodingView() {
        if (null == mLodingView)
            mLodingView = LoadingDialog(this)
        mLodingView?.let {
            if (!mLodingView!!.isShowing)
                mLodingView!!.show();
        }
    }

    override fun hideLodingView() {
        mLodingView?.let {
            if (mLodingView!!.isShowing)
                mLodingView!!.dismiss()
        }
    }

    override fun toastError(message: String) {
        ToastUtils.textToastError(this, message)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            mToolbar?.let {
                CommonUtils.hiddenKeyBoard(mToolbar!!)
            }
            finishAfterTransition()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}