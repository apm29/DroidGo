package io.github.apm29.core.arch

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

open class BaseFragment:Fragment() ,HandleBackPress{

    //处理back点击事件
    override fun handleBackPress(): Boolean {
        return false
    }

    lateinit var io: IOSensitive

    /**
     * 将io事件和back press事件交给当前Activity处理
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BackPressSensitive){
            context.handleBackPress = this
        }
        if (context is IOObservable){
            io = context.ioSensitive
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


}