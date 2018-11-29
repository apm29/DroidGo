package io.github.apm29.core.arch

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.utils.Event
import io.reactivex.disposables.CompositeDisposable

/**
 * 对数据io加载状态敏感
 */
interface IOSensitive{
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
    var loadingMessage:String?
}