package com.lv.note.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.dalong.francyconverflow.FancyCoverFlow
import com.dalong.francyconverflow.FancyCoverFlowAdapter
import com.lv.note.R
import com.lv.note.entity.weather.Weather
import com.lv.note.util.CommonUtils
import java.util.*


class LFancyCoverFlowAdapter(private val mContext: Context, var list: List<Weather>) : FancyCoverFlowAdapter() {

    override fun getCoverFlowItem(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pager_view, null)
            val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val width = wm.defaultDisplay.width
            convertView!!.layoutParams = FancyCoverFlow.LayoutParams(width / 2, FancyCoverFlow.LayoutParams.WRAP_CONTENT)
            holder = ViewHolder()
            holder.image = convertView.findViewById(R.id.vitem_image) as ImageView?;
            holder.temperature = convertView.findViewById(R.id.vitem_temperature) as TextView?;
            holder.weather = convertView.findViewById(R.id.vitem_weather) as TextView?;
            holder.wind = convertView.findViewById(R.id.vitem_wind) as TextView?;
            holder.date = convertView.findViewById(R.id.vitem_date) as TextView?;
            holder.week = convertView.findViewById(R.id.vitem_week) as TextView?;
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val item = getItem(position)
        holder.temperature!!.text = "${item.lowtemp} ~ ${item.hightemp}"
        holder.weather!!.text = item.type
        holder.wind!!.text = "${item.fengli}  ${item.fengxiang}"
        holder.date!!.text = item.date
        holder.week!!.text = item.week
        holder.image!!.setImageResource(CommonUtils.getWeatherTypeImageID(item.type,holder.isDay))
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


    internal class ViewHolder {
        var image: ImageView? = null
        var temperature: TextView? = null
        var weather: TextView? = null
        var wind: TextView? = null
        var date: TextView? = null
        var week: TextView? = null
        var isDay = true

        constructor() {
            val cal = Calendar.getInstance();
            val time = cal.get(Calendar.HOUR_OF_DAY)
            isDay = (6 < time) && (time < 18)
        }
    }
}
