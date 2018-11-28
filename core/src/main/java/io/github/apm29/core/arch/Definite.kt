package io.github.apm29.core.arch

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.apm29.core.BuildConfig
import io.reactivex.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


/**
 * 核心模块
 * 提供:application层面单例的内容
 */
@Singleton
@Component(
    modules = [
        RemoteRepositoryModule::class,
        SharedPreferencesModule::class,
        LocalRepositoryModule::class
    ]
)
interface CoreComponent {
    fun inject(app: DroidGoApp)

    fun okHttp():OkHttpClient

    fun retrofit(): Retrofit

    fun gson(): Gson

    fun shared(): SharedPreferences

    fun application(): Application

    fun userDao():UserDao

    @Component.Builder
    interface Builder {

        fun build(): CoreComponent

        fun app(appModule: AppModule):Builder

        fun remote(remoteRepositoryModule: RemoteRepositoryModule = RemoteRepositoryModule()): Builder

        fun shared(sharedPreferences: SharedPreferencesModule = SharedPreferencesModule()): Builder

        fun local(localRepositoryModule: LocalRepositoryModule = LocalRepositoryModule()): Builder
    }

}

class AppDataRepository:DataRepository{

}

/**
 * 核心配置
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application {
        return application
    }

    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create()
    }
}


/**
 * 基础network配置
 */
@Module(
    includes = [AppModule::class]
)
class RemoteRepositoryModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("https://dribbble.com/")
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(
                //日志LOG
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request-LOG")
                    .response("Response-LOG")
                    .addHeader("version", BuildConfig.VERSION_NAME)
                    .build()
            )
            .build()
    }
}

/**
 * 基础network配置
 */
@Module(
    includes = [AppModule::class]
)
class LocalRepositoryModule {

    @Singleton
    @Provides
    fun provideUserDataBase(context: Context): UserLocalDataBase = UserLocalDataBase.getInstance(context)

    @Singleton
    @Provides
    fun provideUserDao(userLocalDataBase: UserLocalDataBase): UserDao = userLocalDataBase.getUserDao()

}

/**
 * 基础SP配置
 */
@Module(
    includes = [
        AppModule::class
    ]
)
class SharedPreferencesModule {

    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("stored_sp_data", Context.MODE_PRIVATE)
    }
}


interface DataRepository {

}


/**
 * App
 */
interface App {

    fun application(): Application

    fun onCreate()

    fun onLowMemory()

    fun onTrimMemory(level: Int)

    fun onTerminate()
}

interface NetworkSensitive {
    var connected: Boolean

    fun showNoConnection()

    fun showNormal()
}
interface IOSensitive{
    val loading:MutableLiveData<Event<Boolean>>
    val message:MutableLiveData<Event<String>>
    val disposables:CompositeDisposable
}

open class IOSensitiveViewModel:ViewModel(),IOSensitive{

    override val disposables: CompositeDisposable = CompositeDisposable()

    override val message: MutableLiveData<Event<String>> = MutableLiveData()

    override val loading: MutableLiveData<Event<Boolean>> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

/**======================================================================================================================================================*/

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


/**
 * 示例Activity
 */
abstract class ComponentInjectedActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}



