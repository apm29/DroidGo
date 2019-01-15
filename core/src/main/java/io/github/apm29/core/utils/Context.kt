package io.github.apm29.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.yanzhenjie.permission.Permission
import io.github.apm29.core.R


/**
 * Activity/Context/Fragment 扩展方法
 */

var toast: Toast? = null

@SuppressLint("ShowToast")
fun Context.showToast(msg: String?) {
    msg?.let {
        if (toast == null) {
            try {
                toast = Toast(this)
                toast?.view = LayoutInflater.from(this).inflate(R.layout.toast_layout, null, false)
            } catch (e: Exception) {
                toast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
            }
        }
        val textView = toast?.view?.findViewById<TextView>(R.id.textToast)
        if (textView != null) {
            textView.text = msg
        } else {
            toast?.setText(msg)
        }
        toast?.show()
    }
}

@SuppressLint("ShowToast")
fun Fragment.showToast(msg: String?) {
    requireContext().showToast(msg)
}

fun Context.colorOf(@ColorRes colorRes: Int): Int {
    return ActivityCompat.getColor(this, colorRes)
}

fun Fragment.colorOf(@ColorRes colorRes: Int): Int {
    return requireContext().colorOf(colorRes)
}

fun Context.requestAndPermission(
    permissions: Array<String>,
    onGranted: (List<String>) -> Unit,
    onCancel: ((DialogInterface, Int) -> Unit)? = null
) {
    com.yanzhenjie.permission.AndPermission.with(this)
        .runtime()
        .permission(permissions)
        .onGranted {
            onGranted(it)
        }.onDenied {
            val permissionNames = Permission.transformText(this, it)
            val message = getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames))

            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("请求必要权限:")
                .setMessage(message)
                .setPositiveButton(R.string.resume) { _, _ ->
                    if (com.yanzhenjie.permission.AndPermission.hasAlwaysDeniedPermission(this, it)) {
                        com.yanzhenjie.permission.AndPermission.with(this)
                            .runtime()
                            .setting()
                            .onComeback {
                                // 用户从设置回来了。
                                requestAndPermission(permissions, onGranted, onCancel)
                            }
                            .start()
                    } else {
                        requestAndPermission(permissions, onGranted, onCancel)
                    }
                }
                .setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, which: Int ->
                    onCancel?.invoke(dialogInterface, which)
                }
                .show()
        }.rationale(
            Rationale()
        ).start()
}

fun Fragment.requestAndPermission(
    permissions: Array<String>,
    onGranted: (List<String>) -> Unit,
    onCancel: ((DialogInterface, Int) -> Unit)? = null
) {
    requireContext().requestAndPermission(permissions, onGranted, onCancel)
}

val PROJECTION = arrayOf(
    ContactsContract.RawContacts.CONTACT_ID
)
val PROJECTION_DATA = arrayOf(
    ContactsContract.Data.DATA1,
    ContactsContract.Data.MIMETYPE
)
const val SELECTION = ContactsContract.RawContacts.CONTACT_ID + "= ?"

fun Context.tryReadContacts() {
    val query = contentResolver.query(
        ContactsContract.RawContacts.CONTENT_URI, PROJECTION,
        null, null, null
    )
    query?.let {
        if (query.count <= 0) {
            showToast("没有联系人或者未获得授权")
            throw RuntimeException("没有联系人或者未获得授权")
        }
        if (query.moveToNext()) {
            query.getString(0)
        }
    }
    query?.close()
}

fun Fragment.tryReadContact() {
    requireContext().tryReadContacts()
}