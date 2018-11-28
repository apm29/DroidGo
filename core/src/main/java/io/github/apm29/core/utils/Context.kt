package io.github.apm29.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

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