package com.lv.note.adapter

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dalong.francyconverflow.FancyCoverFlow
import com.dalong.francyconverflow.FancyCoverFlowAdapter
import com.lv.note.R
import com.lv.note.entity.weather.Weather
import com.lv.note.util.CommonUtils
import kotlinx.android.synthetic.main.item_pager_view.view.*
import java.util.*


class LFancyCoverFlowAdapter(private val mContext: Context, var list: List<Weather>) : FancyCoverFlowAdapter() {
    private var isDay =false
    init {
        val cal = Calendar.getInstance()
        val time = cal.get(Calendar.HOUR_OF_DAY)
        isDay = (6 < time) && (time < 18)
    }
    override fun getCoverFlowItem(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pager_view, null)
            val dm = DisplayMetrics()
            (mContext as Activity).windowManager.defaultDisplay.getMetrics(dm)
            val width = dm.widthPixels
            convertView!!.layoutParams = FancyCoverFlow.LayoutParams(width / 2, FancyCoverFlow.LayoutParams.WRAP_CONTENT)
        }
        val item = getItem(position)
        convertView.vitem_temperature.text = "${item.lowtemp} ~ ${item.hightemp}"
        convertView.vitem_weather.text = item.type
        convertView.vitem_wind.text = "${item.fengli}  ${item.fengxiang}"
        convertView.vitem_date.text = item.date
        convertView.vitem_week.text = item.week
        convertView.vitem_image.setImageResource(CommonUtils.getWeatherTypeImageID(item.type,isDay))
        return convertView
    }


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Weather {
        return list[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
}
