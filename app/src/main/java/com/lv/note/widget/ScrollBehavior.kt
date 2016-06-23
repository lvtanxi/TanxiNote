package com.lv.note.widget

import android.content.Context
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.lv.note.R

class ScrollBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {
    private var mIsAnimatingOut = false

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?,
                                     directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        // 确定是在垂直方向上滑动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?,
                                target: View?, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed > 0 && !this.mIsAnimatingOut && child!!.visibility == View.VISIBLE) {
            // 不显示FAB
            animateOut(child)
        } else if (dyConsumed < 0 && child!!.visibility != View.VISIBLE) {
            // 显示FAB
            animateIn(child)
        }
    }

    // 定义滑动时的属性动画效果
    private fun animateOut(button: FloatingActionButton) {
        if (Build.VERSION.SDK_INT >= 1) {
            ViewCompat.animate(button).scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setInterpolator(INTERPOLATOR).withLayer().setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View) {
                    this@ScrollBehavior.mIsAnimatingOut = true
                }

                override fun onAnimationCancel(view: View) {
                    this@ScrollBehavior.mIsAnimatingOut = false
                }

                override fun onAnimationEnd(view: View) {
                    this@ScrollBehavior.mIsAnimatingOut = false
                    view.visibility = View.GONE
                }
            }).start()
        } else {
            val anim = AnimationUtils.loadAnimation(button.context, R.anim.push_up_out)
            anim.interpolator = INTERPOLATOR
            anim.duration = 200L
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    this@ScrollBehavior.mIsAnimatingOut = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    this@ScrollBehavior.mIsAnimatingOut = false
                    button.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })
            button.startAnimation(anim)
        }
    }

    private fun animateIn(button: FloatingActionButton) {
        button.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= 1) {
            ViewCompat.animate(button).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start()
        } else {
            val anim = AnimationUtils.loadAnimation(button.context, R.anim.push_up_in)
            anim.duration = 200L
            anim.interpolator = INTERPOLATOR
            button.startAnimation(anim)
        }
    }

    companion object {
        private val INTERPOLATOR = FastOutSlowInInterpolator()
    }
}