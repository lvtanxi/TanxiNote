package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.entity.Person
import com.lv.note.helper.CustTextWatcher
import com.lv.note.helper.FindListenerSub
import com.lv.note.helper.SaveListenerSub
import com.lv.note.util.CommonUtils
import com.lv.note.util.CountDown
import com.lv.note.widget.HeartProgressBar
import com.lv.test.ArrayUtils
import com.lv.test.BaseActivity
import com.orhanobut.hawk.Hawk
import com.plattysoft.leonids.ParticleSystem
import com.xiaomi.market.sdk.XiaomiUpdateAgent


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 10:02
 * Description:登录界面
 */
class LoginAct : BaseActivity() {


    private var name: EditText? = null
    private var pwd: EditText? = null
    private var sub: ImageButton? = null
    private var ps: ParticleSystem ? = null
    private var mP: HeartProgressBar? = null
    private var header: ImageView? = null
    private var what: ImageButton? = null

    companion object {
        val USER_NAME = "USER_PHONE"
        fun startLoginAct(actvity: Activity) {
            actvity.startActivity(Intent(actvity, LoginAct::class.java))
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_login
    }

    override fun initViews() {
        name = fdb(R.id.login_name);
        pwd = fdb(R.id.login_pwd);
        sub = fdb(R.id.login_sub);
        mP = fdb(R.id.login_progress);
        header = fdb(R.id.login_image);
        what = fdb(R.id.login_what);
    }

    override fun initData() {
        name!!.setText(Hawk.get(USER_NAME, ""))
        if(name!!.text.length !=0){
            pwd!!.isFocusable=true
            pwd!!.isFocusableInTouchMode=true
            pwd!!.requestFocus()
        }
    }

    override fun bindListener() {
        name!!.addTextChangedListener(object : CustTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                stopHeartProgressBar()
                if (name!!.text.length == 11)
                    loadImage(name!!.text.toString())
                else
                    header!!.setImageResource(R.drawable.header)
            }
        })
        pwd!!.addTextChangedListener(object : CustTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                stopHeartProgressBar()
            }
        })
        sub!!.setOnClickListener { findUser() }

        what!!.setOnClickListener {
            AlertDialog.Builder(this)
            .setTitle("檀溪提示")
            .setMessage("用户是根据后端判断是否存在，所以没用注册界面，请见谅")
            .setPositiveButton("确定"){dialog,index -> dialog.dismiss()}
            .create()
            .show()
        }
    }

    fun stopHeartProgressBar(){
        if((name!!.text.length == 11) && (pwd!!.text.length >= 6)){
            mP!!.dismiss()
            sub!!.visibility= View.VISIBLE
            return
        }
        sub!!.visibility= View.GONE
        mP!!.start()
    }

    override fun processLogic() {
        super.processLogic()
        XiaomiUpdateAgent.update(this)
        loadImage(Hawk.get(USER_NAME, ""))
        Handler().postDelayed({
            mP!!.start()
        }, 400)
    }

    private fun findUser() {
        val mPerson = Person()
        mPerson.name = name!!.text.toString()
        mPerson.pwd = pwd!!.text.toString()
        val query = BmobQuery<Person>("Person")
        query.addWhereEqualTo("name", mPerson.name)
        query.addWhereEqualTo("pwd", mPerson.pwd)
        query.findObjects(this, object : FindListenerSub<Person>(this) {
            override fun onSuccess(p0: MutableList<Person>?) {
                if (ArrayUtils.isEmpty(p0)) {
                    addUser(mPerson)
                    return
                }
                changeAct(p0!![0])
            }
        })
    }

    private fun changeAct(mPerson: Person) {
        CommonUtils.showSuccess(this, sub!!
                , object : CountDown.CountDownBack {
            override fun countDownFinish() {
                Hawk.put(USER_NAME, mPerson.name)
                App.getInstance().savePerson(mPerson)
                MainAct.startMainAct(this@LoginAct)
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
        mPerson.save(this, object : SaveListenerSub(this) {
            override fun onSuccess() {
                changeAct(mPerson)
            }
        })
    }


    fun loadImage(key: String) {
        CommonUtils.displayRoundImage(header!!, Hawk.get(key, ""))
    }


}