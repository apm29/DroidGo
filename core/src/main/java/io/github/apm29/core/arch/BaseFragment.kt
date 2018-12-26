package io.github.apm29.core.arch

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.apm29.core.R
import io.github.apm29.core.arch.dagger.CoreComponent
import io.github.apm29.core.utils.Event
import timber.log.Timber

abstract class BaseFragment : Fragment(), HandleBackPress {

    protected open val mHandler: Handler = Handler()

    protected val coreComponent: CoreComponent by lazy {
        (requireContext().applicationContext as DroidGoApp).mCoreComponent
    }

    //处理back点击事件
    override fun handleBackPress(): Boolean {
        return false
    }

    protected open var listeningIoBySelf = false

    open var isUserStateChangeSensitive: Boolean = false

    lateinit var io: IOSensitive

    private val userStateChangerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mHandler.post {
                onUserStateChange()
            }
        }

    }

    protected open fun onUserStateChange() {

    }

    /**
     * 将io事件和back press事件交给当前Activity处理
     */
    @CallSuper
    override fun onAttach(context: Context?) {
        Timber.d("BaseFragment.onAttach")
        super.onAttach(context)
        if (context is BackPressSensitive) {
            context.handleBackPress = this
        }
        if (context is IOObservable && !listeningIoBySelf) {
            io = context.ioSensitive
        } else {
            io = IOSensitiveViewModel()
            //需要自己监听io事件
            listeningIoBySelf = true
        }
    }

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error_dialog_title))
            .create()
    }

    private val loadingObserver = Observer<Event<Boolean>> {
        it.getDataIfNotConsumed { loading ->
            if (loading) {
                showLoading(io.loadingMessage)
            } else {
                stopLoading()
            }
        }

    }

    private fun stopLoading(delay: Long = 800) {
        val stubLoading: View? by lazy { view?.findViewById<View>(R.id.loadingStub) }
        mHandler.postDelayed({
            stubLoading?.visibility = View.GONE
            onLoadingStop()
        }, delay)
    }

    protected open fun onLoadingStop(){

    }
    protected open fun onLoadingStart(){

    }

    private fun showLoading(loadingMessage: String?) {
        val stubLoading: View? by lazy { view?.findViewById<View>(R.id.loadingStub) }
        val loadingTextView: TextView? by lazy {
            view?.findViewById<TextView>(R.id.fragmentLoadingText)
        }
        mHandler.post {
            stubLoading?.visibility = View.VISIBLE
            if (loadingMessage != null) {
                loadingTextView?.text = loadingMessage
            } else {
                loadingTextView?.text = getString(R.string.loading_text)
            }
            onLoadingStart()
        }
    }

    private val errorObserver = Observer<Event<String>> {
        it.getDataIfNotConsumed { message ->
            errorDialog.setMessage(message)
            errorDialog.show()
        }

    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("BaseFragment.onCreate")
        super.onCreate(savedInstanceState)
        if (listeningIoBySelf) {
            io.message.removeObserver(errorObserver)
            io.message.observe(this, errorObserver)
            io.loading.removeObserver(loadingObserver)
            io.loading.observe(this, loadingObserver)
        }

        if (isUserStateChangeSensitive) {
            LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(userStateChangerReceiver, IntentFilter(UserManager.ACTION_USER_STATE_CHANGE))
        }
    }


    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("BaseFragment.onCreateView")
        val view = inflater.inflate(R.layout.base_fragment_layout, container, false)
        val stubNormal: FrameLayout? = view.findViewById(R.id.normalStub)
        stubNormal?.removeAllViews()
        inflater.inflate(layoutRes(savedInstanceState), stubNormal, true)
        return view
    }

    abstract fun layoutRes(savedInstanceState: Bundle?): Int


    override fun onDestroy() {
        Timber.d("BaseFragment.onDestroy")
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        if(listeningIoBySelf) {
            io.disposables.dispose()
        }
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(userStateChangerReceiver)
    }
}