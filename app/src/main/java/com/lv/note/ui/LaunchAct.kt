package com.lv.note.ui

import android.content.Intent
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.entity.Person
import com.lv.note.helper.FindListenerSub
import com.lv.note.util.changeTopBgColor
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 09:33
 * Description:启动界面
 */
class LaunchAct : BaseActivity(){

    val param = "isFrist"

    private var mSubscribe: Subscription? =null
    override fun loadLayoutId(): Int {
        return R.layout.act_launch
    }

    override fun initViews() {
    }

    override fun initData() {
        changeTopBgColor()
    }


    override fun onResume() {
        super.onResume()
        mSubscribe=Observable.timer(800,TimeUnit.MILLISECONDS)
                .subscribe {
                    var mCl:Class<*> = LoginAct::class.java
                    if (App.getInstance().getPerson() == null)
                    mCl = LoginAct::class.java
                    else
                    mCl = HomeAct::class.java
                    startActivity(Intent(this,mCl))
                    finish()
                }
        addSubscription(mSubscribe)
    }

    override fun onPause() {
        super.onPause()
        mCompositeSubscription?.remove(mSubscribe)
    }

    override fun processLogic() {
        val query = BmobQuery<Person>("Person")
        query.addWhereEqualTo("name", "我是的")
        query.addWhereEqualTo("pwd", "123123")
        addSubscription(query.findObjects(object :FindListenerSub<Person>(this,false){
            override fun onSuccess(result: MutableList<Person>) {
            }
        }))
    }

}


