package io.github.apm29.core.arch

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * baseView应该把view显示的逻辑分离出去,IViewStateDelegate只需要获取View不同状态的layoutRes/ViewInstance/ViewID
 * 给IViewStateDelegate,给IViewStateDelegate负责view的显示/隐藏.
 * 不同层级的View(Activity/Fragment/Custom Views)可以将io加载事件委托到上一层或者保留在本层,
 * 代理的执行者是IOSensitive,委托方式待定
 */
interface IViewStateDelegate {
    fun startLoading()
    fun stopLoading()
    fun provideIOSensitive(): IOSensitive
}

class ViewStateManager private constructor(
    private val ioObservable: IOObservable,
    lifecycleOwner: LifecycleOwner,
    private val hideNormalWhenLoading: Boolean = false
) :
    IViewStateDelegate {

    companion object {
        fun register(
            ioObservable: IOObservable,
            lifecycleOwner: LifecycleOwner,
            hideNormalWhenLoading: Boolean = false
        ): ViewStateManager {
            return ViewStateManager(ioObservable, lifecycleOwner, hideNormalWhenLoading)
        }
    }


    init {
        ioObservable.ioSensitive.loading.observe(lifecycleOwner, Observer { event ->
            event.getDataIfNotConsumed { loading ->
                if (loading) {
                    startLoading()
                } else {
                    stopLoading()
                }
                println("${ioObservable.javaClass.canonicalName} loading state: $loading")
            }
        })
    }

    override fun startLoading() {
        ioObservable.viewLoading.isVisible = true
        if (hideNormalWhenLoading) {
            ioObservable.viewNormal.isVisible = false
        }
    }

    override fun stopLoading() {
        ioObservable.viewLoading.isVisible = false
        if (hideNormalWhenLoading) {
            ioObservable.viewNormal.isVisible = true
        }
    }

    override fun provideIOSensitive(): IOSensitive {
        return ioObservable.ioSensitive
    }

}

class ActivityIOManager(val activity: AppCompatActivity) {
    fun provideIoSensitive(): IOSensitive {
        return ViewModelProviders.of(activity)[BaseActivityViewModel::class.java]
    }
}

class Act : AppCompatActivity(){
    private val activityIoManager: ActivityIOManager by lazy {
        ActivityIOManager(this)
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

}

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}