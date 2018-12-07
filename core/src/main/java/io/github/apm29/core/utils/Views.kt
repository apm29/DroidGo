package io.github.apm29.core.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import io.github.apm29.core.R

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
fun Int.toPx(context: Context): Int {
    return (context.resources.displayMetrics.density * this).toInt()
}

/**
 * view旋转动画
 * @param degreeInit 初始角度
 * @param rotateDegree 需要旋转多少
 * @param durationAnim 动画持续时间
 */
fun View.startRotate(degreeInit: Float = 0f, rotateDegree: Float = 180f, durationAnim: Long = 400): Boolean {
    if (rotateAnimator.isRunning) {
        return false
    }
    rotateAnimator.duration = durationAnim
    rotateAnimator.addUpdateListener(
        object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val fraction = animation.animatedFraction
                this@startRotate.rotation = degreeInit + rotateDegree * fraction
                if (fraction == 1f) {
                    rotateAnimator.removeUpdateListener(this)
                }
            }
        })
    rotateAnimator.start()
    return true
}

/**
 * View 缩小动画
 */
fun View.shrink(durationAnim: Long = 400):Boolean {
    if (shrinkAndGrowAnimator.isRunning) {
        return false
    }
    shrinkAndGrowAnimator.duration = durationAnim
    val originHeight = measuredHeight
    shrinkAndGrowAnimator.addUpdateListener(
        object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val fraction = animation.animatedFraction
                this@shrink.layoutParams.height = (originHeight - originHeight * fraction).toInt()
                requestLayout()
                if (fraction == 1f) {
                    visibility = View.GONE
                    rotateAnimator.removeUpdateListener(this)
                }
            }
        })
    shrinkAndGrowAnimator.start()
    return true
}

/**
 * view 生长动画
 * 需要先设置expectedHeight,可以通过ViewTreeObserve获取布局后的高度,赋值给扩展属性expectedHeight
 */
fun View.grow(durationAnim: Long = 400): Boolean {
    if (shrinkAndGrowAnimator.isRunning) {
        return false
    }
    shrinkAndGrowAnimator.duration = durationAnim

    shrinkAndGrowAnimator.addUpdateListener(
        object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val fraction = animation.animatedFraction
                this@grow.layoutParams.height = (expectedHeight * fraction).toInt()
                requestLayout()
                if (!isVisible) {
                    visibility = View.VISIBLE
                }
                if (fraction == 1f) {
                    rotateAnimator.removeUpdateListener(this)
                }
            }
        })
    shrinkAndGrowAnimator.start()
    return true
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


/**
 * view的扩展属性,用于缩小生长动画
 */
val View.shrinkAndGrowAnimator: ValueAnimator by lazy {
    ValueAnimator().apply {
        setFloatValues(1f)
        duration = 400
    }
}
var View.expectedHeight: Int
    get() = getTag(R.id.tag_expectedHeight) as? Int ?: 0
    set(value) {
        setTag(R.id.tag_expectedHeight, value)
    }