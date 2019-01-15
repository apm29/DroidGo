package io.github.apm29.core.arch

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.utils.Event
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * 对数据io加载状态敏感,配合Reactive工具类中threadAutoSwitch/subscribeAuto方法实现,将页面的加载状态委托到
 * Activity/Fragment层级的IOObservable
 * @see io.github.apm29.core.utils.autoThreadSwitch
 * @see io.github.apm29.core.utils.subscribeAuto
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
        Timber.d("signal = $signal")
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

    fun resetLoadingSignal() {
        signalStrength.set(0)
        signalLoading(0)
    }

    fun sendMessage(err: String) {
        message.value = Event(err)
    }

}

fun <T> Single<T>.bindWithIO(ioSensitive: IOSensitive, message: String? = null): Single<T> {
    return this.compose {
        it.doAfterSuccess {
            ioSensitive.loadingMessage = null
            ioSensitive.decreaseLoadingSignal()
        }.doOnSubscribe {
            ioSensitive.disposables.add(it)
            ioSensitive.increaseLoadingSignal(message)
        }.doOnError {
            ioSensitive.loadingMessage = null
            ioSensitive.decreaseLoadingSignal()
        }
    }
}