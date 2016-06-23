package com.lv.note.entity.weather

import android.text.TextUtils
import com.lv.note.widget.chart.WeatherChartItem


/**
 * User: 吕勇
 * Date: 2016-06-15
 * Time: 15:31
 * Description:
 */
class Weather : WeatherChartItem {

        override val x: String
                get() = if(TextUtils.isEmpty(curTemp)) week.replace("星期","周") else "今天"
        override val y: Float
                get() = if(TextUtils.isEmpty(curTemp)) (hightemp.replace("℃","").toFloat()+lowtemp.replace("℃","").toFloat())/2 else curTemp.replace("℃","").toFloat()

        var date: String = ""
        var week: String = ""
        var curTemp: String = ""
        var aqi: String = ""
        var fengxiang: String = ""
        var fengli: String = ""
        var hightemp: String = ""
        var lowtemp: String = ""
        var type: String = ""
        var index: List<Index>? = null

}