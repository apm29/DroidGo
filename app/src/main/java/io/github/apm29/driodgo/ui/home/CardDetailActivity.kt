package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import androidx.lifecycle.Observer
import io.github.apm29.core.arch.BaseActivity
import io.github.apm29.core.utils.glide.GlideApp
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.ArtifactApiModule
import io.github.apm29.driodgo.di.CardDetailModule
import io.github.apm29.driodgo.di.DaggerCardDetailComponent
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.vm.CardDetailViewModel
import kotlinx.android.synthetic.main.activity_card_detail.*
import javax.inject.Inject

class CardDetailActivity : BaseActivity() {

    @Inject
    lateinit var cardDetailViewModel: CardDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)
        val cardId = intent.getIntExtra("cardId", 0)
        val card: CardListItem? = intent.getParcelableExtra("cardDetail") as? CardListItem

        DaggerCardDetailComponent.builder()
            .artifactApiModule(ArtifactApiModule())
            .cardDetailModule(CardDetailModule(this))
            .coreComponent(mCoreComponent)
            .build()
            .inject(this)

        cardDetailViewModel.cardDetailData.observe(this, Observer {
            GlideApp.with(this)
                .load(it.large_image?.schinese)
                .into(cardLargeImage)
        })
        if (card!=null){
            GlideApp.with(this)
                .load(card.large_image?.schinese)
                .into(cardLargeImage)
        }else {
            loadCardDetail(cardId)
        }
    }

    private fun loadCardDetail(cardId: Int) {
        cardDetailViewModel.getCardDetail(cardId)
    }
}