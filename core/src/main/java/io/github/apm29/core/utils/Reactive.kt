package io.github.apm29.core.utils

import io.github.apm29.core.arch.IOSensitive
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Rx类 扩展方法
 */

fun <T> Single<T>.autoThreadSwitch(): Single<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeAuto(ioSensitive: IOSensitive? = null, onSuccess: (T) -> Unit) = this.subscribe(
    object : SingleObserver<T> {
        override fun onSuccess(t: T) {
            onSuccess(t)
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onSubscribe(d: Disposable) {
            ioSensitive?.increaseLoadingSignal()
        }

        override fun onError(e: Throwable) {
            ioSensitive?.decreaseLoadingSignal()
            ioSensitive?.sendMessage(e.localizedMessage.toString())
            e.printStackTrace()
        }

    }
)