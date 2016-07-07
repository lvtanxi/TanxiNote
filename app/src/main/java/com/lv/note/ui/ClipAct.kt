package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.util.CommonUtils
import com.lv.note.widget.clip.ClipImageLayout
import java.io.File

/**
 * User: 吕勇
 * Date: 2016-03-31
 * Time: 15:52
 * Description:图片裁剪
 */
class ClipAct : BaseActivity() {
    private var mClipImageLayout: ClipImageLayout? = null
    private var path: String? = null
    private var btn: View? = null
    override fun loadLayoutId(): Int {
        return R.layout.act_clipimage
    }

    override fun initViews() {
        mClipImageLayout = fdb(R.id.id_clipImageLayout)
        btn = fdb(R.id.id_action_clip)
    }

    override fun bindListener() {
        btn!!.setOnClickListener{
            showLodingView()
            Thread(Runnable {
                val bitmap = mClipImageLayout!!.clip()
                val path = Clip_CACHE + System.currentTimeMillis() + ".png"
                CommonUtils.savePhotoToSDCard(bitmap, path)
                val intent = Intent()
                intent.putExtra(Clip_PATH, path)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }).start()
        }
    }

    override fun onDestroy() {
        hideLodingView()
        super.onDestroy()
    }


    override fun initData() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
        mClipImageLayout!!.setBitmap(bitmap!!)
    }

    companion object {
        val IMAGE_COMPLETE = 2 // 结果
        val Clip_PATH = "Clip_PATH"
        val Clip_CACHE = "${Environment.getExternalStorageDirectory()}/tanxi/cache/"
        fun startClipAct(activity: Activity, path: String) {
            activity.startActivityForResult(Intent(activity, ClipAct::class.java).putExtra(Clip_PATH, path), IMAGE_COMPLETE)
        }
    }
}
