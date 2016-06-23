package com.lv.note.helper


/**
 * User: 吕勇
 * Date: 2016-06-13
 * Time: 12:56
 * Description:网络加载view
 */
interface IBaseView {

    fun showLodingView()

    fun hideLodingView()

    fun toastError(message: String)

}