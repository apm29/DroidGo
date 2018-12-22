package io.github.apm29.core.arch

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import io.github.apm29.core.BuildConfig
import io.github.apm29.core.arch.dagger.*
import timber.log.Timber

/**
 * 示例Application
 */
open class DroidGoApp : Application(), App {

    val mCoreComponent: CoreComponent by lazy {
        DaggerCoreComponent.builder()
            .app(AppModule(application()))
            .shared()
            .remote()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        Stetho.initializeWithDefaults(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

    }


    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        }
    }


    override fun application(): Application {
        return this
    }

    fun coreComponent(): CoreComponent {
        return mCoreComponent
    }

    companion object {

        lateinit var application: DroidGoApp

        fun getCoreComponent(context: Context) =
            (context.applicationContext as DroidGoApp).mCoreComponent
    }
}



