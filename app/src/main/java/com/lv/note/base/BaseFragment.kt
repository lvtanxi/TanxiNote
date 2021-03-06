package com.lv.note.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lv.note.util.ToastUtils
import com.lv.test.BaseActivity

/**
 * User: 吕勇
 * Date: 2016-03-17
 * Time: 09:38
 * Description:所有Fragment的基类
 */
abstract class BaseFragment : Fragment() {
    protected var contentView: View? = null
    protected var mBaseActivity: BaseActivity? = null
    protected var seavStatus = true

    override fun onAttach(activity: Context?) {
        mBaseActivity = getActivity() as BaseActivity
        super.onAttach(activity)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (seavStatus) {
            if (contentView == null)
                initFragment(inflater)

            contentView?.parent?.let {
                val parent = contentView!!.parent as ViewGroup
                parent.removeView(contentView)
            }

        } else {
            initFragment(inflater)
        }
        return contentView
    }


    private fun initFragment(inflater: LayoutInflater) {
        this.contentView = inflater.inflate(loadLayoutId(), null)
        initViews()
        initData()
        bindListener()
        processLogic()
    }

    /**
     * 为Fragment加载布局
     */
    protected abstract fun loadLayoutId(): Int

    /**
     * 初始化控件
     */
    protected abstract fun initViews()

    /**
     * 初始化数
     */
    protected abstract fun initData()

    /**
     * 为控件设置监
     */
    protected open fun bindListener() {

    }

    /**
     * 逻辑操作，网络请求
     */
    protected open fun processLogic() {

    }


    /**
     * 控件点击回调
     */
    protected fun onClick(view: View, id: Int) {

    }

    protected fun <T : View> fdb(@IdRes viewId: Int): T? {
        val mView:View?=contentView!!.findViewById(viewId)
        return mView as T?
    }

    protected fun <T : View> fdb(view: View, @IdRes viewId: Int): T {
        return view.findViewById(viewId) as T
    }

    protected  fun toastError(message: String) {
        ToastUtils.textToastError(mBaseActivity!!, message)
    }

    override fun onDestroyView() {
        contentView = null
        mBaseActivity = null
        super.onDestroyView()
    }
}
