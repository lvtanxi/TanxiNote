package com.lv.note.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseAdapter
import com.lv.note.base.BaseRecyclerActivity
import com.lv.note.entity.move.Directors
import com.lv.note.entity.move.Movie
import com.lv.note.widget.flowtag.TagAdapter
import com.lv.note.widget.flowtag.TagFlowLayout
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.json.JSONObject


/**
 * User: 吕勇
 * Date: 2016-06-22
 * Time: 09:00
 * Description:
 */
class MovieAct : BaseRecyclerActivity<Movie>() {

    private var mInflater: LayoutInflater? = null

    companion object {
        fun startMovieAct(actvity: Activity) {
            actvity.startActivity(Intent(actvity, MovieAct::class.java))
        }
    }

    override fun initData() {
        mToolbar!!.title = "檀溪电影"
        super.initData()
        mInflater = LayoutInflater.from(this)
    }

    override fun onBGARefresh(): Boolean {
        val url = "http://api.douban.com/v2/movie/in_theaters"
        OkHttpUtils
                .get()
                .url(url)
                .addParams("count", "20")
                .tag(this)
                .build()
                .execute(object : StringCallback() {
                    override fun onResponse(response: String?, id: Int) {
                        response?.let {
                            val jsonObj = JSONObject(response)
                            if (jsonObj.has("subjects")) {
                                val resultData: List<Movie> = Gson().fromJson(jsonObj.optString("subjects"), object : TypeToken<List<Movie>>() {}.type)
                                addItems(resultData)
                            }
                        }
                    }

                    override fun onError(call: Call?, e: Exception?, id: Int) {
                        toastError("网络异常,请稍候再试!")
                    }

                    override fun onAfter(id: Int) {
                        super.onAfter(id)
                        stopRefreshing()
                    }
                })
        return false
    }

    override val lBaseAdapter: LBaseAdapter<Movie>
        get() = object : LBaseAdapter<Movie>(R.layout.item_movie) {
            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: Movie) {
                baseHolder.setImageUrl(R.id.movieitem_image, item.images!!.large)
                        .setText(R.id.movieitem_name, item.title)
                        .setText(R.id.movieitem_date, "上映时间：${item.year}年")
                        .setText(R.id.movieitem_time, "电影片长：${item.collect_count / 1000}分钟")
                        .setRating(R.id.movieitem_ratingbar, item.rating!!.average / 2)

                baseHolder.getView<TagFlowLayout<String>>(R.id.common_tagflow).setAdapter(object : TagAdapter<String>(item.genres!!) {
                    override fun getView(parent: ViewGroup, position: Int, t: String): View {
                        val tv = mInflater!!.inflate(R.layout.item_movie_type, parent, false) as TextView
                        tv.text = t
                        return tv
                    }
                })
                baseHolder.getView<TagFlowLayout<Directors>>(R.id.movieitem_person).setAdapter(object : TagAdapter<Directors>(item.directors!!) {
                    override fun getView(parent: ViewGroup, position: Int, t: Directors): View {
                        val tv = mInflater!!.inflate(R.layout.item_movie_type, parent, false) as TextView
                        tv.text = t.name
                        return tv
                    }
                })
            }

            override fun onItemClick(item: Movie) {
                WebViewAct.startWebViewAct(this@MovieAct, item.alt, item.title)
            }
        }
}