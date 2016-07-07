package com.lv.note.widget.selectpop

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.lv.note.R
import com.lv.test.notEmptyList
import java.util.*

/**
 * User: 吕勇
 * Date: 2015-11-17
 * Time: 11:51
 * Description:选择PopupWindow(后期如果item太多了，可换成ListView处理)
 */

 abstract class SelectPopupWindow<T : ExtendItem>(private val mContext: Context, title: String?, spinnerItems: List<T>) : PopupWindow(mContext), OnClickListener {

    private var popLayout: LinearLayout? = null
    private var selectPopPanent: LinearLayout? = null
    private var btnTakeTitle: TextView? = null
    private var btnCancel: Button? = null
    private var scrollView: ScrollView? = null


    private fun assignViews(title: String?): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.select_pop, null, false)
        popLayout = view.findViewById(R.id.pop_layout) as LinearLayout
        selectPopPanent = view.findViewById(R.id.select_pop_panent) as LinearLayout
        btnTakeTitle = view.findViewById(R.id.btn_take_title) as TextView
        btnCancel = view.findViewById(R.id.btn_cancel) as Button
        scrollView = view.findViewById(R.id.select_pop_scrollView) as ScrollView
        title?.let{
            btnTakeTitle!!.visibility = View.VISIBLE
            btnTakeTitle!!.text = title
        }
        //取消按钮
        btnCancel!!.setOnClickListener {
            //销毁弹出框
            dismiss()
            //                SettingActivity.isClick = false;
        }
        return view
    }


    init {
        val popup = assignViews(title)
        addItem(spinnerItems)

        //设置按钮监听

        //设置SelectPicPopupWindow的View
        this.contentView = popup
        //设置SelectPicPopupWindow弹出窗体的宽
        this.width = LayoutParams.MATCH_PARENT
        //设置SelectPicPopupWindow弹出窗体的高
        this.height = LayoutParams.MATCH_PARENT
        //设置SelectPicPopupWindow弹出窗体可点击
        this.isFocusable = true
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.animationStyle = R.style.ActionSheetAnimation
        //实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(0xb0000000.toInt())
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw)
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        popup.setOnTouchListener { v, event ->
            val height = popLayout!!.top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }
    }

    internal fun addItem(spinnerItems: List<T>) {
        if (spinnerItems.notEmptyList()) {
            if (spinnerItems.size >= 7) {
                val dm = DisplayMetrics()
                (mContext as Activity).windowManager.defaultDisplay.getMetrics(dm)
                val params = scrollView!!.layoutParams as LinearLayout.LayoutParams
                params.height = dm.heightPixels / 2
                scrollView!!.layoutParams = params
            }
            val dividerMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    48.0f, mContext.resources.displayMetrics).toInt()
            val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dividerMargin)
            val color = ContextCompat.getColor(mContext, R.color.blue)
            var itemBtn: Button
            val paramsLine = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1)
            var itemView:View
            for ((index, item) in spinnerItems.withIndex()) {
                itemBtn = Button(mContext)
                itemBtn.textSize = 15.0f
                itemBtn.setTextColor(color)
                itemBtn.layoutParams = params
                itemBtn.text = item.value
                itemBtn.tag = item
                itemBtn.setOnClickListener(this)
                if(0 == index){
                    if(btnTakeTitle!!.visibility == View.VISIBLE){
                        itemBtn.setBackgroundResource(if(spinnerItems.size==1)R.drawable.btn_dialog_selector else R.drawable.border_top_bottom_gray_selector)
                    }else{
                        itemBtn.setBackgroundResource(if(spinnerItems.size==1)R.drawable.btn_dialog_selector else R.drawable.btn_top)
                    }
                }else if(index == spinnerItems.size - 1){
                    if(spinnerItems.size==2&&btnTakeTitle!!.visibility != View.VISIBLE){
                        itemView= View(mContext)
                        itemView.layoutParams=paramsLine
                        selectPopPanent!!.addView(itemView)
                    }
                    itemBtn.setBackgroundResource(R.drawable.btn_bottom)
                } else{
                    if(btnTakeTitle!!.visibility != View.VISIBLE &&index==1){
                        itemBtn.setBackgroundResource(R.drawable.border_top_bottom_gray_selector)
                    }else{
                        itemBtn.setBackgroundResource(R.drawable.background_border_bottom_gray_selector)
                    }
                }
                selectPopPanent!!.addView(itemBtn)
            }
        }
    }

    override fun onClick(v: View) {
        if (v.tag != null)
            selectPopupBack(v.tag as T)
        dismiss()
    }

    protected open  fun selectPopupBack(item:T){

    }


    fun show(view: View) {
        showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0)
    }

    companion object {


        /**获取默认的item选项
         * @param items item的字符串
         */
        fun getDefExtendItems(vararg items: String): List<DefExtendItem> {
            val itemList = ArrayList<DefExtendItem>()
            var item: ExtendItem
            for (i in items.indices) {
                item = DefExtendItem(i, items[i])
                itemList.add(item)
            }
            return itemList
        }
    }

}  