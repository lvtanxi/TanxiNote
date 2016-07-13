package com.lv.note.ui

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lv.note.R
import com.lv.note.adapter.BaseHolder
import com.lv.note.adapter.LBaseSearchAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.entity.City
import com.lv.note.helper.CityComparator
import com.lv.note.util.CommonUtils
import com.lv.note.util.CountDown
import com.lv.note.util.openNewAct
import com.lv.note.widget.FancyIndexer
import com.lv.note.widget.SearchEditText
import com.lv.note.widget.flowtag.TagAdapter
import com.lv.note.widget.flowtag.TagFlowLayout
import com.orhanobut.hawk.Hawk
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.json.JSONObject
import java.io.InputStream
import java.util.*

class ChangeCityAct : BaseActivity() {

    private var citys: List<City>? = null
    private var mRecyclerView: RecyclerView? = null
    private var mBaseAdapter: LBaseSearchAdapter<City>? = null
    private var mLetterIndexes: ArrayMap<String, Int>? = null
    private var mFancyIndexer: FancyIndexer? = null
    private var mSearchEditText: SearchEditText? = null
    private var mTagFlowLayout: TagFlowLayout<String>? = null
    private var mInflater: LayoutInflater? = null

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            mBaseAdapter!!.addItems(citys, true)
            hideLodingView()
        }
    }


    companion object {
        fun startChangeCityAct(actvity: Activity) {
            actvity.openNewAct(ChangeCityAct::class.java)
        }
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_change_city
    }

    override fun initViews() {
        mRecyclerView = fdb(R.id.city_recyclerview);
        mFancyIndexer = fdb(R.id.city_fancyindexer);
        mSearchEditText=fdb(R.id.city_search);
    }

    override fun initData() {
        mToolbar!!.title="选择城市"
        mInflater = LayoutInflater.from(this)
        mTagFlowLayout= mInflater!!.inflate(R.layout.common_tagflow,null) as TagFlowLayout<String>?
        mTagFlowLayout!!.setPadding( TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10.0f,resources.displayMetrics).toInt(),0,0,0)
        mTagFlowLayout!!.setCheckable(false)
        mBaseAdapter = object : LBaseSearchAdapter<City>(R.layout.item_city) {
            override fun compareVal(item: City, constraint: String): Boolean {
                return item.name.contains(constraint)||item.pinyin.contains(constraint)
            }

            override fun onBindItem(baseHolder: BaseHolder, realPosition: Int, item: City) {
                baseHolder.setText(R.id.cityitem_name, item.name)
                        .setVisible(R.id.cityitem_letter, !TextUtils.isEmpty(item.letter))
                        .setAvatarImageText(R.id.cityitem_name_icon,item.name[0])
                        .setText(R.id.cityitem_letter, item.letter)
            }

            override fun onItemClick(view: View,item: City) {
                httpCityCode(item.name)
            }
        }
        val tagAdapter=object :TagAdapter<String>(arrayOf("成都","重庆","北京","上海","广州","深圳","武汉","杭州","天津")){
            override fun getView(parent: ViewGroup, position: Int, t: String): View {
                val tv = mInflater!!.inflate(R.layout.item_movie_type, parent, false) as TextView
                tv.background= ContextCompat.getDrawable(tv.context, R.drawable.shape_white_border)
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
        mTagFlowLayout!!.setAdapter(tagAdapter)
        mBaseAdapter!!.addHeaderView(mTagFlowLayout)
        mRecyclerView!!.layoutManager = LinearLayoutManager(mRecyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.adapter = mBaseAdapter
    }
    private fun httpCityCode(cityname:String) {
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
                                Hawk.put(WeatherAct.CITY_ID, jsonObj.optJSONObject("retData").optString("cityCode"))
                                Hawk.put(WeatherAct.CITY_NAME,jsonObj.optJSONObject("retData").optString("cityName"))
                                Hawk.put(WeatherAct.CITY_CHANGE, true)
                                CommonUtils.showSuccess(this@ChangeCityAct, mRecyclerView!!, object : CountDown.CountDownBack{
                                    override fun countDownFinish() {
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
        Thread(Runnable {
            var input: InputStream? = null
            try {
                input = assets.open("city.txt")
                val size = input.available()
                val buffer = ByteArray(size)
                input.read(buffer)
                val txt = String(buffer)
                input.close()

                citys = Gson().fromJson(txt, object : TypeToken<List<City>>() {}.type);
                citys?.let {
                    Collections.sort<City>(citys, CityComparator())
                    mLetterIndexes = ArrayMap(citys!!.size)
                    for ((index, city) in citys!!.withIndex()) {
                        val currentLetter = CommonUtils.getFirstLetter(city.pinyin);
                        if (!mLetterIndexes!!.contains(currentLetter)) {
                            city.letter = currentLetter
                            mLetterIndexes!!.put(currentLetter, index+1)
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
                mHandler.sendEmptyMessage(0)
            }

        }).start()
    }

    override fun bindListener() {
        mFancyIndexer!!.setOnTouchLetterChangedListener(object : FancyIndexer.OnTouchLetterChangedListener {
            override fun onTouchLetterChanged(str: String) {
                mLetterIndexes!![str]?.let {
                    mRecyclerView!!.layoutManager.scrollToPosition(mLetterIndexes!![str]!!)
                }
            }

        })
        mSearchEditText!!.addTextChangedListener(mBaseAdapter!!.filterTextWatcher)
    }

}
