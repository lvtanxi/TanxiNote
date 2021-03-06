package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.text.Html
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.widget.PopupMenu
import cn.bmob.v3.BmobQuery
import com.lv.note.App
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.base.BaseRecyclerActivity
import com.lv.note.entity.Note
import com.lv.note.helper.FindListenerSub
import com.lv.note.helper.UpdateListenerSub
import com.orhanobut.hawk.Hawk
import com.xiaomi.market.sdk.XiaomiUpdateAgent


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 13:31
 * Description:主界面
 */
class MainAct : BaseRecyclerActivity<Note>(), AppBarLayout.OnOffsetChangedListener {

    private var mDrawerLayout: DrawerLayout? = null
    private var mActionBarDrawerToggle: ActionBarDrawerToggle? = null;
    private var mAppBar: AppBarLayout? = null
    private var mAddBtn: FloatingActionButton? = null
    var mFrag: NavigationFra? = null
    private var year = ""


    companion object {
        val CHANGE_NOTE = "CHANGE_NOTE"
        fun startMainAct(actvity: Activity) {
            actvity.startActivity(Intent(actvity, MainAct::class.java))
        }
    }

    override fun loadLayoutId(): Int {
        column = 2
        return R.layout.act_main
    }

    override fun processLogic() {
        super.processLogic()
        XiaomiUpdateAgent.update(this)
    }

    override fun initViews() {
        super.initViews()
        mDrawerLayout = fdb(R.id.mian_drawer_layout);
        mAppBar = fdb(R.id.main_appbar);
        mAddBtn = fdb(R.id.main_float_button);
    }

    override fun initData() {
        mActionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle?.syncState()
        super.initData()
        year = DateFormat.format("yyyy年", System.currentTimeMillis()) as String
    }

    override fun bindListener() {
        super.bindListener()
        mDrawerLayout?.addDrawerListener(mActionBarDrawerToggle!!)
        mAppBar?.addOnOffsetChangedListener(this)
        mAddBtn?.setOnClickListener { AddNoteAct.startAddNoteAct(this, null) }
        mBaseAdapter?.setOnRecyclerItemChildClickListener(object : LBaseAdapter
        .OnRecyclerItemChildClickListener {
            override fun onItemChildClick(view: View, position: Int) {
                if (view.id == R.id.item_knife) {
                    AddNoteAct.startAddNoteAct(this@MainAct, mBaseAdapter!!.getItem(position))
                    return
                }
                val mPopupMenu = PopupMenu(this@MainAct, view)
                mPopupMenu.menu.add("分享")
                mPopupMenu.menu.add("删除")
                mPopupMenu.setOnMenuItemClickListener { item ->
                    val note = mBaseAdapter?.getItem(position)
                    note?.let {
                        if (TextUtils.equals("分享", item.title))
                            shareText(note.note)
                        else
                            updateNote(note)
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
        startActivity(Intent.createChooser(shareIntent, "分享到"))
    }

    private fun updateNote(note: Note) {
        note.status = "0"
        note.update(this, note.objectId, object : UpdateListenerSub(this) {
            override fun onSuccess() {
                processLogic()
            }
        })
    }

    override val lBaseAdapter: LBaseAdapter<Note>
        get() = object : LBaseAdapter<Note>(R.layout.item_note) {
            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: Note) {
                baseHolder.setKnifeTextHtml(R.id.item_knife, item.note)
                        .setText(R.id.item_date, item.year.replace(year, ""))
                        .setText(R.id.item_time, item.time)
                        .setOnItemChildClickListener(OnItemChildClickListener(), R.id.item_more, R.id.item_knife)
            }

            override fun onItemClick(item: Note) {
                AddNoteAct.startAddNoteAct(this@MainAct, item)
            }
        }

    override fun onBGARefresh(): Boolean {
        val query = BmobQuery<Note>()
        query.addWhereEqualTo("userId", App.getInstance().getPerson()?.objectId)
        query.addWhereEqualTo("status", "1")
        query.order("createdAt")
        query.findObjects(this, object : FindListenerSub<Note>(this, false) {
            override fun onSuccess(p0: MutableList<Note>?) {
                addItems(p0!!)
            }

            override fun onFinish() {
                stopRefreshing()
            }
        })
        return false
    }


    override fun onResume() {
        super.onResume()
        mAppBar?.addOnOffsetChangedListener(this)
        if (Hawk.get(CHANGE_NOTE, false)) {
            Hawk.remove(CHANGE_NOTE)
            commonRefresh?.setDelegate(mDelegate)
            processLogic()
        }
    }


    override fun onPause() {
        super.onPause()
        mAppBar?.removeOnOffsetChangedListener(this)
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        commonRefresh?.setDelegate(if (verticalOffset == 0) mDelegate else null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFrag?.let {
            mFrag!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}