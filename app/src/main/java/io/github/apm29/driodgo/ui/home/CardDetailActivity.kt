package io.github.apm29.driodgo.ui.home

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionSet
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import io.github.apm29.core.arch.BaseActivity
import io.github.apm29.core.utils.glide.GlideApp
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.di.ArtifactApiModule
import io.github.apm29.driodgo.di.CardDetailModule
import io.github.apm29.driodgo.di.DaggerCardDetailComponent
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.ui.transitions.TextFlow
import io.github.apm29.driodgo.ui.widget.ElasticDragDismissFrameLayout
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
            initCardContent(it)
        })
        if (card != null) {
            initCardContent(card)
        } else {
            loadCardDetail(cardId)
        }

        window.sharedElementEnterTransition = TransitionSet()
            .addTransition(TextFlow())
            .addTransition(ChangeBounds())
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .setDuration(3000)
        window.sharedElementReturnTransition = TransitionSet()
            .addTransition(TextFlow())
            .addTransition(ChangeBounds())
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .setDuration(3000)
    }

    private fun initCardContent(card: CardListItem) {
        GlideApp.with(this)
            .load(card.large_image?.schinese)
            .into(cardLargeImage)
        textHeroDescription.text = HtmlCompat.fromHtml(
            card.card_text?.schinese ?: "--",
            HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
        )
    }

    private val systemChromeFade: ElasticDragDismissFrameLayout.SystemChromeFader by lazy {
        ElasticDragDismissFrameLayout.SystemChromeFader(this)

    }

    override fun onResume() {
        super.onResume()
        draggableFrame.addListener(systemChromeFade)
    }

    override fun onPause() {
        super.onPause()
        draggableFrame.removeListener(systemChromeFade)
    }

    private fun loadCardDetail(cardId: Int) {
        cardDetailViewModel.getCardDetail(cardId)
    }
}