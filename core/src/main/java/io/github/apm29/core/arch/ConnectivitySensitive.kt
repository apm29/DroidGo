package io.github.apm29.core.arch

interface ConnectivitySensitive {
    var connected: Boolean

    fun onDisconnect()

    fun onConnected()
}