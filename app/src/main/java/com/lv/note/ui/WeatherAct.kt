package com.lv.note.ui

import android.view.Menu
import android.view.MenuItem
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import com.dalong.francyconverflow.FancyCoverFlow
import com.google.gson.Gson
import com.lv.note.R
import com.lv.note.adapter.LFancyCoverFlowAdapter
import com.lv.note.base.BaseActivity
import com.lv.note.entity.weather.ResultData
import com.lv.note.entity.weather.Weather
import com.lv.note.helper.BGARefreshDelegate
import com.lv.note.util.CommonUtils
import com.lv.note.util.openNewAct
import com.lv.note.widget.chart.WeatherChartItem
import com.lv.note.widget.chart.WeatherChartView
import com.orhanobut.hawk.Hawk
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import org.json.JSONObject
import java.util.*


/**
 * User: 吕勇
 * Date: 2016-06-15
 * Time: 09:40
 * Description:
 */
class WeatherAct : BaseActivity(), BGARefreshDelegate.BGARefreshListener {


    private var mRefreshLayout: BGARefreshLayout? = null
    private var mDelegate: BGARefreshDelegate? = null
    private var mfancyCoverFlow: FancyCoverFlow? = null
    private var mWeatherChartView: WeatherChartView<WeatherChartItem>? = null

    companion object {
        val CITY_ID="CITY_ID"
        val CITY_CHANGE="CITY_CHANGE"
        val CITY_NAME="CITY_NAME"
    }

    override fun loadLayoutId(): Int {
        return R.layout.act_weather
    }

    override fun initViews() {
        mRefreshLayout = fdb(R.id.w_recyclerview_refresh);
        mfancyCoverFlow = fdb(R.id.w_fancyCoverFlow);
        mWeatherChartView = fdb(R.id.w_weather_chartview);
    }

    override fun initData() {
        mToolbar!!.title = "${Hawk.get(CITY_NAME,"成都")}天气"
        mDelegate = BGARefreshDelegate(mRefreshLayout!!, this, false)
        mfancyCoverFlow!!.unselectedScale = 0.3f//设置选中的规模
        mfancyCoverFlow!!.scaleDownGravity = 0.5f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_change_city,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId==R.id.action_change){
            openNewAct(ChangeCityAct::class.java,mToolbar!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun processLogic() {
        mRefreshLayout!!.beginRefreshing()
    }

    override fun bindListener() {
        mRefreshLayout!!.setDelegate(mDelegate)
    }

    override fun onResume() {
        super.onResume()
        if(Hawk.get(CITY_CHANGE,false)){
            Hawk.remove(CITY_CHANGE)
            mToolbar!!.title = "${Hawk.get(CITY_NAME,"成都")}天气"
            processLogic()
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


                                mfancyCoverFlow!!.adapter = LFancyCoverFlowAdapter(this@WeatherAct, datas)
                                mfancyCoverFlow!!.setSelection(index)
                                val mWeatherChartData = datas.filterIndexed { i, weather -> (i > index - 3 && i < index + 4) }
                                mWeatherChartView!!.setTuView(mWeatherChartData, "单位: 摄氏度");
                            } else {
                                toastError(jsonObj.optString("errMsg"))
                            }
                        }
                    }

                    override fun onAfter(id: Int) {
                        super.onAfter(id)
                        mRefreshLayout!!.endRefreshing()
                        CommonUtils.showSuccess(this@WeatherAct, mRefreshLayout!!, null)
                    }

                });
        return false
    }

    override fun onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this)
        super.onDestroy()
    }

}