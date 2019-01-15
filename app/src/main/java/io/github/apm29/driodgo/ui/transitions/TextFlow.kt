package io.github.apm29.driodgo.ui.transitions

import android.animation.Animator
import android.transition.Transition
import android.transition.TransitionValues
import android.view.ViewGroup
import android.widget.TextView
import timber.log.Timber

class TextFlow : Transition() {

    private val PROPNAME_TEXT_LINES = "droid_go:text_flow:text_lines"
    private val PROPNAME_TEXT_SIZE = "droid_go:text_flow:text_size"
    private val sTransitionProperties = arrayOf(
        PROPNAME_TEXT_LINES,
        PROPNAME_TEXT_SIZE
    )

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        val values = transitionValues.values
        if (view is TextView) {
            values[PROPNAME_TEXT_LINES] = view.lineCount
            values[PROPNAME_TEXT_SIZE] = view.textSize
        }
    }

    override fun getTransitionProperties(): Array<String>? {
        return sTransitionProperties
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        Timber.d("$startValues")
        Timber.d("$endValues")

        return super.createAnimator(sceneRoot, startValues, endValues)
    }


}