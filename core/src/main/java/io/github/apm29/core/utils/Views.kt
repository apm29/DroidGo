package io.github.apm29.core.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.TextInputLayout
import io.github.apm29.core.R
import io.github.apm29.core.arch.DroidGoApp
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

}

fun TextView.getTextOrEmpty(): String {
    return this.text?.toString()?.trim() ?: ""
}


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
fun Int.dp(context: Context): Int {
    return (context.resources.displayMetrics.density * this).toInt()
}

/**
 * sp -> px
 */
fun Int.sp(context: Context): Int {
    return (context.resources.displayMetrics.scaledDensity * this).toInt()
}

/**
 * view旋转动画
 * @param degreeInit 初始角度
 * @param rotateDegree 需要旋转多少
 * @param durationAnim 动画持续时间
 * @return 如果在动画中就返回false
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
 * @return 如果在动画中就返回false
 */
fun View.shrink(durationAnim: Long = 400): Boolean {
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
 * ioObservable 生长动画
 * 需要先设置expectedHeight,可以通过ViewTreeObserve获取布局后的高度,赋值给扩展属性expectedHeight
 * @return  如果在动画中就返回false
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
 * view的扩展属性,用于shrink/grow动画
 */
val View.shrinkAndGrowAnimator: ValueAnimator by lazy {
    ValueAnimator().apply {
        setFloatValues(1f)
        duration = 400
    }
}
/**
 * 扩展属性,用于grow动画预设最大生长高度
 */
var View.expectedHeight: Int
    get() = getTag(R.id.tag_expectedHeight) as? Int ?: 200.dp(context)
    set(value) {
        setTag(R.id.tag_expectedHeight, value)
    }


fun View?.hideSoftInput() {
    if (this != null) {
        val inputMethodManager = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val windowToken = this.windowToken
        windowToken?.let {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}


data class Verify constructor(var error: String, var success: Boolean, var currentView: View?) {

    companion object {
        @JvmStatic
        fun init(): Verify {
            return Verify(error = "验证完成", success = true, currentView = null)
        }
    }

    fun verifyText(
        text: TextView,
        validate: (verify: Verify, text: String?, hint: String?) -> Verify = emptyVerify
    ): Verify {
        if (!success) {
            return this
        }
        this.currentView = text
        val content = text.text?.toString()
        val result = validate(this, content, text.hint?.toString())
        this.success = result.success
        this.error = result.error
        return this
    }


    fun verifyPicker(
        text: TextView,
        validate: (verify: Verify, text: String?, hint: String?) -> Verify = pickerEmptyVerify
    ): Verify {
        if (!success) {
            return this
        }
        this.currentView = text
        val content = text.text?.toString()
        val result = validate(this, content, text.hint?.toString())
        this.success = result.success
        this.error = result.error
        this.currentView = result.currentView
        if (!success) {
            text.error = result.error

            val simpleTextWatcher = object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    text.error = null
                }
            }
            text.removeTextChangedListener(simpleTextWatcher)
            text.addTextChangedListener(simpleTextWatcher)
        } else {
            text.error = null
        }
        return this
    }

    fun verifyEdit(
        inputLayout: TextInputLayout,
        validate: (verify: Verify, text: String?, hint: String?) -> Verify = emptyVerify
    ): Verify {
        if (!success) {
            return this
        }
        this.currentView = inputLayout
        val content = inputLayout.editText?.text?.toString()
        val result = validate(this, content, inputLayout.editText?.hint?.toString())
        this.success = result.success
        this.error = result.error
        this.currentView = result.currentView
        if (!success) {
            inputLayout.error = result.error

            val simpleTextWatcher = object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inputLayout.error = null
                }
            }
            inputLayout.editText?.removeTextChangedListener(simpleTextWatcher)
            inputLayout.editText?.addTextChangedListener(simpleTextWatcher)
        } else {
            inputLayout.error = null
        }
        return this
    }

    fun verifyEdit(
        editText: EditText,
        validate: (verify: Verify, text: String?, hint: String?) -> Verify = emptyVerify
    ): Verify {
        if (!success) {
            return this
        }
        this.currentView = editText
        val content = editText.text?.toString()
        val result = validate(this, content, editText.hint.toString())
        this.success = result.success
        this.error = result.error
        this.currentView = result.currentView
        if (!success) {
            editText.error = result.error
            val simpleTextWatcher =
                editText.getTag(R.id.TAG_TEXT_ERROR_WATCHER) as? SimpleTextWatcher ?: object : SimpleTextWatcher() {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        editText.error = null
                    }
                }
            editText.setTag(R.id.TAG_TEXT_ERROR_WATCHER,simpleTextWatcher)
            editText.removeTextChangedListener(simpleTextWatcher)
            editText.addTextChangedListener(simpleTextWatcher)
        } else {
            editText.error = null
        }
        return this
    }

    fun verifyConditional(prediction: Boolean, verifyAction: (Verify) -> Verify): Verify {
        if (prediction) {
            return verifyAction(this)
        }
        return this
    }


    private val emptyVerify: (Verify, String?, String?) -> Verify = { verify, text, hint ->
        if (!verify.success) {
            verify
        } else {
            verify.error = hint ?: "输入项不可为空"
            verify.success = !(text.isNullOrEmpty())
            verify
        }
    }

    private val pickerEmptyVerify: (verify: Verify, text: String?, String?) -> Verify = { verify, text, hint ->
        if (!verify.success) {
            verify
        } else {
            verify.error = hint ?: "选择项不可为空"
            verify.success = (!(text.isNullOrEmpty())) && text !=
                    DroidGoApp.application.getString(R.string.text_unselected)
            verify
        }
    }

}


val mobileVerify: (Verify, String?, String?) -> Verify = { verify, text, hint ->
    val trimText = text?.replace(" ", "")
    if (!verify.success) {
        verify
    } else {
        verify.error = hint ?: "手机号输入错误"
        verify.success =
                (!(trimText.isNullOrEmpty()))
                && trimText.length == 11
        if (trimText?.length != 11) {
            verify.error = "手机号码长度必须为11"
        }
        verify.currentView?.let {
            if (it is TextView) {
                it.error = if (verify.success) null else verify.error
            }
            if (it is TextInputLayout) {
                it.error = if (verify.success) null else verify.error
            }
        }
        verify
    }
}


fun View?.findScrollViewAndScroll() {
    this?.requestFocus()
    val parent = findScrollView()
    parent?.scrollTo(0, this?.top ?: parent.scrollY)
}

private fun View?.findScrollView(): NestedScrollView? {
    return if (this is NestedScrollView) {
        this
    } else {
        (this?.parent as? View)?.findScrollView()
    }
}

fun View.dp(dp: Int): Int {
    return dp.dp(context)
}

fun View.sp(sp: Int): Int {
    return sp.sp(context)
}

fun View.colorOf(@ColorRes res: Int): Int {
    return context.colorOf(res)
}


@SuppressLint("CheckResult")
fun View.setFilteredOnClickListener(doAfterSet: (() -> Unit)? = null, listener: (View) -> Unit) {
    Flowable.create(FlowableOnSubscribe<View> { emitter ->
        setOnClickListener {
            if (!emitter.isCancelled) {
                emitter.onNext(it)
            }
        }
        doAfterSet?.invoke()
    }, BackpressureStrategy.LATEST)
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .throttleFirst(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe {
            Timber.d("clicked ioObservable handled:$it")
            listener(it)
        }
}