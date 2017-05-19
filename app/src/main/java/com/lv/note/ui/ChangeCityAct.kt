package com.lv.note.ui

import android.app.Activity
import android.support.v4.util.ArrayMap
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lv.note.R
import com.lv.note.adapter.LBaseMultiItemAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.entity.City
import com.lv.note.helper.CityComparator
import com.lv.note.helper.PinnedHeaderDecoration
import com.lv.note.util.CommonUtils
import com.lv.note.util.io_main
import com.lv.note.util.openNewAct
import com.lv.note.widget.WaveSideBar
import com.orhanobut.hawk.Hawk
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.act_change_city.*
import kotlinx.android.synthetic.main.item_city.view.*
import kotlinx.android.synthetic.main.item_city_title.view.*
import okhttp3.Call
import org.json.JSONObject
import rx.Observable
import rx.Subscriber
import java.io.InputStream
import java.util.*

class ChangeCityAct : BaseActivity() {

    private var mBaseAdapter: LBaseMultiItemAdapter<City>? = null
    private var mLetterIndexes: ArrayMap<String, Int>? = null


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
        mBaseAdapter = object : LBaseMultiItemAdapter<City>() {
            override fun addMultiItem() {
                addItmeType(0, R.layout.item_city)
                addItmeType(SECTION_HEADER_VIEW, R.layout.item_city_title)
            }

            override fun onBindHeaderItem(itemView: View, realPosition: Int, item: City) {
                itemView.cityitem_letter.text = item.letter
            }

            override fun onBindNormalItem(itemView: View, realPosition: Int, item: City) {
                itemView.cityitem_name.text = item.name
                itemView.cityitem_name_icon.setTextAndColorSeed(item.name[0].toString(), item.name[0].toString())
            }

            override fun onItemClick(view: View, item: City) {
                httpCityCode(item.name)
            }
        }
        city_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        city_recyclerview.addItemDecoration(PinnedHeaderDecoration())
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
                                finish()
                            } else {
                                toastError(jsonObj.optString("errMsg"))
                            }
                        }
                    }

                    override fun onAfter(id: Int) {
                        super.onAfter(id)
                        hideLodingView()

                    }

                })
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
                        mBaseAdapter?.addItems(mCitys, true)
                    }
                })
    }

    fun getCitys(): List<City>? {
        var input: InputStream? = null
        var citys: ArrayList<City>? = null
        try {
            input = assets.open("city.txt")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            val txt = String(buffer)
            input.close()

            citys = Gson().fromJson(txt, object : TypeToken<ArrayList<City>>() {}.type)
            citys?.let {
                Collections.sort<City>(citys, CityComparator())
                mLetterIndexes = ArrayMap(citys!!.size)
                for ((index, city) in citys!!.withIndex()) {
                    val currentLetter = CommonUtils.getFirstLetter(city.pinyin)
                    if (!mLetterIndexes!!.contains(currentLetter)) {
                        city.letter = currentLetter
                        city.type = LBaseMultiItemAdapter.SECTION_HEADER_VIEW
                        mLetterIndexes!!.put(currentLetter, index + 1)
                        continue
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

        side_bar.setOnSelectIndexItemListener(object : WaveSideBar.OnSelectIndexItemListener {
            override fun onSelectIndexItem(str: String) {
                mLetterIndexes!![str]?.let {
                    city_recyclerview.layoutManager.scrollToPosition(mLetterIndexes!![str]!!)
                }
            }

        })
    }

}
