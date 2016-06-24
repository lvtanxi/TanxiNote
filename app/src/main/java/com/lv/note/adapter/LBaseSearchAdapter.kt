package com.lv.note.adapter

import android.text.Editable
import android.text.TextWatcher
import android.widget.Filter
import android.widget.Filterable
import java.util.*

abstract class LBaseSearchAdapter<T>(layoutId: Int) : LBaseAdapter<T>(layoutId), Filterable {
    private var filter: ListFilter? = null

    abstract fun compareVal(item : T, constraint: String): Boolean

    override fun getFilter(): Filter {
        if (filter == null)
            filter = ListFilter(mDatas)
        return filter!!
    }

    private inner class ListFilter(private val original: List<T>) : Filter() {

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            if (constraint == null || constraint.length == 0) {
                results.values = original
                results.count = original.size
            } else {
                val mList = ArrayList<T>()
                for (p in original) {
                    val compareVal = compareVal(p, constraint.toString())
                    if (compareVal) {
                        mList.add(p)
                    }
                }
                results.values = mList
                results.count = mList.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence,
                                    results: Filter.FilterResults) {
            mDatas = results.values as MutableList<T>
            notifyDataSetChanged()
        }

    }

    //这里传入数据就可以了
    val filterTextWatcher: TextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            getFilter().filter(s)
        }
    }
}
