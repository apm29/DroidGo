package io.github.apm29.core.arch

import android.app.Application

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