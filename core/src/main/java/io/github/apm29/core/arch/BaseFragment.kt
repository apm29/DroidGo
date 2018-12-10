package io.github.apm29.core.arch

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.github.apm29.core.utils.Event
import io.github.apm29.core.R

abstract class BaseFragment : Fragment(), HandleBackPress {

    protected val mHandler: Handler = Handler()
    //处理back点击事件
    override fun handleBackPress(): Boolean {
        return false
    }

    private var listeningIoBySelf = false

    lateinit var io: IOSensitive

    /**
     * 将io事件和back press事件交给当前Activity处理
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BackPressSensitive) {
            context.handleBackPress = this
        }
        if (context is IOObservable) {
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

    private fun stopLoading() {
        mHandler.post {
            stubLoading?.visibility =View.GONE
        }
    }

    private fun showLoading(loadingMessage: String?) {
        mHandler.post {
            stubLoading?.visibility = View.VISIBLE
            if (loadingMessage != null) {
                loadingTextView?.text = loadingMessage
            } else {
                loadingTextView?.text = getString(R.string.loading_text)
            }
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
        super.onCreate(savedInstanceState)
        if (listeningIoBySelf) {
            io.message.removeObserver(errorObserver)
            io.message.observe(this, errorObserver)
            io.loading.removeObserver(loadingObserver)
            io.loading.observe(this, loadingObserver)
        }
    }

    private val stubNormal: ViewStub? by lazy { view?.findViewById<ViewStub>(R.id.normalStub) }
    private val stubLoading: ViewStub? by lazy { view?.findViewById<ViewStub>(R.id.loadingStub) }
    private val loadingTextView: TextView? by lazy {
        view?.findViewById<TextView>(R.id.fragmentLoadingText)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.base_fragment_layout, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            stubLoading?.inflate()
            stubNormal?.layoutResource = layoutRes(savedInstanceState)
            stubNormal?.inflate()
            stubLoading?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun layoutRes(savedInstanceState: Bundle?):Int

}