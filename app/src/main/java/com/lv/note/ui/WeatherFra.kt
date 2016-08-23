package com.lv.note.ui

import com.google.gson.Gson
import com.lv.note.R
import com.lv.note.adapter.LFancyCoverFlowAdapter
import com.lv.note.base.BaseFragment
import com.lv.note.entity.weather.ResultData
import com.lv.note.entity.weather.Weather
import com.lv.note.helper.BGARefreshDelegate
import com.lv.note.util.CommonUtils
import com.orhanobut.hawk.Hawk
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.fra_weather.*
import okhttp3.Call
import org.json.JSONObject
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-06-15
 * Time: 09:40
 * Description:
 */
class WeatherFra : BaseFragment(), BGARefreshDelegate.BGARefreshListener {


    private var mDelegate: BGARefreshDelegate? = null

    companion object {
        val CITY_ID="CITY_ID"
        val CITY_CHANGE="CITY_CHANGE"
        val CITY_NAME="CITY_NAME"
    }

    override fun loadLayoutId(): Int {
        return R.layout.fra_weather
    }

    override fun initData() {
        change_city_btn.text = "${Hawk.get(CITY_NAME,"成都")}天气"
        mDelegate = BGARefreshDelegate(w_recyclerview_refresh, this, false)
        w_fancyCoverFlow.unselectedScale = 0.3f//设置选中的规模
        w_fancyCoverFlow.scaleDownGravity = 0.5f
    }



    override fun processLogic() {
        w_recyclerview_refresh.beginRefreshing()
    }

    override fun bindListener() {
        w_recyclerview_refresh.setDelegate(mDelegate)
        change_city_btn.setOnClickListener{view->
            ChangeCityAct.startChangeCityAct(activity,view)
        }
    }


    override fun onBGARefresh(): Boolean {
        val url = "http://apis.baidu.com/apistore/weatherservice/recentweathers"
        OkHttpUtils
                .get()
                .url(url)
                .addParams("cityid", Hawk.get(CITY_ID,"101270101"))
                .addHeader("apikey", "8f6daecf84cbae393f48b080ae899728")
                .tag(this)
                .build()
                .execute(object : StringCallback() {
                    override fun onError(call: Call?, e: Exception?, id: Int) {
                        toastError("网络异常,请稍候再试!")
                    }

                    override fun onResponse(response: String?, id: Int) {
                        response?.let {
                            try {
                                val jsonObj = JSONObject(response)
                                val code: Int = jsonObj.optInt("errNum")
                                if (code == 0) {
                                    val resultData: ResultData = Gson().fromJson(jsonObj.optString("retData")!!, ResultData::class.java)
                                    var index = 0
                                    val datas = ArrayList<Weather>()

                                    resultData.history?.let {
                                        datas.addAll(resultData.history!!)
                                        index += resultData.history!!.size
                                    }

                                    resultData.today?.let {
                                        datas.add(resultData.today!!)
                                    }

                                    resultData.forecast?.let {
                                        datas.addAll(resultData.forecast!!)
                                    }


                                    w_fancyCoverFlow.adapter = LFancyCoverFlowAdapter(activity, datas)
                                    w_fancyCoverFlow.setSelection(index)
                                    val mWeatherChartData = datas.filterIndexed { i, weather -> (i > index - 3 && i < index + 4) }
                                    w_weather_chartview.setTuView(mWeatherChartData, "单位: 摄氏度")
                                } else {
                                    toastError(jsonObj.optString("errMsg"))
                                }
                            }catch (e:Exception){
                                toastError("获取天气失败")
                            }
                        }
                    }

                    override fun onAfter(id: Int) {
                        super.onAfter(id)
                        w_recyclerview_refresh.endRefreshing()
                        CommonUtils.showSuccess(activity, w_recyclerview_refresh, null)
                    }

                });
        return false
    }

    override fun onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this)
        super.onDestroy()
    }
    override fun onResume() {
        super.onResume()
        if(Hawk.get(CITY_CHANGE,false)){
            Hawk.remove(CITY_CHANGE)
            change_city_btn.text = "${Hawk.get(CITY_NAME,"成都")}天气"
            processLogic()
        }
    }

}