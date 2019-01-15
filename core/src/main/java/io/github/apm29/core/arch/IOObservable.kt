package io.github.apm29.core.arch

import android.content.DialogInterface
import android.view.View

interface IOObservable {
    val parentIOObservable: IOObservable?
    val ioSensitive: IOSensitive
    val viewLoading: View
    val viewNormal: View
    val viewErrorHint: DialogInterface
    val delegateLoadState:Boolean
}