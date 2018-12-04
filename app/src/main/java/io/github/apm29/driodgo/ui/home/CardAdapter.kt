package io.github.apm29.driodgo.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.github.apm29.core.utils.GlideApp
import io.github.apm29.core.utils.autoThreadSwitch
import io.github.apm29.core.utils.subscribeAuto
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.model.artifact.CardType
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.bean.CardReference
import io.github.apm29.driodgo.vm.HomeViewModel

class CardAdapter(
    context: Context,
    private val cardList: MutableList<CardListItem>
) :
    RecyclerView.Adapter<CardStackFragment.VH>() {


    private val displayMetrics = context.resources.displayMetrics
    private val density = displayMetrics.density
    private val widthPixels = displayMetrics.widthPixels
    private val imageWidth = ((widthPixels * 1f - 48 * density) / 2).toInt()
    private val imageHeight = (imageWidth * 16f / 9f).toInt()
    private val layoutInflater = LayoutInflater.from(context)

    private var _cardFilter: MutableMap<FilterTag, (CardListItem) -> Boolean> = mutableMapOf()

    fun setCard(data: List<CardListItem>) {
        val size = cardList.size
        cardList.clear()
        notifyItemRangeRemoved(0, size)
        cardList.addAll(data)
        notifyItemRangeInserted(0, data.size)
    }

    fun addCardFilter(tag: FilterTag, filter: (CardListItem) -> Boolean) {
        _cardFilter[tag] = filter
    }

    fun removeCardFilter(tag: FilterTag): Boolean {
        return _cardFilter.remove(tag) != null
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

        with(holder) {
            largeImage?.let {
                GlideApp.with(itemView.context)
                    .load(cardListItem.large_image?.schinese)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(largeImage)
            }

            textName?.let {
                textName.text = cardListItem.card_name?.schinese
            }
            textAbility?.let {
                textAbility.text = HtmlCompat.fromHtml(
                    cardListItem.card_text?.schinese ?: "-",
                    HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
                )
            }

            imageIncludes?.let {
                imageIncludes.isNestedScrollingEnabled = false
                imageIncludes.isEnabled = false
                if (cardListItem.references?.isNotEmpty() == true) {
                    val id = cardListItem.references
                    imageIncludes.layoutManager = object :LinearLayoutManager(it.context, LinearLayoutManager.HORIZONTAL, true){
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }
                    }
                    imageIncludes.adapter = SkillAdapter(id)
                } else {
                    imageIncludes.adapter = null
                }
            }
        }
    }

    private fun defaultFilter(card: CardListItem) =
        !card.is_included

    /**
     * 包含的能力
     */
    class SkillVH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
    }

    class SkillAdapter(
        val id: List<CardReference>
    ) : RecyclerView.Adapter<SkillVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_include_skill_layout, parent, false)
            return SkillVH(view)
        }

        override fun getItemCount(): Int {
            return id.size
        }

        override fun onBindViewHolder(holder: SkillVH, position: Int) {
            with(holder) {
                GlideApp.with(itemView.context)
                    .load(id[holder.adapterPosition].image)
                    .into(image)
            }
        }

    }


}