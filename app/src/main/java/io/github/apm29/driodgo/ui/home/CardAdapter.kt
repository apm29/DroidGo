package io.github.apm29.driodgo.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.github.apm29.core.utils.GlideApp
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.model.artifact.bean.CardListItem

class CardAdapter(
    val context: Context,
    private val cardList: List<CardListItem>
) :
    RecyclerView.Adapter<CardStackFragment.VH>() {

    private val displayMetrics = context.resources.displayMetrics
    private val density = displayMetrics.density
    private val widthPixels = displayMetrics.widthPixels
    private val imageWidth = ((widthPixels * 1f - 48 * density) / 2).toInt()
    private val imageHeight = (imageWidth * 16f / 9f).toInt()
    private val layoutInflater = LayoutInflater.from(context)

    private var _cardFilter: MutableMap<String, (CardListItem) -> Boolean> = mutableMapOf()

    fun addCardFilter(tag: String, filter: (CardListItem) -> Boolean) {
        _cardFilter[tag] = filter
    }

    fun removeCardFilter(tag: String): Boolean {
        return _cardFilter.remove(tag)!=null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackFragment.VH {
        val view = layoutInflater.inflate(R.layout.item_card_layout, parent, false)
        return CardStackFragment.VH(view)
    }

    override fun getItemCount(): Int {
        return cardList.filter { card ->
            defaultFilter(card)
        }.filter { card ->
            _cardFilter.all {
                it.value(card)
            }
        }.size
    }

    override fun onBindViewHolder(holder: CardStackFragment.VH, position: Int) {
        val cardListItem = cardList.filter { card ->
            defaultFilter(card)
        }.filter { card ->
            _cardFilter.all {
                it.value(card)
            }
        }[holder.adapterPosition]

        GlideApp.with(holder.itemView.context)
            .load(cardListItem.large_image?.schinese)
            .override(imageWidth, imageHeight)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.largeImage)
    }

    private fun defaultFilter(card: CardListItem) =
        card.card_type != "Passive Ability" && card.card_type != "Ability"

}