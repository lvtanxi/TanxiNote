package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.view.View
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.util.CommonUtils
import com.lv.note.util.changeTopBgColor
import com.lv.note.util.openNewAct
import kotlinx.android.synthetic.main.act_clipimage.*
import rx.Observable
import rx.Subscriber
import java.io.File

/**
 * User: 吕勇
 * Date: 2016-03-31
 * Time: 15:52
 * Description:图片裁剪
 */
class ClipAct : BaseActivity() {
    private var path: String? = null
    override fun loadLayoutId(): Int {
        changeTopBgColor()
        return R.layout.act_clipimage
    }


    override fun bindListener() {
        id_action_clip.setOnClickListener {
            addSubscription(Observable.create(Observable.OnSubscribe<String> { subscriber ->
                val bitmap = id_clipImageLayout.clip()
                val path = Clip_CACHE + System.currentTimeMillis() + ".png"
                CommonUtils.savePhotoToSDCard(bitmap, path)
                subscriber.onNext(path)
                subscriber.onCompleted()
            }).doOnSubscribe { showLodingView() }
                    .subscribe(object : Subscriber<String>() {
                        override fun onError(e: Throwable?) {
                            toastError("裁剪图片失败")
                        }

                        override fun onNext(t: String?) {
                            val intent = Intent()
                            intent.putExtra(Clip_PATH, path)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }

                        override fun onCompleted() {
                            hideLodingView()
                        }

                    }))
        }
    }


    override fun initData() {
        path = intent.getStringExtra(Clip_PATH)
        if (TextUtils.isEmpty(path) || !File(path!!).exists()) {
            toastError("图片加载失败")
            finish()
            return
        }
        val bitmap = CommonUtils.convertToBitmap(path!!, 600, 600)
        if (bitmap == null) {
            toastError("图片加载失败")
            finish()
            return
        }
        id_clipImageLayout.setBitmap(bitmap!!)
    }

    companion object {
        val IMAGE_COMPLETE = 2 // 结果
        val Clip_PATH = "Clip_PATH"
        val Clip_CACHE = "${Environment.getExternalStorageDirectory()}/tanxi/cache/"

        fun startClipAct(activity: Activity, view: View, path: String) {
            activity.openNewAct(Intent(activity, ClipAct::class.java).putExtra(Clip_PATH, path), view, IMAGE_COMPLETE)
        }

        fun startClipAct(activity: Activity, path: String) {
            activity.startActivityForResult(Intent(activity, ClipAct::class.java).putExtra(Clip_PATH, path), IMAGE_COMPLETE)
        }
    }
}
