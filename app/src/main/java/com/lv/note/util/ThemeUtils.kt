package com.lv.note.util

import android.app.Activity
import android.view.View
import com.lv.note.R
import com.lv.note.ui.HomeAct
import com.orhanobut.hawk.Hawk


/**
 * User: 吕勇
 * Date: 2016-06-27
 * Time: 09:44
 * Description:
 */
object ThemeUtils {
    val THEMEID = "themeId"

    fun saveTheme(activity: Activity, tageView: View, whichId: Int) {
        var themeId = R.style.AppTheme
        when (whichId) {
            R.id.action_theme_pink ->
                themeId = R.style.AppTheme_pink
            R.id.action_theme_purple ->
                themeId = R.style.AppTheme_purple
            R.id.action_theme_blue ->
                themeId = R.style.AppTheme_blue
            R.id.action_theme_green ->
                themeId = R.style.AppTheme_green
            R.id.action_theme_yellow ->
                themeId = R.style.AppTheme_yellow
            R.id.action_theme_orange ->
                themeId = R.style.AppTheme_orange
        }
        Hawk.put(THEMEID, themeId)
        activity.openNewAct(HomeAct::class.java, tageView)
        activity.finish()
    }

    fun obtainCurrentTheme(): Int {
        return Hawk.get(THEMEID, R.style.AppTheme)
    }

    fun obtainThemeColor(): Int {
        val themeId = Hawk.get(THEMEID, R.style.AppTheme)
        when (themeId) {
            R.style.AppTheme_pink ->
                return R.color.theme_pink
            R.style.AppTheme_purple ->
                return R.color.theme_purple
            R.style.AppTheme_blue ->
                return R.color.theme_blue
            R.style.AppTheme_green ->
                return R.color.theme_green
            R.style.AppTheme_yellow ->
                return R.color.theme_yellow
            R.style.AppTheme_orange ->
                return R.color.theme_orange
            else ->
                return R.color.theme_color_primary

        }
    }

}