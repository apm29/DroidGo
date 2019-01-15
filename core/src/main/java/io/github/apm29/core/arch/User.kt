package io.github.apm29.core.arch

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.apm29.core.utils.SP_KEY_USER_MOBILE
import io.github.apm29.core.utils.SP_KEY_USER_TOKEN

object UserManager {

    init {
        checkInit()
    }

    interface IUserInfo

    private lateinit var sharedPreferences: SharedPreferences

    const val ACTION_USER_STATE_CHANGE: String = "ACTION_USER_STATE_CHANGE"

    private fun checkInit() {
        if (!UserManager::sharedPreferences.isInitialized) {
            val coreComponent = DroidGoApp.application.coreComponent()
            sharedPreferences = coreComponent.shared()
        }
    }

    var userInfo: IUserInfo? = null

    fun isLogin(): Boolean {
        checkInit()
        val token = sharedPreferences.getString(SP_KEY_USER_TOKEN, null)
        return !token.isNullOrEmpty()
    }

    fun login(token: String?, context: Context, userInfo: IUserInfo? = null) {
        checkInit()
        if (sharedPreferences.edit()
                .putString(SP_KEY_USER_TOKEN, token)
                .commit()
        ) {
            notifyUserStateChange(context)
        }
        this.userInfo = userInfo
    }


    fun saveUserMobile(mobile: String?): Boolean {
        checkInit()
        return sharedPreferences.edit()
            .putString(SP_KEY_USER_MOBILE, mobile)
            .commit()
    }

    fun getUserMobile(mask: Boolean = false): String? {
        checkInit()
        sharedPreferences.getString(SP_KEY_USER_MOBILE, null).apply {
            return if (mask && null != this && length > 7) {
                this.replaceRange(4..7, "****")
            } else {
                this
            }
        }
    }

    fun logout(context: Context) {
        checkInit()
        if (sharedPreferences.edit()
                .putString(SP_KEY_USER_TOKEN, null)
                .putString(SP_KEY_USER_MOBILE, null)
                .commit()
        ) {
            notifyUserStateChange(context)
        }
        this.userInfo = null
    }

    fun notifyUserStateChange(context: Context) {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(ACTION_USER_STATE_CHANGE))
    }

    fun getUserToken(): String {
        checkInit()
        return sharedPreferences.getString(SP_KEY_USER_TOKEN, null) ?: ""
    }

}