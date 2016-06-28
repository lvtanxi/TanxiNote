package com.lv.note.util

import org.json.JSONObject
import org.json.JSONTokener

object JsonUtils {

    fun parseIatResult(json: String): String {
        val ret = StringBuffer()
        try {
            val tokener = JSONTokener(json)
            val joResult = JSONObject(tokener)

            val words = joResult.getJSONArray("ws")
            for (i in 0..words.length() - 1) {
                // 转写结果词，默认使用第一个结果
                val items = words.getJSONObject(i).getJSONArray("cw")
                val obj = items.getJSONObject(0)
                ret.append(obj.getString("w"))
                // 如果需要多候选结果，解析数组其他字段
                // for(int j = 0; j < items.length(); j++)
                // {
                // JSONObject obj = items.getJSONObject(j);
                // ret.append(obj.getString("w"));
                // }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ret.toString()
    }

}
