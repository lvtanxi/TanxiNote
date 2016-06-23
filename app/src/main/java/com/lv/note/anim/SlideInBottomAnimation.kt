package com.lv.note.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

/**
 * User: 吕勇
 * Date: 2016-05-18
 * Time: 9:39
 * Description:RecyclerView 下先动画
 */

class SlideInBottomAnimation : BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, "translationY", view.measuredHeight*1.0f, 0.0f))
    }
}