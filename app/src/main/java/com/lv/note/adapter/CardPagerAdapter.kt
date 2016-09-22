package com.lv.note.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lv.note.R
import com.lv.note.entity.weather.Weather
import com.lv.note.util.CommonUtils
import kotlinx.android.synthetic.main.item_pager_view.view.*
import java.util.*

class CardPagerAdapter(private val mContext: Context, var mData: List<Weather>) : PagerAdapter() {
    private var isDay = false

    init {
        val cal = Calendar.getInstance()
        val time = cal.get(Calendar.HOUR_OF_DAY)
        isDay = (6 < time) && (time < 18)
    }


    override fun getCount(): Int {
        return mData.size
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pager_view,  container, false)
        val item = mData[position]
        convertView.vitem_temperature.text = "${item.lowtemp} ~ ${item.hightemp}"
        convertView.vitem_weather.text = item.type
        convertView.vitem_wind.text = "${item.fengli}  ${item.fengxiang}"
        convertView.vitem_date.text = item.date
        convertView.vitem_week.text = item.week
        convertView.vitem_image.setImageResource(CommonUtils.getWeatherTypeImageID(item.type, isDay))
        container.addView(convertView)
        return convertView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }
}
