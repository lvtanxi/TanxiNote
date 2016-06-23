package com.lv.note.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

/**
 * User: 吕勇
 * Date: 2016-05-18
 * Time: 9:39
 * Description:RecyclerView 淡进动画
 */

class AlphaInAnimation @JvmOverloads constructor(private val mFrom: Float = AlphaInAnimation.DEFAULT_ALPHA_FROM) : BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))
    }

    companion object {

        private val DEFAULT_ALPHA_FROM = 0f
    }
}
