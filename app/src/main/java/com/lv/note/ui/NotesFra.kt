package com.lv.note.ui

import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.text.Html
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.view.ViewTreeObserver
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.base.BaseRecyclerFragment
import com.lv.note.entity.Note
import com.lv.note.helper.FindListenerSub
import com.lv.note.helper.UpdateListenerSub
import com.orhanobut.hawk.Hawk
import io.github.mthli.knife.KnifeText


/**
 * User: 吕勇
 * Date: 2016-08-01
 * Time: 15:47
 * Description:
 */
class NotesFra : BaseRecyclerFragment<Note>() {
    private var year = ""
    private var mSearchMessage = ""

    companion object{
        val CHANGE_NOTE="CHANGE_NOTE"
    }

    override fun loadLayoutId(): Int {
        column = 2
        return super.loadLayoutId()
    }

    override fun initData() {
        super.initData()
        year = DateFormat.format("yyyy年", System.currentTimeMillis()) as String
    }

    override val lBaseAdapter: LBaseAdapter<Note>
        get() = object : LBaseAdapter<Note>(R.layout.item_note) {
            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: Note) {
                val knife = baseHolder.getView<KnifeText>(R.id.item_knife)
                baseHolder.setKnifeTextHtml(R.id.item_knife, item.note)
                        .setText(R.id.item_date, item.year.replace(year, ""))
                        .setText(R.id.item_time, item.time)
                        .setOnItemChildClickListener(OnItemChildClickListener(), R.id.item_more, R.id.item_view)
                knife.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        knife.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        val itemView = baseHolder.getView<View>(R.id.item_view)
                        val itemViewL = itemView.layoutParams
                        itemViewL.height = knife.height
                        itemView.layoutParams = itemViewL
                    }
                })
            }

            override fun onItemClick(view: View, item: Note) {
                AddNoteAct.startAddNoteAct(activity, item, view)

            }
        }

    override fun onBGARefresh(): Boolean {
        val query = BmobQuery<Note>()
        query.addWhereEqualTo("userId", App.getInstance().getPerson()?.objectId)
        query.addWhereEqualTo("status", "1")
        if (!TextUtils.isEmpty(mSearchMessage))
            query.addWhereContains("note", mSearchMessage)
        query.order("createdAt")
        query.findObjects(activity, object : FindListenerSub<Note>(mBaseActivity!!, false) {
            override fun onSuccess(p0: MutableList<Note>) {
                addItems(p0)
            }

            override fun onFinish() {
                mSearchMessage=""
                stopRefreshing()
            }
        })
        return false
    }

    override fun bindListener() {
        super.bindListener()
        mBaseAdapter?.setOnRecyclerItemChildClickListener(object : LBaseAdapter
        .OnRecyclerItemChildClickListener {
            override fun onItemChildClick(view: View, position: Int) {
                if (view.id == R.id.item_view) {
                    AddNoteAct.startAddNoteAct(activity, mBaseAdapter!!.getItem(position), view)
                    return
                }
                val mPopupMenu = PopupMenu(activity, view)
                mPopupMenu.menu.add("分享")
                mPopupMenu.menu.add("删除")
                mPopupMenu.setOnMenuItemClickListener { item ->
                    val note = mBaseAdapter?.getItem(position)
                    note?.let {
                        when (item.title) {
                            "分享" ->
                                shareText(note.note)
                            "删除" ->
                                updateNote(note)

                        }
                    }
                    true
                }
                mPopupMenu.show()
            }
        })
    }

    private fun shareText(message: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message).toString())
        shareIntent.type = "text/plain"
        activity.startActivity(Intent.createChooser(shareIntent, "分享到"))
    }

    private fun updateNote(note: Note) {
        note.status = "0"
        note.update(activity, note.objectId, object : UpdateListenerSub(mBaseActivity!!) {
            override fun onSuccess() {
                processLogic()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (Hawk.get(CHANGE_NOTE, false)) {
            Hawk.remove(CHANGE_NOTE)
            commonRefresh?.setDelegate(mDelegate)
            processLogic()
        }
    }

    fun doSearch(message: String) {
        mSearchMessage = message
        commonRefresh?.beginRefreshing()
    }
}