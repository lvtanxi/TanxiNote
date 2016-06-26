package com.lv.note.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lv.note.R
import com.plattysoft.leonids.ParticleSystem
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.regex.Pattern

/**
 * @author andy he
 * *
 * @ClassName: CommonUtils
 * *
 * @Description: 通用、不好归类的工具
 * *
 * @date 2016年1月15日 上午10:18:53
 */
object CommonUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                if (info.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取版本名称
     */
    fun versionName(context: Context): String {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取版本号
     */
    fun versionCode(context: Context): Int {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        return info.versionCode.toInt()// 版本号
    }


    /**
     * 显示键盘

     * @param context 内容上下文
     */
    fun showKeyBoard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * 隐藏键盘

     * @param view 控件
     */
    fun hiddenKeyBoard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    /**
     * 检查SDK是否存在

     * @return
     */
    fun checkSDCardAvailable(): Boolean {
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }

    /**
     * 获取屏幕
     * @param context
     * *
     * @return
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }

    fun getErrorMessage(code: Int): String {
        when (code) {
            9001 ->
                return "Application Id为空，请初始化"
            9002 ->
                return "解析返回数据出错"
            9003 ->
                return "上传文件出错"
            9004 ->
                return "文件上传失败"
            9005 ->
                return "批量操作只支持最多50条"
            9006 ->
                return "objectId为空"
            9007 ->
                return "文件大小超过10M"
            9008 ->
                return "上传文件不存在"
            9009 ->
                return "没有缓存数据"
            9010 ->
                return "网络超时"
            9011 ->
                return "BmobUser类不支持批量操作"
            9012 ->
                return "上下文为空"
            9013 ->
                return "BmobObject（数据表名称）格式不正确"
            9014 ->
                return "第三方账号授权失败"
            9015 ->
                return "未知错误"
            9016 ->
                return "无网络连接,请检查您的手机网络"
            9017 ->
                return "第三方登录失败"
            9018 ->
                return "参数不能为空"
            9019 ->
                return "格式不正确"
            else -> return ""
        }

    }

    fun convertToBitmap(path: String, w: Int, h: Int): Bitmap? {
        try {
            val opts = BitmapFactory.Options()
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            // 返回为空
            BitmapFactory.decodeFile(path, opts)
            val width = opts.outWidth
            val height = opts.outHeight
            var scaleWidth = 0.0f
            var scaleHeight = 0.0f
            if (width > w || height > h) {
                // 缩放
                scaleWidth = width.toFloat() / w
                scaleHeight = height.toFloat() / h
            }
            opts.inJustDecodeBounds = false
            val scale = Math.max(scaleWidth, scaleHeight)
            opts.inSampleSize = scale.toInt()
            val weak = WeakReference(BitmapFactory.decodeFile(path, opts))
            val bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get()!!.width, weak.get()!!.height, null, true)
            if (bMapRotate != null) {
                return bMapRotate
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    fun savePhotoToSDCard(photoBitmap: Bitmap?, path: String) {
        if (checkSDCardAvailable()) {
            val photoFile = File(path)
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(photoFile)
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush()
                    }
                }
            } catch (e: Exception) {
                photoFile.delete()
                e.printStackTrace()
            } finally {
                try {
                    fileOutputStream!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun showSuccess(activity: Activity, tageView: View, back: CountDown.CountDownBack?) {
        ParticleSystem(activity, 800, R.drawable.star_pink, 1000)
                .setSpeedRange(0.1f, 0.25f)
                .oneShot(tageView, 100)
        CountDown(1000)
                .setActivity(activity)
                .setDownBack(back)
                .start();
    }


    fun displayRoundImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.header)
                .error(R.drawable.header)
                .crossFade()
                .into(imageView)
    }

    fun getWeatherTypeImageID(type: String?, isDay: Boolean): Int {
        if (null == type)
            return R.drawable.ic_weather_no
        val weatherId: Int
        when (type) {
            "晴" -> if (isDay) {
                weatherId = R.drawable.ic_weather_sunny_day
            } else {
                weatherId = R.drawable.ic_weather_sunny_night
            }
            "多云" -> if (isDay) {
                weatherId = R.drawable.ic_weather_cloudy_day
            } else {
                weatherId = R.drawable.ic_weather_cloudy_night
            }
            "阴" -> weatherId = R.drawable.ic_weather_overcast
            "雷阵雨", "雷阵雨伴有冰雹" -> weatherId = R.drawable.ic_weather_thunder_shower
            "雨夹雪" -> weatherId = R.drawable.ic_weather_sleet
            "冻雨" -> weatherId = R.drawable.ic_weather_ice_rain
            "小雨", "小到中雨", "阵雨" -> weatherId = R.drawable.ic_weather_light_rain_or_shower
            "中雨", "中到大雨" -> weatherId = R.drawable.ic_weather_moderate_rain
            "大雨", "大到暴雨" -> weatherId = R.drawable.ic_weather_heavy_rain
            "暴雨", "大暴雨", "特大暴雨", "暴雨到大暴雨", "大暴雨到特大暴雨" -> weatherId = R.drawable.ic_weather_storm
            "阵雪", "小雪", "小到中雪" -> weatherId = R.drawable.ic_weather_light_snow
            "中雪", "中到大雪" -> weatherId = R.drawable.ic_weather_moderate_snow
            "大雪", "大到暴雪" -> weatherId = R.drawable.ic_weather_heavy_snow
            "暴雪" -> weatherId = R.drawable.ic_weather_snowstrom
            "雾" -> weatherId = R.drawable.ic_weather_foggy
            "霾" -> weatherId = R.drawable.ic_weather_haze
            "沙尘暴" -> weatherId = R.drawable.ic_weather_duststorm
            "强沙尘暴" -> weatherId = R.drawable.ic_weather_sandstorm
            "浮尘", "扬沙" -> weatherId = R.drawable.ic_weather_sand_or_dust
            else -> if (type.contains("尘") || type.contains("沙")) {
                weatherId = R.drawable.ic_weather_sand_or_dust
            } else if (type.contains("雾") || type.contains("霾")) {
                weatherId = R.drawable.ic_weather_foggy
            } else if (type.contains("雨")) {
                weatherId = R.drawable.ic_weather_ice_rain
            } else if (type.contains("雪") || type.contains("冰雹")) {
                weatherId = R.drawable.ic_weather_moderate_snow
            } else {
                weatherId = R.drawable.ic_weather_no
            }
        }

        return weatherId
    }

    fun getFirstLetter(pinyin: String): String {
        if (TextUtils.isEmpty(pinyin)) return "#"
        val c = pinyin.substring(0, 1)
        val pattern = Pattern.compile("^[A-Za-z]+$")
        if (pattern.matcher(c).matches()) {
            return c.toUpperCase()
        }
        return "#"
    }

}
