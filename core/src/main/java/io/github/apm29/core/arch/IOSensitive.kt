package io.github.apm29.core.arch

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.utils.Event
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.atomic.AtomicInteger

/**
 * 对数据io加载状态敏感
 */
interface IOSensitive {


    /**
     * 是否在加载
     */
    val loading: MutableLiveData<Event<Boolean>>
    /**
     * 错误信息
     */
    val message: MutableLiveData<Event<String>>
    /**
     * disposable合集
     */
    val disposables: CompositeDisposable

    /**
     * loading 信息
     */
    var loadingMessage: String?

    /**
     * 需要子类定一个初始值
     */
    var signalStrength: AtomicInteger

    fun increaseLoadingSignal(message: String? = null) {
        val signal = signalStrength.incrementAndGet()
        signalLoading(signal)
        message?.apply {
            loadingMessage = this
        }
    }

    private fun signalLoading(signal: Int) {
        if (signal > 0) {
            loading.value = Event(true)
        } else {
            loading.value = Event(false)
        }

    }

    fun decreaseLoadingSignal() {
        val signal = signalStrength.decrementAndGet()
        signalLoading(signal)
    }

    fun sendMessage(err: String) {
        message.value = Event(err)
    }
}