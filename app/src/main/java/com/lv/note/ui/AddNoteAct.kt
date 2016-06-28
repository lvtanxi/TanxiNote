package com.lv.note.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.lv.note.App
import com.lv.note.R
import com.lv.note.entity.Note
import com.lv.note.helper.SaveListenerSub
import com.lv.note.helper.UpdateListenerSub
import com.lv.note.util.CommonUtils
import com.lv.note.util.CountDown
import com.lv.note.util.JsonUtils
import com.lv.note.util.Permission.PermissionListener
import com.lv.note.util.Permission.PermissionManager
import com.lv.test.BaseActivity
import com.lv.test.StrUtils
import com.orhanobut.hawk.Hawk
import io.github.mthli.knife.KnifeText


/**
 * User: 吕勇
 * Date: 2016-06-14
 * Time: 09:25
 * Description:
 */
class AddNoteAct : BaseActivity() {

    private var bold: ImageButton? = null
    private var italic: ImageButton? = null
    private var underline: ImageButton? = null
    private var strikethrough: ImageButton? = null
    private var bullet: ImageButton? = null
    private var quote: ImageButton? = null
    private var link: ImageButton? = null
    private var clear: ImageButton? = null
    private var undo: ImageButton? = null
    private var redo: ImageButton? = null
    private var volume: ImageButton? = null
    private var knife: KnifeText? = null
    private var isShowDialog = true
    private var mAlertDialog: AlertDialog? = null
    // 语音听写对象
    private var mIat: SpeechRecognizer? = null;
    // 语音听写UI
    private var mIatDialog: RecognizerDialog? = null;
    // 引擎类型
    private val mEngineType = SpeechConstant.TYPE_CLOUD;
    private val resultText =StringBuffer()


    /**
     * 初始化监听器。
     */
    private val mInitListener = InitListener() {
        if (it != ErrorCode.SUCCESS)
            toastError("初始化失败")

    }


    companion object {
        val ADD_PARAM: String = "ADD_PARAM"
        fun startAddNoteAct(activity: Activity, note: Note?) {
            activity.startActivity(Intent(activity, AddNoteAct::class.java)
                    .putExtra(ADD_PARAM, note))
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_add_note
    }

    override fun initViews() {
        volume = fdb(R.id.volume);
        bold = fdb(R.id.bold);
        italic = fdb(R.id.italic);
        underline = fdb(R.id.underline);
        strikethrough = fdb(R.id.strikethrough);
        bullet = fdb(R.id.bullet);
        quote = fdb(R.id.quote);
        link = fdb(R.id.link);
        clear = fdb(R.id.clear);
        undo = fdb(R.id.undo);
        redo = fdb(R.id.redo);
        knife = fdb(R.id.knife);
    }

    override fun initData() {
        mToolbar?.title = "新建笔记"
        val note: Note? = intent.getSerializableExtra(ADD_PARAM) as Note?
        note?.let {
            knife!!.fromHtml(note.note)
            knife!!.setSelection(knife!!.editableText.length)
        }
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener)
        mIatDialog = RecognizerDialog(this, mInitListener)
        setParam()
    }

    private val mRecognizerDialogListener = object : RecognizerDialogListener {
        override fun onResult(results: RecognizerResult?, isLast: Boolean) {
            isShowDialog = isLast
            results?.let {
                val  text = JsonUtils.parseIatResult(results.resultString);
                resultText.append(text);
                if (isLast) {
                    // 最后的结果
                    knife!!.fromHtml(knife!!.toHtml()+resultText.toString().trim())
                    knife!!.setSelection(knife!!.editableText.length)
                }
            }
        }

        override fun onError(p0: SpeechError?) {
            isShowDialog = true
        }

    }


    override fun bindListener() {
        bold!!.setOnClickListener { knife!!.bold(!knife!!.contains(KnifeText.FORMAT_BOLD)) }
        italic!!.setOnClickListener { knife!!.italic(!knife!!.contains(KnifeText.FORMAT_ITALIC)) }
        underline!!.setOnClickListener { knife!!.underline(!knife!!.contains(KnifeText.FORMAT_UNDERLINED)) }
        strikethrough!!.setOnClickListener { knife!!.strikethrough(!knife!!.contains(KnifeText.FORMAT_STRIKETHROUGH)) }
        bullet!!.setOnClickListener { knife!!.bullet(!knife!!.contains(KnifeText.FORMAT_BULLET)) }
        quote!!.setOnClickListener { knife!!.quote(!knife!!.contains(KnifeText.FORMAT_QUOTE)) }
        link!!.setOnClickListener { showLinkDialog() }
        clear!!.setOnClickListener { knife!!.clearFormats() }
        undo!!.setOnClickListener { knife!!.undo() }
        redo!!.setOnClickListener { knife!!.redo() }
        volume!!.setOnClickListener {
            getPermission()
        }
    }

    private fun setParam() {
        // 清空参数
        mIat!!.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat!!.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat!!.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat!!.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat!!.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat!!.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat!!.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat!!.setParameter(SpeechConstant.ASR_PTT, "1");

    }

    fun getPermission(){
        PermissionManager.Companion.with(this@AddNoteAct)
                //添加权限请求码
                .addRequestCode(NavigationFra.REQUEST_CODE_CAMERA)
                //设置权限，可以添加多个权限
                .permissions(Manifest.permission.RECORD_AUDIO)
                .setPermissionsListener(object : PermissionListener {
                    //当权限被授予时调用
                    override fun onGranted() {
                        if (isShowDialog) {
                            // 显示听写对话框
                            resultText.setLength(0)
                            mIatDialog!!.setListener(mRecognizerDialogListener);
                            mIatDialog!!.show();
                            isShowDialog = false
                        }
                    }
                    override fun onShowRationale(permissionManager: PermissionManager, permissions: Array<String>) {
                        Snackbar.make(knife!!, "需要语音去进行语音输入", Snackbar.LENGTH_INDEFINITE)
                                .setAction("确定") {
                                    permissionManager.setIsPositive(true)
                                    permissionManager.request()
                                }.show()
                    }
                }).request()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_save) {
            if (StrUtils.notEmpty(knife?.toHtml().toString()))
                saveOrUpdateNote()
            else
                toastError("亲,还是需要输入内容的喔....")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveOrUpdateNote() {
        val note: Note? = intent.getSerializableExtra(ADD_PARAM) as Note?
        if (note == null)
            saveNote()
        else
            updateNote(note)
    }

    private fun saveNote() {
        val note = Note()
        note.userId = App.getInstance().getPerson()!!.objectId
        note.note = knife?.toHtml().toString()
        note.year = DateFormat.format("yyyy年MM月dd日", System.currentTimeMillis()) as String
        note.time = DateFormat.format("HH:mm", System.currentTimeMillis()) as String
        note.save(this, object : SaveListenerSub(this) {
            override fun onSuccess() {
                goBack()
            }
        })
    }

    private fun updateNote(note: Note) {
        note.note = knife?.toHtml().toString()
        note.update(this, note.objectId, object : UpdateListenerSub(this) {
            override fun onSuccess() {
                goBack()
            }
        })
    }

    private fun goBack() {
        CommonUtils.showSuccess(this, knife!!, object : CountDown.CountDownBack {
            override fun countDownFinish() {
                Hawk.put(MainAct.CHANGE_NOTE, true)
                finish()
            }
        })
    }

    private fun showLinkDialog() {
        val startIndex = knife!!.selectionStart
        val endIndex = knife!!.selectionEnd
        if (null == mAlertDialog) {
            val editText = EditText(this)
            mAlertDialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("请输入链接")
                    .setView(editText)
                    .setPositiveButton("确定") { dialog, which ->
                        val linkStr = editText.text.toString()
                        if (StrUtils.notEmpty(linkStr))
                            knife?.link(linkStr, startIndex, endIndex)
                    }
                    .setNegativeButton("取消") { dialog, which -> }
                    .create()
        }
        mAlertDialog?.show()
    }

    override fun onDestroy() {
        mIat?.cancel();
        mIat?.destroy();
        super.onDestroy()
    }

}