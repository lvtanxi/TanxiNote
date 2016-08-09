package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.lv.note.App
import com.lv.note.R
import com.lv.note.base.BaseActivity
import com.lv.note.entity.Note
import com.lv.note.helper.ActionBack
import com.lv.note.helper.SaveListenerSub
import com.lv.note.helper.UpdateListenerSub
import com.lv.note.util.CommonUtils
import com.lv.note.util.notEmptyStr
import com.lv.note.util.openNewAct
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
    private var knife: KnifeText? = null
    private var mAlertDialog: AlertDialog? = null




    companion object {
        val ADD_PARAM: String = "ADD_PARAM"
        fun startAddNoteAct(activity: Activity, note: Note?,view: View) {
            activity.openNewAct(Intent(activity, AddNoteAct::class.java)
                    .putExtra(ADD_PARAM, note),view)
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_add_note
    }

    override fun initViews() {
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
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_save) {
            if (knife?.toHtml().toString().notEmptyStr())
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
        addSubscription(note.save(object:SaveListenerSub(this){
            override fun onSuccess() {
                goBack()
            }
        }))
    }

    private fun updateNote(note: Note) {
        note.note = knife?.toHtml().toString()
        addSubscription(note.update(note.objectId,object :UpdateListenerSub(this){
            override fun onSuccess() {
                goBack()
            }
        }))
    }

    private fun goBack() {
        CommonUtils.showSuccess(this, knife!!, object : ActionBack {
            override fun call() {
                Hawk.put(NotesFra.CHANGE_NOTE, true)
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
                        if (linkStr.notEmptyStr())
                            knife?.link(linkStr, startIndex, endIndex)
                    }
                    .setNegativeButton("取消") { dialog, which -> }
                    .create()
        }
        mAlertDialog?.show()
    }
}