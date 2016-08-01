package com.lv.note.ui

import android.content.Intent
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.entity.Person
import com.lv.note.helper.FindListenerSub
import com.lv.note.util.CountDown
import com.lv.note.util.changeTopBgColor
import com.orhanobut.hawk.Hawk


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 09:33
 * Description:启动界面
 */
class LaunchAct : BaseActivity(), CountDown.CountDownBack {

    private var mCountDown: CountDown? = null
    val param = "isFrist"

    override fun loadLayoutId(): Int {
        return R.layout.act_launch
    }

    override fun initViews() {
    }

    override fun initData() {
        mCountDown = CountDown(1000)
        changeTopBgColor()
    }


    override fun onResume() {
        super.onResume()
        mCountDown?.start()
    }

    override fun onPause() {
        super.onPause()
        mCountDown?.cancel()
    }

    override fun bindListener() {
        mCountDown?.setDownBack(this)
    }

    override fun countDownFinish() {
        findUser()
        var mCl:Class<*> = LoginAct::class.java
        if (App.getInstance().getPerson() == null)
            mCl = LoginAct::class.java
        else if(Hawk.get(NewMainAct.IS_NEW,true))
            mCl = NewMainAct::class.java
        else
            mCl = MainAct::class.java
        startActivity(Intent(this,mCl))
        finish()
    }

    private fun findUser() {
        val query = BmobQuery<Person>("Person")
        query.addWhereEqualTo("name", "我是的")
        query.addWhereEqualTo("pwd", "123123")
        query.findObjects(this, object : FindListenerSub<Person>(this, false) {
            override fun onSuccess(p0: MutableList<Person>) {
            }
        })
    }
}


