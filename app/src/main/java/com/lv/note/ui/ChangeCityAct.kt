package com.lv.note.ui

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lv.note.R
import com.lv.note.adapter.LBaseSearchAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.entity.City
import com.lv.note.helper.ActionBack
import com.lv.note.helper.CityComparator
import com.lv.note.util.CommonUtils
import com.lv.note.util.io_main
import com.lv.note.util.openNewAct
import com.lv.note.widget.FancyIndexer
import com.lv.note.widget.flowtag.TagAdapter
import com.lv.note.widget.flowtag.TagFlowLayout
import com.orhanobut.hawk.Hawk
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.act_change_city.*
import kotlinx.android.synthetic.main.item_city.view.*
import okhttp3.Call
import org.json.JSONObject
import rx.Observable
import rx.Subscriber
import java.io.InputStream
import java.util.*

class ChangeCityAct : BaseActivity() {

    private var mBaseAdapter: LBaseSearchAdapter<City>? = null
    private var mLetterIndexes: ArrayMap<String, Int>? = null
    private var mTagFlowLayout: TagFlowLayout<String>? = null
    private var mInflater: LayoutInflater? = null


    companion object {
        fun startChangeCityAct(actvity: Activity, tageView: View) {
            actvity.openNewAct(ChangeCityAct::class.java, tageView)
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_change_city
    }


    override fun initData() {
        mToolbar!!.title = "选择城市"
        mInflater = LayoutInflater.from(this)
        mTagFlowLayout = mInflater!!.inflate(R.layout.common_tagflow, null) as TagFlowLayout<String>?
        mTagFlowLayout!!.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, resources.displayMetrics).toInt(), 0, 0, 0)
        mTagFlowLayout!!.setCheckable(false)
        mBaseAdapter = object : LBaseSearchAdapter<City>(R.layout.item_city) {
            override fun compareVal(item: City, constraint: String): Boolean {
                return item.name.contains(constraint) || item.pinyin.contains(constraint)
            }

            override fun onBindItem(itemView: View, realPosition: Int, item: City) {
                itemView.cityitem_name.text =  item.name
                itemView.cityitem_letter.visibility = if(TextUtils.isEmpty(item.letter)) View.GONE else View.VISIBLE
                itemView.cityitem_name_icon.setTextAndColorSeed(item.name[0].toString(),item.name[0].toString())
                itemView.cityitem_letter.text =  item.letter
            }

            override fun onItemClick(view: View, item: City) {
                httpCityCode(item.name)
            }
        }
        val tagAdapter = object : TagAdapter<String>(arrayOf("成都", "重庆", "北京", "上海", "广州", "深圳", "武汉", "杭州", "天津")) {
            override fun getView(parent: ViewGroup, position: Int, t: String): View {
                val tv = mInflater!!.inflate(R.layout.item_movie_type, parent, false) as TextView
                tv.background = ContextCompat.getDrawable(tv.context, R.drawable.shape_white_border)
                tv.text = t
                return tv
            }

            override fun onSelect(parent: ViewGroup, view: View, position: Int) {
                httpCityCode(getItem(position))
            }

            override fun onUnSelect(parent: ViewGroup, view: View, position: Int) {
                httpCityCode(getItem(position))
            }
        }
        mTagFlowLayout?.setAdapter(tagAdapter)
        mBaseAdapter?.addHeaderView(mTagFlowLayout)
        city_recyclerview.layoutManager = LinearLayoutManager(city_recyclerview.context, LinearLayoutManager.VERTICAL, false)
        city_recyclerview.adapter = mBaseAdapter
    }

    private fun httpCityCode(cityname: String) {
        showLodingView()
        val url = "http://apis.baidu.com/apistore/weatherservice/cityinfo"
        OkHttpUtils
                .get()
                .url(url)
                .addParams("cityname", cityname)
                .addHeader("apikey", "8f6daecf84cbae393f48b080ae899728")
                .tag(this)
                .build()
                .execute(object : StringCallback() {
                    override fun onError(call: Call?, e: Exception?, id: Int) {
                        toastError("网络异常,请稍候再试!")
                    }

                    override fun onResponse(response: String?, id: Int) {
                        response?.let {
                            val jsonObj = JSONObject(response)
                            val code: Int = jsonObj.optInt("errNum")
                            if (code == 0) {
                                Hawk.put(WeatherFra.CITY_ID, jsonObj.optJSONObject("retData").optString("cityCode"))
                                Hawk.put(WeatherFra.CITY_NAME, jsonObj.optJSONObject("retData").optString("cityName"))
                                Hawk.put(WeatherFra.CITY_CHANGE, true)
                                CommonUtils.showSuccess(this@ChangeCityAct, city_recyclerview, object : ActionBack {
                                    override fun call() {
                                        finish()
                                    }
                                })
                            } else {
                                toastError(jsonObj.optString("errMsg"))
                            }
                        }
                    }

                    override fun onAfter(id: Int) {
                        super.onAfter(id)
                        hideLodingView()

                    }

                });
    }

    override fun processLogic() {
        showLodingView()
        Observable.create(Observable.OnSubscribe<List<City>> { subscriber ->
            subscriber.onNext(getCitys())
            subscriber.onCompleted()
        }).io_main<List<City>>()
                .subscribe(object : Subscriber<List<City>>() {
                    override fun onError(e: Throwable?) {

                    }

                    override fun onCompleted() {
                        hideLodingView()
                    }

                    override fun onNext(mCitys: List<City>?) {
                        mBaseAdapter?.addItems(mCitys,true)
                    }
                })
    }

    fun getCitys(): List<City>? {
        var input: InputStream? = null
        var citys: List<City>? = null
        try {
            input = assets.open("city.txt")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            val txt = String(buffer)
            input.close()

            citys =Gson().fromJson(txt, object : TypeToken<List<City>>() {}.type);
            citys?.let {
                Collections.sort<City>(citys, CityComparator())
                mLetterIndexes = ArrayMap(citys!!.size)
                for ((index, city) in citys!!.withIndex()) {
                    val currentLetter = CommonUtils.getFirstLetter(city.pinyin);
                    if (!mLetterIndexes!!.contains(currentLetter)) {
                        city.letter = currentLetter
                        mLetterIndexes!!.put(currentLetter, index + 1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            input?.let {
                input!!.close()
                input = null
            }
            return citys
        }
    }

    override fun bindListener() {
        city_fancyindexer.setOnTouchLetterChangedListener(object : FancyIndexer.OnTouchLetterChangedListener {
            override fun onTouchLetterChanged(str: String) {
                mLetterIndexes!![str]?.let {
                    city_recyclerview.layoutManager.scrollToPosition(mLetterIndexes!![str]!!)
                }
            }

        })
        city_search.addTextChangedListener(mBaseAdapter!!.filterTextWatcher)
    }

}
