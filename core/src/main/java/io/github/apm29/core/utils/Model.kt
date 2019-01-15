package io.github.apm29.core.utils

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 事件,data获取一次后不可再次获取
 */
data class Event<T>(
    private val data: T
) {
    var consumed = false

    fun getDataIfNotConsumed(callback: (T) -> Unit) {
        if (!consumed) {
            callback(data)
            consumed = true
        }
    }

    fun getDataIfNotConsumed(): T? {
        return if (!consumed) {
            consumed = true
            data
        } else {
            null
        }
    }

    /**
     * 需要重用的时候返回data
     */
    fun peek(): T {
        return data
    }

    private val classesThatHandledTheEvent = HashSet<String>(0)

    /**
     * 对应class未使用时返回data,否则为null
     * Returns the content and prevents its use again from the given class.
     */
    fun getDataIfNotConsumed(classThatWantToUseEvent: Any): T? {
        val canonicalName = classThatWantToUseEvent::javaClass.get().canonicalName

        canonicalName?.let {
            return if (!classesThatHandledTheEvent.contains(canonicalName)) {
                classesThatHandledTheEvent.add(canonicalName)
                data
            } else {
                null
            }
        } ?: return null
    }


}

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Timber.w("Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}
