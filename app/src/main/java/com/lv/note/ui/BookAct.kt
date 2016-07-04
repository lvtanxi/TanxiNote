package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.lv.note.R
import com.lv.note.entity.Book
import com.lv.test.BaseActivity
import com.orhanobut.hawk.Hawk
import java.io.File


/**
 * User: 吕勇
 * Date: 2016-07-04
 * Time: 12:57
 * Description:
 */
class BookAct : BaseActivity(), OnPageChangeListener {

    companion object {
        private val BOOK_PARAM = "book_param"
        fun startBookAct(actvity: Activity, book: Book) {
            actvity.startActivity(Intent(actvity, BookAct::class.java).putExtra(BOOK_PARAM, book))
        }
    }

    private var mPDFView: PDFView ? = null

    override fun loadLayoutId(): Int {
        return R.layout.act_book
    }

    override fun initViews() {
        mPDFView = fdb(R.id.book_pdf_view);
    }

    override fun initData() {
        val book: Book? = intent.getSerializableExtra(BOOK_PARAM) as Book?;
        book?.let {
            mToolbar?.title = book.fileName
            val file = File(book.filePath)
            if (!file.exists())
                return
            mPDFView!!.fromFile(file)
                    .swipeVertical(true)
                    .enableSwipe(true)
                    .defaultPage(Hawk.get(book.fileName,1))
                    .onPageChange(this).load()
        }
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        Hawk.put(mToolbar!!.title.toString(),page)
        Hawk.put("${mToolbar!!.title}_progress","$page/$pageCount")
    }

}