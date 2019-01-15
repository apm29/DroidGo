package io.github.apm29.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.getSystemService
import io.reactivex.Single
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

fun deviceIP(): Single<String> {
    return Single.create { emitter ->
        try {
            val ipAddress = getLocalIpAddress() ?: ""
            emitter.onSuccess(ipAddress)
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}

fun getLocalIpAddress(): String? {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    while (networkInterfaces.hasMoreElements()) {
        val networkInterface = networkInterfaces.nextElement()
        val iNetAddresses = networkInterface.inetAddresses
        while (iNetAddresses.hasMoreElements()) {
            val iNetAddress = iNetAddresses.nextElement()
            if (!iNetAddress.isLoopbackAddress && (iNetAddress is Inet4Address) || (iNetAddress is Inet6Address)) {
                return iNetAddress.hostAddress.toString()
            }
        }
    }
    return null
}


@SuppressLint("MissingPermission")
fun Context.deviceIMEI(): Single<String> {
    val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return Single.create {
        try {
            val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                telephonyManager.deviceId
            }
            it.onSuccess(imei)
        } catch (e: Exception) {
            it.onError(e)
        }
    }
}