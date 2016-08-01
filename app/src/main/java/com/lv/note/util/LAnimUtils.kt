package com.lv.note.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView

/**
 * User: 吕勇
 * Date: 2016-07-22
 * Time: 16:02
 * Description:普通的anim集合
 */
object LAnimUtils {

    val PERFECT_MILLS: Long = 350
    val MINI_RADIUS = 0


    /**
     * 向四周伸张，直到完成显示。
     */
    @JvmOverloads fun showAsCircular(myView: View, startRadius: Float = MINI_RADIUS.toFloat(), durationMills: Long = PERFECT_MILLS) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            showView(myView)
        } else {
            val cx = (myView.left + myView.right) / 2
            val cy = (myView.top + myView.bottom) / 2

            val w = myView.width
            val h = myView.height

            // 勾股定理 & 进一法
            val finalRadius = Math.sqrt((w * w + h * h).toDouble()).toInt() + 1

            val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, startRadius, finalRadius.toFloat())
            myView.visibility = View.VISIBLE
            anim.duration = durationMills
            anim.start()
        }

    }

    /**
     * 向四周伸张，直到完成显示。
     */
    fun showView(view: View) {
        // 收缩按钮
        view.visibility = View.VISIBLE
        val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = PERFECT_MILLS
        scaleAnimation.setAnimationListener(object : CustAnimationListener(view) {
            override fun onAnimationEnd(animation: Animation) {
                mView.clearAnimation()
            }
        })
        view.startAnimation(scaleAnimation)
    }

    /**
     * 由满向中间收缩，直到隐藏。
     */
    fun hideView(view: View) {
        // 收缩按钮
        val scaleAnimation = ScaleAnimation(1f, 0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = PERFECT_MILLS
        scaleAnimation.setAnimationListener(object : CustAnimationListener(view) {
            override fun onAnimationEnd(animation: Animation) {
                mView.visibility = View.GONE
                mView.clearAnimation()
            }
        })
        view.startAnimation(scaleAnimation)
    }


    /**
     * 由满向中间收缩，直到隐藏。
     */
    @JvmOverloads fun hideAsCircular(myView: View, endRadius: Float = MINI_RADIUS.toFloat(), durationMills: Long = PERFECT_MILLS) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            hideView(myView)
        } else {
            val cx = (myView.left + myView.right) / 2
            val cy = (myView.top + myView.bottom) / 2
            val w = myView.width
            val h = myView.height

            // 勾股定理 & 进一法
            val initialRadius = Math.sqrt((w * w + h * h).toDouble()).toInt() + 1

            val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius.toFloat(), endRadius)
            anim.duration = durationMills
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            })

            anim.start()
        }


    }

    /**
     * view旋转
     */
    fun viewRotate(view: View, startAngle: Int, endAngle: Int) {
        val rotateAnimation = RotateAnimation(startAngle.toFloat(), endAngle.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)//显示动画
        rotateAnimation.duration = PERFECT_MILLS
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }


    /**
     * 从指定View开始向四周伸张(伸张颜色或图片为colorOrImageRes), 然后进入另一个Activity,
     * 返回至 @thisActivity 后显示收缩动画。
     */
    fun startActivityForResultAsCircular(
            thisActivity: Activity, intent: Intent, requestCode: Int?, bundle: Bundle?,
            triggerView: View, colorOrImageRes: Int, durationMills: Long) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            thisActivity.startActivity(intent)
        } else {
            val location = IntArray(2)
            triggerView.getLocationInWindow(location)
            val cx = location[0] + triggerView.width / 2
            val cy = location[1] + triggerView.height / 2
            val view = ImageView(thisActivity)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
            view.setImageResource(colorOrImageRes)
            val decorView = thisActivity.window.decorView as ViewGroup
            val w = decorView.width
            val h = decorView.height
            decorView.addView(view, w, h)
            val finalRadius = Math.sqrt((w * w + h * h).toDouble()).toInt() + 1
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
            anim.duration = durationMills
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        if (requestCode == null)
                            thisActivity.startActivity(intent)
                        else if (bundle == null)
                            thisActivity.startActivityForResult(intent, requestCode)
                        else
                            thisActivity.startActivityForResult(intent, requestCode, bundle)

                        // 默认渐隐过渡动画.
                        thisActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        // 默认显示返回至当前Activity的动画.
                        triggerView.postDelayed({
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius.toFloat(), 0f)
                                anim.duration = durationMills
                                anim.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        try {
                                            decorView.removeView(view)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                })
                                anim.start()
                            }
                        }, PERFECT_MILLS)
                    }

                }
            })
            anim.start()
        }


    }


    /*下面的方法全是重载，用简化上面方法的构建*/


    fun startActivityForResultAsCircular(
            thisActivity: Activity, intent: Intent, requestCode: Int?, triggerView: View, colorOrImageRes: Int) {
        startActivityForResultAsCircular(thisActivity, intent, requestCode, null, triggerView, colorOrImageRes, PERFECT_MILLS)
    }

    @JvmOverloads fun startActivityAsCircular(
            thisActivity: Activity, intent: Intent, triggerView: View, colorOrImageRes: Int, durationMills: Long = PERFECT_MILLS) {
        startActivityForResultAsCircular(thisActivity, intent, null, null, triggerView, colorOrImageRes, durationMills)
    }

    fun startActivityAsCircular(thisActivity: Activity, targetClass: Class<*>, triggerView: View, colorOrImageRes: Int) {
        startActivityAsCircular(thisActivity, Intent(thisActivity, targetClass), triggerView, colorOrImageRes, PERFECT_MILLS)
    }


    abstract class CustAnimationListener(protected var mView: View) : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {

        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    }

}
