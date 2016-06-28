package com.lv.note

import android.app.Application
import cn.bmob.v3.Bmob
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.lv.note.entity.Person
import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.HawkBuilder
import com.orhanobut.logger.Logger
import com.zhy.http.okhttp.OkHttpUtils
import im.fir.sdk.FIR
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 11:38
 * Description:app
 */
class App : Application() {
    val USER_INFO ="USER_INFO"

    private var mPerson: Person? =null

    companion object {
        private var app: App? = null
        fun getInstance(): App {
            if (app == null)
                app = App()
            return app as App
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        if(!BuildConfig.DEBUG)
            FIR.init(this);
         else
            Logger.init("lvtanxi")
        initBmob()
        initDb()
        initHttp()
    }

    private fun initBmob() {
        Bmob.initialize(this, "a43cff586c57dc8bcebb977d34cb7685");
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5770df82")
    }

    private fun initHttp() {
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient)
    }

    private fun initDb() {
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSharedPrefStorage(this))
                .build()
    }
    fun savePerson(person: Person?){
        this.mPerson=person
        if(null !=person){
            Hawk.put(USER_INFO,person)
            Hawk.put(mPerson!!.name,mPerson!!.header)
        } else{
            Hawk.remove(USER_INFO)
        }
    }
    fun getPerson():Person?{
        if(mPerson==null)
           mPerson=Hawk.get(USER_INFO);
        return mPerson
    }
}