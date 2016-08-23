package com.lv.note.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.entity.Person
import com.lv.note.helper.FindListenerSub
import com.lv.note.util.changeTopBgColor
import kotlinx.android.synthetic.main.act_launch.*
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 09:33
 * Description:启动界面
 */
class LaunchAct : BaseActivity() {

    val param = "isFrist"
    private var mAnimatorSet:AnimatorSet? =null

    private var mSubscribe: Subscription? = null
    override fun loadLayoutId(): Int {
        return R.layout.act_launch
    }


    override fun initData() {
        changeTopBgColor()
    }


    override fun onResume() {
        super.onResume()
        mSubscribe = Observable.timer(1000, TimeUnit.MILLISECONDS)
                .doOnSubscribe {  startAnim() }
                .map { num -> if (App.getInstance().getPerson() == null) LoginAct::class.java else HomeAct::class.java }
                .subscribe { restule ->
                        startActivity(Intent(this, restule))
                        finish()
                }
        addSubscription(mSubscribe)
    }

    fun startAnim() {
        mAnimatorSet = AnimatorSet()
        val scaleX = ObjectAnimator.ofFloat(launch_lay, "scaleX", 1f, 1.12f)
        val scaleY = ObjectAnimator.ofFloat(launch_lay, "scaleY", 1f, 1.12f)
        mAnimatorSet?.setDuration(1000)?.play(scaleX)?.with(scaleY)
        mAnimatorSet?.start()
    }

    override fun onPause() {
        super.onPause()
        mCompositeSubscription?.remove(mSubscribe)
        mAnimatorSet?.cancel()
    }


    override fun processLogic() {
        val query = BmobQuery<Person>("Person")
        query.addWhereEqualTo("name", "我是的")
        query.addWhereEqualTo("pwd", "123123")
        addSubscription(query.findObjects(object : FindListenerSub<Person>(this, false) {
            override fun onSuccess(result: MutableList<Person>) {
            }
        }))
    }

}


