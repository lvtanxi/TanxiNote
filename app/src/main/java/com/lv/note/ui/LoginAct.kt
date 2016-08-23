package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.entity.Person
import com.lv.note.helper.ActionBack
import com.lv.note.helper.CustTextWatcher
import com.lv.note.helper.FindListenerSub
import com.lv.note.helper.SaveListenerSub
import com.lv.note.util.CommonUtils
import com.lv.note.util.changeTopBgColor
import com.lv.note.util.isEmptyList
import com.lv.note.util.isNumeric
import com.lv.test.DLog
import com.orhanobut.hawk.Hawk
import com.plattysoft.leonids.ParticleSystem
import com.sdsmdg.tastytoast.TastyToast
import com.tencent.connect.UserInfo
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.xiaomi.market.sdk.XiaomiUpdateAgent
import kotlinx.android.synthetic.main.act_login.*
import org.json.JSONObject


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 10:02
 * Description:登录界面
 */
class LoginAct : BaseActivity() {
    private var ps: ParticleSystem ? = null
    private var mTencent: Tencent? = null


    companion object {
        val APPID = "1105488020"
        val USER_NAME = "USER_PHONE"
        fun startLoginAct(actvity: Activity) {
            actvity.startActivity(Intent(actvity,LoginAct::class.java))
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_login
    }


    override fun initData() {
        changeTopBgColor()
        if(Hawk.get(USER_NAME, "").isNumeric())
            login_name.setText(Hawk.get(USER_NAME, ""))
        if (login_name.text.length != 0) {
            login_pwd.isFocusable = true
            login_pwd.isFocusableInTouchMode = true
            login_pwd.requestFocus()
        }
        mTencent = Tencent.createInstance(APPID, applicationContext)
    }

    override fun bindListener() {
        login_name.addTextChangedListener(object : CustTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                stopHeartProgressBar()
                if (login_name.text.length == 11)
                    loadImage(login_name.text.toString())
                else
                    login_image.setImageResource(R.drawable.header)
            }
        })
        login_pwd.addTextChangedListener(object : CustTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                stopHeartProgressBar()
            }
        })
        login_sub.setOnClickListener { doLogin() }

        login_what.setOnClickListener {
            TastyToast.makeText(getApplicationContext(), "用户是根据后端判断是否存在,所以没用注册界面,请见谅.也请妥善管理自己的用户名和密码!", TastyToast.LENGTH_LONG, TastyToast.INFO)
        }
        login_qq.setOnClickListener {
            mTencent?.login(this, "all", loginListener)
        }
    }


    private val loginListener = object : IUiListener {

        override fun onComplete(value: Any) {
            try {
                if (value == null)
                    return
                val jo = value as JSONObject

                if (jo.has("ret") && jo.optInt("ret") == 0) {
                    DLog.d(jo)
                    val openID = jo.getString("openid")
                    val accessToken = jo.getString("access_token")
                    val expires = jo.getString("expires_in")
                    mTencent?.setOpenId(openID)
                    mTencent?.setAccessToken(accessToken, expires)
                    val userInfo = UserInfo(this@LoginAct, mTencent?.getQQToken())
                    userInfo.getUserInfo(object : IUiListener {
                        override fun onComplete(reult: Any?) {
                            if (reult == null)
                                return
                            try {
                                val jo = reult as JSONObject
                                if (jo.has("ret") && jo.optInt("ret") == 0) {
                                    val imageUrl = jo.getString("figureurl_qq_2")
                                    val mPerson = Person()
                                    mPerson.name = openID
                                    mPerson.pwd = openID
                                    mPerson.header=imageUrl
                                    httpFindUser(mPerson)
                                    CommonUtils.displayRoundImage(login_image, imageUrl)
                                }
                            } catch (e: Exception) {
                                toastError("获取用户信息失败")
                            }
                        }

                        override fun onCancel() {
                        }

                        override fun onError(p0: UiError?) {
                            toastError("获取用户信息失败")
                        }

                    })
                }

            } catch (e: Exception) {
                toastError("获取用户信息失败")
            }
        }

        override fun onError(uiError: UiError) {
            toastError("获取用户信息失败")
        }

        override fun onCancel() {

        }

    }

    fun stopHeartProgressBar() {
        if ((login_name.text.length == 11) && (login_pwd.text.length >= 6)) {
            login_progress.dismiss()
            login_sub.visibility = View.VISIBLE
            return
        }
        login_sub.visibility = View.GONE
        login_progress.start()
    }

    override fun processLogic() {
        super.processLogic()
        XiaomiUpdateAgent.update(this)
        loadImage(Hawk.get(USER_NAME, ""))
        Handler().postDelayed({
            login_progress.start()
        }, 400)
    }

    private fun doLogin() {
        val mPerson = Person()
        mPerson.name = login_name.text.toString()
        mPerson.pwd = login_pwd.text.toString()
        httpFindUser(mPerson)
    }

    private fun httpFindUser(mPerson: Person) {
        val query = BmobQuery<Person>("Person")
        query.addWhereEqualTo("name", mPerson.name)
        query.addWhereEqualTo("pwd", mPerson.pwd)
        addSubscription(query.findObjects(object : FindListenerSub<Person>(this) {
            override fun onSuccess(result: MutableList<Person>) {
                if (result.isEmptyList()) {
                    addUser(mPerson)
                    return
                }
                changeAct(result!![0])
            }

        }))
    }

    private fun changeAct(mPerson: Person) {
        CommonUtils.showSuccess(this, login_sub
                , object : ActionBack {
            override fun call() {
                Hawk.put(USER_NAME, mPerson.name)
                App.getInstance().savePerson(mPerson)
                startActivity(Intent(this@LoginAct,HomeAct::class.java))
                finish()
            }

        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ps = ParticleSystem(this, 200, R.drawable.star_pink, 800)
                    ps!!.setScaleRange(0.7f, 1.3f)
                    ps!!.setSpeedRange(0.05f, 0.1f)
                    ps!!.setRotationSpeedRange(90f, 180f)
                    ps!!.setFadeOut(200, AccelerateInterpolator())
                    ps!!.emit(event.x.toInt(), event.y.toInt(), 40)
                }
                MotionEvent.ACTION_MOVE -> ps!!.updateEmitPoint(event.x.toInt(), event.y.toInt())
                MotionEvent.ACTION_UP -> ps!!.stopEmitting()
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun addUser(mPerson: Person) {
        addSubscription(mPerson.save(object : SaveListenerSub(this) {
            override fun onSuccess() {
                changeAct(mPerson)
            }
        }))
    }


    fun loadImage(key: String) {
        CommonUtils.displayRoundImage(login_image, Hawk.get(key, ""))
    }

    override fun onDestroy() {
        mTencent?.logout(this)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}