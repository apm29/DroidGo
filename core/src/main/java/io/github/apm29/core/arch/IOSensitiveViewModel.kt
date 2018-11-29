package io.github.apm29.core.arch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.apm29.core.utils.Event
import io.reactivex.disposables.CompositeDisposable

open class IOSensitiveViewModel: ViewModel(), IOSensitive {

    override var loadingMessage: String? = null

    override val disposables: CompositeDisposable =
        CompositeDisposable()

    override val message: MutableLiveData<Event<String>> =
        MutableLiveData()

    override val loading: MutableLiveData<Event<Boolean>> =
        MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}