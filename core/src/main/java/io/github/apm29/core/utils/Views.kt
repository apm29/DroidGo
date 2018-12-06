package io.github.apm29.core.utils

import android.content.Context
import android.graphics.Rect
import android.view.View

/**
 * Determines if two views intersect in the window.
 */
infix fun View?.isIntersec(other: View?): Boolean {
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

fun Int.toPx(context: Context):Int{
    return (context.resources.displayMetrics.density * this).toInt()
}