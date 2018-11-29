package io.github.apm29.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment


/**
 * Activity/Context/Fragment 扩展方法
 */

var toast: Toast? = null

@SuppressLint("ShowToast")
fun Context.showToast(msg: String?) {
    msg?.let {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        }
        toast?.setText(msg)
        toast?.show()
    }
}

@SuppressLint("ShowToast")
fun Fragment.showToast(msg: String?) {
    requireContext().showToast(msg)
}