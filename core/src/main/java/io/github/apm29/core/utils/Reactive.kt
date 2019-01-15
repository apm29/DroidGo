package io.github.apm29.core.utils

import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.github.apm29.core.arch.IOSensitive
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Subscription
import timber.log.Timber


/**
 * Rx类 扩展方法
 */

fun <T> Single<T>.autoThreadSwitch(): Single<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.autoThreadSwitch(): Flowable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.autoThreadSwitch(): Maybe<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.autoThreadSwitch(): Observable<T> = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun Completable.autoThreadSwitch(): Completable = this.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeAuto(
    ioSensitive: IOSensitive? = null,
    loadingMessage: String? = null,
    onSuccess: (T) -> Unit
) =
    this
        .subscribe(
            object : SingleObserver<T> {
                override fun onSuccess(t: T) {
                    ioSensitive?.loadingMessage = null
                    onSuccess(t)
                    ioSensitive?.decreaseLoadingSignal()
                }

                override fun onSubscribe(d: Disposable) {
                    ioSensitive?.loadingMessage = loadingMessage
                    ioSensitive?.disposables?.add(d)
                    ioSensitive?.increaseLoadingSignal()
                }

                override fun onError(e: Throwable) {
                    ioSensitive?.loadingMessage = null
                    ioSensitive?.decreaseLoadingSignal()
                    ioSensitive?.sendMessage(e.message ?: e.toString())
                    e.printStackTrace()
                }

            }
        )

fun <T> Flowable<T>.subscribeAuto(
    ioSensitive: IOSensitive? = null,
    loadingMessage: String? = null,
    onNext: (T) -> Unit
) =
    this
        .subscribe(
            object : FlowableSubscriber<T> {
                override fun onSubscribe(subscription: Subscription) {
                    ioSensitive?.loadingMessage = loadingMessage
                    ioSensitive?.disposables?.add(Disposables.fromSubscription(subscription))
                    ioSensitive?.increaseLoadingSignal()
                }

                override fun onComplete() {
                    ioSensitive?.loadingMessage = null
                    ioSensitive?.decreaseLoadingSignal()
                }

                override fun onNext(t: T) {
                    onNext(t)
                }

                override fun onError(e: Throwable) {
                    ioSensitive?.loadingMessage = null
                    ioSensitive?.decreaseLoadingSignal()
                    ioSensitive?.sendMessage(e.message ?: e.toString())
                    e.printStackTrace()
                }

            }
        )


fun Completable.subscribeAuto(
    ioSensitive: IOSensitive? = null,
    loadingMessage: String? = null,
    onComplete: () -> Unit
) = subscribe(
    object : CompletableObserver {
        override fun onComplete() {
            ioSensitive?.loadingMessage = null
            onComplete()
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onSubscribe(d: Disposable) {
            ioSensitive?.loadingMessage = loadingMessage
            ioSensitive?.disposables?.add(d)
            ioSensitive?.increaseLoadingSignal()
        }

        override fun onError(e: Throwable) {
            ioSensitive?.loadingMessage = null
            ioSensitive?.decreaseLoadingSignal()
            ioSensitive?.sendMessage(e.localizedMessage.toString())
            e.printStackTrace()
        }

    }
)

fun <T> Maybe<T>.subscribeAuto(
    ioSensitive: IOSensitive? = null,
    loadingMessage: String? = null,
    onSuccess: (T) -> Unit
) = this.subscribe(
    object : MaybeObserver<T> {
        override fun onComplete() {
            ioSensitive?.loadingMessage = null
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onSuccess(t: T) {
            ioSensitive?.loadingMessage = null
            onSuccess(t)
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onSubscribe(d: Disposable) {
            ioSensitive?.loadingMessage = loadingMessage
            ioSensitive?.disposables?.add(d)
            ioSensitive?.increaseLoadingSignal()
        }

        override fun onError(e: Throwable) {
            ioSensitive?.loadingMessage = null
            ioSensitive?.decreaseLoadingSignal()
            ioSensitive?.sendMessage(e.localizedMessage.toString())
            e.printStackTrace()
        }

    }
)

fun <T> Observable<T>.subscribeAuto(
    ioSensitive: IOSensitive? = null,
    loadingMessage: String? = null,
    onSuccess: (T) -> Unit
) = this.subscribe(
    object : Observer<T> {
        override fun onComplete() {
            ioSensitive?.loadingMessage = null
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onNext(t: T) {
            ioSensitive?.loadingMessage = null
            onSuccess(t)
            ioSensitive?.decreaseLoadingSignal()
        }

        override fun onSubscribe(d: Disposable) {
            ioSensitive?.loadingMessage = loadingMessage
            ioSensitive?.disposables?.add(d)
            ioSensitive?.increaseLoadingSignal()
        }

        override fun onError(e: Throwable) {
            ioSensitive?.loadingMessage = null
            ioSensitive?.decreaseLoadingSignal()
            ioSensitive?.sendMessage(e.localizedMessage.toString())
            e.printStackTrace()
        }

    }
)


class Live<T> private constructor(private val mLifecycleOwner: LifecycleOwner) : ObservableTransformer<T, T>,
    LifecycleObserver {

    private val mSubject = PublishSubject.create<T>()

    private var mDisposable: Disposable? = null

    private var mData: T? = null

    private var mActive: Boolean = false

    private var mVersion = -1

    private var mLastVersion = -1

    @MainThread
    override fun apply(@NonNull upstream: Observable<T>): ObservableSource<T> {
        assertMainThread()
        if (mLifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
            mLifecycleOwner.lifecycle.addObserver(this)
            mDisposable = upstream.subscribe({ it ->
                assertMainThread()
                ++mVersion
                mData = it
                considerNotify()
            }, { it ->
                assertMainThread()
                mSubject.onError(it)
            }, ::assertMainThread)

            return mSubject.doOnDispose { mDisposable?.dispose() }
        } else {
            return Observable.empty()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    private fun onStateChange() {
        if (this.mLifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            if (mDisposable != null && mDisposable?.isDisposed == false) {
                Timber.d("dispose upstream")
                mDisposable?.dispose()
            }
            if (!mSubject.hasComplete()) {
                mSubject.onComplete()
            }
            mLifecycleOwner.lifecycle.removeObserver(this)
        } else {
            this.activeStateChanged(Live.isActiveState(mLifecycleOwner.lifecycle.currentState))
        }
    }

    private fun activeStateChanged(newActive: Boolean) {
        if (newActive != mActive) {
            mActive = newActive
            considerNotify()
        }
    }

    private fun considerNotify() {
        if (mActive) {
            if (isActiveState(mLifecycleOwner.lifecycle.currentState)) {
                if (mLastVersion < mVersion) {
                    mLastVersion = mVersion
                    if (!mSubject.hasComplete()) {
                        mSubject.onNext(mData!!)
                    }
                }
            }
        }
    }

    private fun assertMainThread() {
        if (!isMainThread) {
            throw IllegalStateException("You should not use the Live Transformer at a background thread.")
        }
    }

    companion object {

        private val TAG = "Live"

        fun <T> bindLifecycle(owner: LifecycleOwner): ObservableTransformer<T, T> {
            return Live(owner)
        }

        internal val isMainThread: Boolean
            get() = Looper.getMainLooper().thread === Thread.currentThread()

        internal fun isActiveState(state: Lifecycle.State): Boolean {
            return state.isAtLeast(Lifecycle.State.STARTED)
        }
    }

}