package io.github.apm29.core.arch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.apm29.core.R
import io.github.apm29.core.arch.dagger.CoreComponent
import io.github.apm29.core.utils.*
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity(), BackPressSensitive, ConnectivitySensitive, IOObservable {

    protected val mHandler: Handler = Handler(Looper.getMainLooper())

    override var handleBackPress: HandleBackPress? = null

    private fun droidApp(): DroidGoApp {
        return application as DroidGoApp
    }

    val mCoreComponent: CoreComponent by lazy {
        droidApp().mCoreComponent
    }

    override val parentIOObservable: IOObservable? by lazy {
        null
    }
    override val ioSensitive: IOSensitive by lazy {
        ViewModelProviders.of(this).get(BaseActivityViewModel::class.java)
    }
    override val viewNormal: FrameLayout by lazy {
        findViewById<FrameLayout>(R.id.normalStubActivity)
    }
    override val viewLoading: View by lazy {
        findViewById<View>(R.id.loadingStubActivity)
    }
    override val viewErrorHint: AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_dialog_title))
            .create()
    }

    override val delegateLoadState: Boolean = true

    open var showLoadingView: Boolean = true
    open var showErrorDialog: Boolean = true
    open var showNoConnectionView: Boolean = true

    open var isUserStateChangeSensitive: Boolean = false

    private val loadingTextView: TextView by lazy {
        findViewById<TextView>(R.id.textLoading)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.loading)
    }

    private val stubNoConn: View by lazy { findViewById<View>(R.id.noConnectionStubActivity) }


    private val userStateChangerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onUserStateChange()
        }

    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.base_activity_layout)

        observeIO()
        observeConnectivity()

        if (isUserStateChangeSensitive) {
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(userStateChangerReceiver, IntentFilter(UserManager.ACTION_USER_STATE_CHANGE))
        }
    }

    @CallSuper
    protected open fun onUserStateChange() {
        Timber.d("userStateChange, userLogin:${UserManager.isLogin()},userInfo:${UserManager.userInfo}")
    }

    private fun observeConnectivity() {
        if (showNoConnectionView) {
            checkConnectivity()
        }
    }


    private val loadingObserver = Observer<Event<Boolean>> {
        it.getDataIfNotConsumed { loading ->
            if (loading) {
                showLoadingView(ioSensitive.loadingMessage)
            } else {
                hideLoadingView()
            }
        }

    }
    private val errorObserver = Observer<Event<String>> {
        it.getDataIfNotConsumed { message ->
            viewErrorHint.setMessage(message)
            viewErrorHint.show()
        }

    }

    private fun observeIO() {
        if (showLoadingView) {
            ioSensitive.loading.removeObserver(loadingObserver)
            ioSensitive.loading.observe(this, loadingObserver)
        }
        if (showErrorDialog) {
            ioSensitive.message.removeObserver(errorObserver)
            ioSensitive.message.observe(this, errorObserver)
        }
    }

    override fun setContentView(layoutResID: Int) {
        //viewNormal.removeAllViews()
        val view = layoutInflater.inflate(layoutResID, viewNormal, true)
        view.findViewWithTag<View?>(TAG_BACK_ARROW)?.setFilteredOnClickListener {
            finish()
        }
    }

    override var connected: Boolean = true

    override fun onDisconnect() {
        showNoConnection()
    }

    override fun onConnected() {
        showConnected()
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

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
        mHandler.removeCallbacksAndMessages(null)
        ioSensitive.disposables.dispose()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(userStateChangerReceiver)
    }

    override fun onBackPressed() {
        if (handleBackPress?.handleBackPress() != true) {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return handleBackPress?.handleBackPress() ?: false || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    protected open fun showLoadingView(loadingMessage: String?) {
        mHandler.post {
            stubNoConn.visibility = View.GONE
            viewLoading.visibility = View.VISIBLE
            viewNormal.visibility = View.VISIBLE

            if (loadingMessage != null) {
                loadingTextView.text = loadingMessage
            } else {
                loadingTextView.text = getString(R.string.loading_text)
            }
        }

    }

    protected open fun hideLoadingView(delay: Long = 800) {
        mHandler.postDelayed({
            viewLoading.visibility = View.GONE
            stubNoConn.visibility = View.GONE
            viewNormal.visibility = View.VISIBLE
        }, delay)
    }

    protected open fun showNoConnection() {
        mHandler.post {
            stubNoConn.visibility = View.VISIBLE
            viewNormal.visibility = View.GONE
        }
    }

    protected open fun showConnected() {
        mHandler.post {
            stubNoConn.visibility = View.GONE
            viewNormal.visibility = View.VISIBLE
        }
    }

    protected fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    protected fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}