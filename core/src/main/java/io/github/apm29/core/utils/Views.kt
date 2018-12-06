package io.github.apm29.core.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.view.View

/**
 * Determines if two views intersect in the window.
 * 计算两个View占据的Rect之间是否相交
 */
fun View?.intersect(other: View?): Boolean {
    if (this == null || other == null) return false

    val view1Loc = IntArray(2)
    this.getLocationOnScreen(view1Loc)
    val view1Rect = Rect(
        view1Loc[0],
        view1Loc[1],
        view1Loc[0] + this.width,
        view1Loc[1] + this.height
    )
    val view2Loc = IntArray(2)
    other.getLocationOnScreen(view2Loc)
    val view2Rect = Rect(
        view2Loc[0],
        view2Loc[1],
        view2Loc[0] + other.width,
        view2Loc[1] + other.height
    )
    return view1Rect.intersect(view2Rect)
}

/**
 * dp -> px
 */
fun Int.toPx(context: Context):Int{
    return (context.resources.displayMetrics.density * this).toInt()
}

/**
 * view旋转动画
 * @param degreeInit 初始角度
 * @param rotateDegree 需要旋转多少
 * @param durationAnim 动画持续时间
 */
fun View.startRotate(degreeInit:Float = 0f, rotateDegree:Float = 180f, durationAnim:Long = 400){
    if (rotateAnimator.isRunning){
        return
    }
    rotateAnimator.duration = durationAnim
    rotateAnimator.addUpdateListener (
        object : ValueAnimator.AnimatorUpdateListener{
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val fraction = animation.animatedFraction
                this@startRotate.rotation = degreeInit + rotateDegree * fraction
                if (fraction == 1f){
                    rotateAnimator.removeUpdateListener(this)
                }
            }
        })
    rotateAnimator.start()
}

/**
 * view的扩展属性,用于旋转动画
 */
val View.rotateAnimator: ValueAnimator by lazy {
    ValueAnimator().apply {
        setFloatValues(1f)
        duration = 400
    }
}