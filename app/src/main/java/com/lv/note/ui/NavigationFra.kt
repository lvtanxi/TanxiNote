package com.lv.note.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.Button
import com.cocosw.bottomsheet.BottomSheet
import com.lv.note.App
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.base.BaseFragment
import com.lv.note.entity.NavigationItem
import com.lv.note.helper.UpdateListenerSub
import com.lv.note.util.CommonUtils
import com.lv.note.util.Permission.PermissionListener
import com.lv.note.util.Permission.PermissionManager
import com.lv.note.util.ThemeUtils
import com.lv.note.util.notEmptyStr
import com.lv.note.widget.CircleImageView
import com.lv.note.widget.selectpop.DefExtendItem
import com.lv.note.widget.selectpop.SelectPopupWindow
import com.upyun.library.common.Params
import com.upyun.library.common.UploadManager
import com.upyun.library.listener.UpCompleteListener
import java.io.File
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-06-14
 * Time: 15:57
 * Description:
 */
class NavigationFra : BaseFragment() {

    companion object {
        val PHOTOZOOM = 0 // 相册/拍照
        val PHOTOTAKE = 1 // 相册/拍照
        val REQUEST_CODE_CAMERA = 101
        val UPYUN_KEY = "P8Qk+8xuRQ8hV0mN5LAV5N/kznE="
        val UPYUN_SPACE = "erp-img-upload"
        val UPYUN_BASE = "http://erp-img-upload.b0.upaiyun.com/"
    }

    private var mRecyclerView: RecyclerView? = null
    private var mBaseAdapter: LBaseAdapter<NavigationItem>? = null
    private var lastIndex = 0
    private var mLoginOut: Button? = null
    private var mImageView: CircleImageView? = null
    private var urls = arrayOf("http://user.qzone.qq.com/992507862/2", "http://my.oschina.net/u/1269023")
    private var photoSaveName: String? = null//图pian名
    private var path: String? = null//图片全路径
    private var mSelectPopupWindow: SelectPopupWindow<DefExtendItem>? = null

    override fun loadLayoutId(): Int {
        return R.layout.fra_navigation_drawer
    }

    override fun initViews() {
        mRecyclerView = fdb(R.id.nva_recycler_view);
        mLoginOut = fdb(R.id.nva_login_out);
        mImageView = fdb(R.id.nva_header);
    }

    override fun initData() {
        (activity as MainAct).mFrag = this@NavigationFra
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBaseAdapter = object : LBaseAdapter<NavigationItem>(R.layout.item_nav) {
            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: NavigationItem) {
                baseHolder.setText(R.id.navitem_txt, item.txt)
                        .setImageResource(R.id.navitem_selected, if(item.selected==1)R.drawable.selected else R.drawable.nav_item)
            }

            override fun onItemClick(item: NavigationItem) {
                mBaseAdapter!!.getItem(lastIndex).selected = 0
                item.selected = 1
                notifyDataSetChanged()
                when (item.icon) {
                    2 ->
                        WeatherAct.startWeatherAct(activity)
                    3 ->
                        BookListAct.startBookListAct(activity)
                    4 ->
                        changeTheme()
                    else ->
                        WebViewAct.startWebViewAct(activity, urls[item.icon], item.txt)
                }
                lastIndex = item.icon
            }

            private fun changeTheme() {
                BottomSheet
                        .Builder(activity)
                        .title("请选择主题:")
                        .sheet(R.menu.menu_theme)
                        .listener { dialogInterface, index ->
                                ThemeUtils.saveTheme(activity,index)
                            }
                        .grid()
                        .build()
                        .show()
            }
        }
        mRecyclerView!!.adapter = mBaseAdapter
        val names = arrayOf("檀溪动态", "檀溪博客", "檀溪天气", "檀溪阅读", "檀溪主题")
        val items = ArrayList<NavigationItem>();
        for ((index, name) in names.withIndex()) {
            when (index) {
                0 ->
                    items.add(NavigationItem(index, name, 1))
                else ->
                    items.add(NavigationItem(index, name, 0))
            }
        }
        mBaseAdapter!!.addItems(items, true)

    }

    override fun processLogic() {
        val file = File("${Environment.getExternalStorageDirectory()}/tanxi/cache/")
        if (!file.exists())
            file.mkdirs()
        photoSaveName = System.currentTimeMillis().toString() + ".png"
        CommonUtils.displayRoundImage(mImageView!!.circleImage, App.getInstance().getPerson()!!.header)
    }

    override fun bindListener() {
        mLoginOut!!.setOnClickListener {
            App.getInstance().savePerson(null)
            LoginAct.startLoginAct(activity)
            activity.finish()
        }
        mImageView!!.setOnClickListener {
            if (mSelectPopupWindow == null) {

                mSelectPopupWindow = object : SelectPopupWindow<DefExtendItem>(activity, "请选择", SelectPopupWindow.getDefExtendItems("拍照", "相册")) {
                    override fun selectPopupBack(item: DefExtendItem) {
                        if (TextUtils.equals("拍照", item.value)) {
                            PermissionManager.with(this@NavigationFra)
                                    //添加权限请求码
                                    .addRequestCode(REQUEST_CODE_CAMERA)
                                    //设置权限，可以添加多个权限
                                    .permissions(Manifest.permission.CAMERA)
                                    .setPermissionsListener(object : PermissionListener {
                                        //当权限被授予时调用
                                        override fun onGranted() {
                                            photoSaveName = System.currentTimeMillis().toString() + ".png"
                                            val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            val imageUri = Uri.fromFile(File(ClipAct.Clip_CACHE, photoSaveName))
                                            openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
                                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                                            startActivityForResult(openCameraIntent, PHOTOTAKE)
                                        }


                                        override fun onShowRationale(permissionManager: PermissionManager, permissions: Array<String>) {
                                            //当用户拒绝某权限时并点击`不再提醒`的按钮时，下次应用再请求该权限时，需要给出合适的响应（比如,给个展示对话框来解释应用为什么需要该权限）
                                            Snackbar.make(mImageView!!, "需要相机权限去拍照", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("确定") {
                                                        permissionManager.setIsPositive(true)
                                                        permissionManager.request()
                                                    }.show()
                                        }
                                    }).request()
                        } else {
                            val openAlbumIntent = Intent(Intent.ACTION_GET_CONTENT)
                            openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                            startActivityForResult(openAlbumIntent, PHOTOZOOM)
                        }
                    }
                }
            }
            mSelectPopupWindow!!.show(mImageView!!)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        when (requestCode) {
            PHOTOZOOM -> {//相册
                if (data == null)
                    return
                val uri = data.data
                path = uri.path
                if (path.notEmptyStr() && !File(path).exists())
                    path = getRealPathFromURI(uri)
                ClipAct.startClipAct(activity, path!!)
            }
            PHOTOTAKE -> {//拍照
                path = ClipAct.Clip_CACHE + photoSaveName
                ClipAct.startClipAct(activity, path!!)
            }

            ClipAct.IMAGE_COMPLETE -> {
                path = data!!.getStringExtra(ClipAct.Clip_PATH)
                upLoadFile()
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upLoadFile() {
        mBaseActivity!!.showLodingView()
        val paramsMap = HashMap<String, Any>()
        //上传空间
        val tempFile = File(path)
        if (!tempFile.exists())
            return
        val savePath = "app" + File.separator + photoSaveName
        paramsMap.put(Params.BUCKET, UPYUN_SPACE)
        paramsMap.put(Params.PATH, savePath)
        UploadManager.getInstance().formUpload(tempFile, paramsMap, UPYUN_KEY, UpCompleteListener { isSuccess, result ->
            tempFile.delete()
            mBaseActivity!!.hideLodingView()
            if (isSuccess){
                App.getInstance().getPerson()!!.header ="$UPYUN_BASE$savePath"
                updateUser()
            } else{
                toastError("上传图片失败,请稍候再试！")
            }
        }, null)
    }

    private fun updateUser() {
        val mPerson = App.getInstance().getPerson()!!
        mPerson.update(activity,mPerson.objectId, object : UpdateListenerSub(mBaseActivity!!) {
            override fun onSuccess() {
                App.getInstance().savePerson(mPerson)
                CommonUtils.displayRoundImage(mImageView!!.circleImage, mPerson.header)
                CommonUtils.showSuccess(activity, mRecyclerView!!, null)
            }
        })
    }

    fun getRealPathFromURI(contentUri: Uri): String {
        var res: String = ""
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = cursor.getString(column_index)
            }
            cursor.close()
        }
        return res
    }

}