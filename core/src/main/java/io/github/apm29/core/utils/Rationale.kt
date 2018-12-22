package io.github.apm29.core.utils

import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.Rationale
import com.yanzhenjie.permission.RequestExecutor
import io.github.apm29.core.R

class Rationale : Rationale<List<String>> {
    override fun showRationale(context: Context, permissions: List<String>, executor: RequestExecutor) {
        val permissionNames = Permission.transformText(context, permissions)
        val message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames))

        AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(R.string.title_permission_dialog)
            .setMessage(message)
            .setPositiveButton(R.string.resume) { _, _ -> executor.execute() }
            .setNegativeButton(R.string.cancel) { _, _ -> executor.cancel() }
            .show()
    }

}