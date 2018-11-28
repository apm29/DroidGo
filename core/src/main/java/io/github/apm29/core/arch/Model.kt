package io.github.apm29.core.arch

import android.content.Context
import androidx.room.*
import com.google.gson.annotations.SerializedName
import io.github.apm29.core.BuildConfig
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


data class LoginModel(
    val mobile: String?,
    val sms: String?
)

data class LoginResult(
    @SerializedName("access_token") var accessToken: String?
)

data class ResponseBase<T>(
    val code: Int?,
    val msg: String?,
    val data: T?
)

data class Event<T>(
    val data: T
){
    var consumed = false

    fun getDataIfNotConsumed(callback:(T)->Unit){
        if (!consumed){
            callback(data)
            consumed = true
        }
    }
}

data class RequestBase<T>(
    val versionCode: Int = BuildConfig.VERSION_CODE,
    @SerializedName("access_token") val accessToken: String? = "",
    @SerializedName("biz_content") val content: T
) {
    companion object {
        fun <T> default(content: T): RequestBase<T> {
            return RequestBase(
                content = content
            )
        }
    }
}


interface Api {
    @POST("/v1/user/login")
    fun login(
        @Body loginModel: RequestBase<LoginModel>
    ): Single<ResponseBase<LoginResult>>


    @GET("/oauth/authorize")
    fun authDribbble(
        @Query("redirectUri", encoded = true) url: String = callbackUrl
    ): Single<Any>
}

//room
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "user_id",typeAffinity = ColumnInfo.INTEGER)
    val userId: Long
)

@Dao
interface  UserDao{

    @androidx.room.Query("Select * From user_table where id = :id")
    fun getUserById(id: Long):Single<User>

    @androidx.room.Query("Select * From user_table where id = :userId")
    fun getUserByUserId(userId: Long):Single<User>
}
@Database(entities = [User::class],version = 1,exportSchema = true)
abstract class UserLocalDataBase: RoomDatabase(){

    abstract fun getUserDao():UserDao

    companion object {
        private const val DATABASE_NAME = "droid-db"

        // For Singleton instantiation
        @Volatile private var instance: UserLocalDataBase? = null

        fun getInstance(context: Context): UserLocalDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): UserLocalDataBase {
            return Room.databaseBuilder(
                context, UserLocalDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}