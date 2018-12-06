package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import io.github.apm29.core.arch.BaseActivity
import io.github.apm29.driodgo.R

class CardDetailActivity:BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_card_layout)

    }
}