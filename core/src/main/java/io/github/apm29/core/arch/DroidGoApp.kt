package io.github.apm29.core.arch

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import io.github.apm29.core.arch.dagger.*

/**
 * 示例Application
 */
class DroidGoApp : Application(), App {

    val mCoreComponent: CoreComponent by lazy {
        DaggerCoreComponent.builder()
            .app(AppModule(application()))
            .shared()
            .remote()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }


    override fun application(): Application {
        return this
    }

    companion object {
        fun getCoreComponent(context: Context) =
            (context.applicationContext as DroidGoApp).mCoreComponent
    }
}



