package com.lv.note.ui

import android.content.Intent
import android.os.Environment
import android.text.format.DateFormat
import android.view.View
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.base.BaseRecyclerActivity
import com.lv.note.entity.Book
import com.lv.note.util.io_main
import com.lv.note.util.openNewAct
import com.lv.test.DLog
import com.orhanobut.hawk.Hawk
import rx.Observable
import rx.Subscriber
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-07-04
 * Time: 10:31
 * Description:
 */
class BookListAct : BaseRecyclerActivity<Book>() {


    override fun initData() {
        mToolbar!!.title = "檀溪阅读(本地pdf)"
        super.initData()
    }


    override fun onBGARefresh(): Boolean {
        addSubscription(Observable.create(Observable.OnSubscribe<List<Book>> { subscriber ->
            val array = ArrayList<Book>()
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                val path = Environment.getExternalStorageDirectory()// 获得SD卡路径
                val files = path.listFiles()// 读取
                loadBooks(files, array)
            }
            subscriber.onNext(array)
            subscriber.onCompleted()
        }).io_main<List<Book>>()
                .subscribe(object : Subscriber<List<Book>>() {
                    override fun onError(e: Throwable?) {

                    }

                    override fun onCompleted() {
                        stopRefreshing()
                    }

                    override fun onNext(mBooks: List<Book>?) {
                        addItems(mBooks)
                    }
                }))
        return false
    }


    override val lBaseAdapter: LBaseAdapter<Book>
        get() = object : LBaseAdapter<Book>(R.layout.item_book) {
            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: Book) {
                baseHolder.setText(R.id.bookitem_name, item.fileName)
                        .setText(R.id.bookitem_size, item.fileSize)
                        .setText(R.id.bookitem_progress, Hawk.get("${item.fileName}_progress", ""))
            }

            override fun onItemClick(view: View, item: Book) {
                openNewAct(BookAct::class.java, view)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mBaseAdapter?.let {
            mBaseAdapter!!.notifyDataSetChanged()
        }
    }


    private fun loadBooks(files: Array<File>?, mBooks: ArrayList<Book>?) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (file in files) {
                if (file.isDirectory()) {
                    loadBooks(file.listFiles(), mBooks)
                } else {
                    val fileName = file.getName()
                    if (fileName.endsWith(".pdf")) {
                        val map = HashMap<String, String>()
                        map.put("name", fileName.substring(0, fileName.lastIndexOf(".")))
                        var fis: FileInputStream? = FileInputStream(file)
                        val count = fis!!.available()
                        fis!!.close()
                        fis = null
                        DLog.d(DateFormat.format("yyyy-MM-dd hh:mm", file.lastModified()));
                        mBooks?.add(Book(fileName.substring(0, fileName.lastIndexOf(".")), formentFileSize(count), file.path))
                    }
                }
            }
        }
    }

    fun formentFileSize(fileS: Int): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        if (fileS < 1024) {
            fileSizeString = df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            fileSizeString = df.format(fileS.toDouble() / 1024) + "K"
        } else if (fileS < 1073741824) {
            fileSizeString = df.format(fileS.toDouble() / 1048576) + "M"
        } else {
            fileSizeString = df.format(fileS.toDouble() / 1073741824) + "G"
        }
        return fileSizeString
    }
}