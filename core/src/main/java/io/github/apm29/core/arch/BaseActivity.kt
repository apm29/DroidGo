package io.github.apm29.core.arch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewStub
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.apm29.core.R
import io.github.apm29.core.arch.dagger.CoreComponent

abstract class BaseActivity : AppCompatActivity(), BackPressSensitive, ConnectivitySensitive {

    val mHandler:Handler = Handler()

    override var handleBackPress: HandleBackPress? = null

    private fun droidApp(): DroidGoApp {
        return application as DroidGoApp
    }

    val mCoreComponent: CoreComponent by lazy {
        droidApp().mCoreComponent
    }

    val io: IOSensitiveViewModel by lazy {
        ViewModelProviders.of(this).get(BaseActivityViewModel::class.java)
    }

    open var showLoadingView: Boolean = true
    open var showErrorDialog: Boolean = true
    open var showNoConnectionView: Boolean = true

    private val stubNormal: ViewStub by lazy { findViewById<ViewStub>(R.id.normalStub) }
    private val stubLoading: View by lazy { findViewById<View>(R.id.loadingStub) }

    private val loadingTextView: TextView by lazy {
        findViewById<TextView>(R.id.loadingText)
    }

    private val progressBar:ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.loading)
    }

    private val stubNoConn: ViewStub by lazy { findViewById<ViewStub>(R.id.noConnectionStub) }
    private val errorDialog :AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_dialog_title))
            .create()
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.base_content_layout)

        if (showLoadingView) {
            io.loading.observe(this, Observer {
                it.getDataIfNotConsumed { loading ->
                    println("loading = $loading")
                    if (loading) {
                        showLoadingView(io.loadingMessage)
                    } else {
                        showNormalView()
                    }
                }

            })
        }
        if (showErrorDialog) {
            io.message.observe(this, Observer {
                it.getDataIfNotConsumed { message ->
                    errorDialog.setMessage(message)
                    errorDialog.show()
                }

            })
        }
        if (showNoConnectionView) {
            checkConnectivity()
        }
    }

    override fun setContentView(layoutResID: Int) {
        stubNormal.layoutResource = layoutResID
        stubNormal.inflate()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override var connected: Boolean = true

    override fun onDisconnect() {
        showNoConnection()
    }

    override fun onConnected() {
        showNormalView()
    }

    private fun checkConnectivity() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected
        registerReceiver(connectivityReceiver, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        })
    }

    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            connected = !(intent.extras?.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY) ?: false)
            if (connected) {
                onConnected()
            } else {
                onDisconnect()
            }
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    override fun onBackPressed() {
        if (handleBackPress?.handleBackPress() != true) {
            super.onBackPressed()
        }
    }

    protected open fun showLoadingView(loadingMessage: String? = getString(R.string.loading_text)) {
        stubNoConn.visibility = View.GONE
        stubNormal.visibility = View.VISIBLE
        stubLoading.visibility = View.VISIBLE
        loadingMessage?.apply {
            loadingTextView.text = this
        }
    }

    protected open fun showNormalView(delay:Long = 500) {
        stubLoading.visibility = View.GONE
        stubNoConn.visibility = View.GONE
        stubNormal.visibility = View.VISIBLE
    }

    protected open fun showNoConnection() {
        stubLoading.visibility = View.GONE
        stubNoConn.visibility = View.VISIBLE
        stubNormal.visibility = View.GONE
    }
}