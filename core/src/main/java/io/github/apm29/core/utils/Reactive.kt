package io.github.apm29.core.utils

import io.github.apm29.core.arch.Event
import io.github.apm29.core.arch.IOSensitive
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.autoThreadSwitch() = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeAuto(ioSensitive: IOSensitive,onSuccess:(T)->Unit) = this.subscribe(
    object :SingleObserver<T>{
        override fun onSuccess(t: T) {
            onSuccess(t)
            ioSensitive.loading.value = Event(false)
        }

        override fun onSubscribe(d: Disposable) {
            ioSensitive.loading.value = Event(true)
        }

        override fun onError(e: Throwable) {
            ioSensitive.loading.value = Event(false)
            ioSensitive.message.value = Event(e.message.toString())
            e.printStackTrace()
        }

    }
)