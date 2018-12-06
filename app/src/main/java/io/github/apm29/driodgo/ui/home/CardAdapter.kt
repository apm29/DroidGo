package io.github.apm29.driodgo.ui.home

import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.apm29.core.utils.glide.GlideApp
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.bean.CardReference
import io.github.apm29.driodgo.model.artifact.repository.CardDiffer
import java.util.*

class CardAdapter(
    val context: Context,
    private val cardList: MutableList<CardListItem>
) :
    RecyclerView.Adapter<CardStackFragment.VH>()
    , ListPreloader.PreloadModelProvider<CardListItem> {


    override fun getPreloadItems(position: Int): MutableList<CardListItem> {
        return Collections.singletonList(cardList[position])
    }

    override fun getPreloadRequestBuilder(item: CardListItem): RequestBuilder<Drawable>? {
        return GlideApp.with(context)
            .load(item.large_image?.schinese)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)

    }

    sealed class ItemPayLoad {
        object Expand : ItemPayLoad()
        object ExpandALL : ItemPayLoad()
    }

    private val layoutInflater = LayoutInflater.from(context)

    private var _cardFilter: MutableMap<FilterTag, (CardListItem) -> Boolean> = mutableMapOf()

    fun setCard(data: List<CardListItem>) {
        val cardDiff = DiffUtil.calculateDiff(
            CardDiffer(
                cardList, data
            )
        )
        cardList.clear()
        cardList.addAll(data)
        cardDiff.dispatchUpdatesTo(this)
    }

    fun addCardFilter(tag: FilterTag, filter: (CardListItem) -> Boolean) {
        val oldItem = getFilteredData()
        _cardFilter[tag] = filter
        val newItem = getFilteredData()
        val cardDiff = DiffUtil.calculateDiff(
            CardDiffer(
                oldItem, newItem
            )
        )
        cardDiff.dispatchUpdatesTo(this)
    }

    fun removeCardFilter(tag: FilterTag): Boolean {
        val success = _cardFilter.remove(tag) != null
        notifyDataSetChanged()
        return success
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackFragment.VH {
        val view = layoutInflater.inflate(R.layout.item_card_layout, parent, false)
        return CardStackFragment.VH(view)
    }

    override fun getItemCount(): Int {
        return getFilteredData().size
    }

    private fun getFilteredData(): List<CardListItem> {
        return cardList.filter { card ->
            defaultFilter(card)
        }.filter { card ->
            _cardFilter.all {
                it.value(card)
            }
        }
    }

    override fun onBindViewHolder(holder: CardStackFragment.VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val expand = payloads.contains(ItemPayLoad.Expand)
            val expandAll = payloads.contains(ItemPayLoad.ExpandALL)
            val cardListItem = getFilteredData()[position]
            if (expand) {
                holder.grpCollapsed.visibility = if (!cardListItem.isExpand) View.VISIBLE else View.GONE
                holder.grpExpand.visibility = if (cardListItem.isExpand) View.VISIBLE else View.GONE
                holder.cardStack.setCardBackgroundColor(cardListItem.getColor(context))
            }
            if (expandAll){
                holder.grpCollapsed.visibility = if (!cardListItem.isExpand) View.VISIBLE else View.GONE
                holder.grpExpand.visibility = if (cardListItem.isExpand) View.VISIBLE else View.GONE
                holder.cardStack.setCardBackgroundColor(cardListItem.getColor(context))
                holder.actionExpand.rotation = if (cardListItem.isExpand) 180f else 0f
            }
        }
    }

    override fun onBindViewHolder(holder: CardStackFragment.VH, position: Int) {
        val filteredData = getFilteredData()
        val cardListItem = filteredData[position]


        with(holder) {
            actionExpand.rotation = if (cardListItem.isExpand) 180f else 0f
            cardStack.setCardBackgroundColor(cardListItem.getColor(context))

            grpCollapsed.visibility = if (!cardListItem.isExpand) View.VISIBLE else View.GONE
            grpExpand.visibility = if (cardListItem.isExpand) View.VISIBLE else View.GONE

            GlideApp.with(itemView.context)
                .load(cardListItem.large_image?.schinese)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(largeImage)


            textName.text = cardListItem.card_name?.schinese
            textAbility.text = HtmlCompat.fromHtml(
                cardListItem.card_text?.schinese ?: "",
                HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
            )

            imageIncludes.isNestedScrollingEnabled = false
            imageIncludes.isEnabled = false
            if (cardListItem.references?.isNotEmpty() == true) {
                val id = cardListItem.references
                imageIncludes.layoutManager =
                        object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true) {
                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }
                        }
                imageIncludes.adapter = SkillAdapter(id)
            } else {
                imageIncludes.adapter = null
            }

            GlideApp.with(itemView.context)
                .load(cardListItem.mini_image?.default)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(collapsedImage)

            textSmallName.text = cardListItem.card_name?.schinese

            actionExpand.setOnClickListener {
                cardListItem.isExpand = !cardListItem.isExpand
                val animator = ValueAnimator().apply {
                    setFloatValues(1f)
                    duration = 400
                }
                val startDegree = if (cardListItem.isExpand) 0f else -180f
                animator.addUpdateListener {
                    val fraction = it.animatedFraction
                    actionExpand.rotation = startDegree + 180f * fraction
                }
                animator.start()

                notifyItemChanged(position, ItemPayLoad.Expand)
            }
            largeImage.setOnClickListener {
                if (context is Activity) {
                    context.setExitSharedElementCallback(object : SharedElementCallback() {
                        override fun onSharedElementStart(
                            sharedElementNames: List<String>,
                            sharedElements: List<View>,
                            sharedElementSnapshots: List<View>
                        ) {
                            context.setExitSharedElementCallback(null)
                        }
                    })

                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        context,
                        Pair(largeImage, "largeImage")
                    )
                    val intent = Intent(context, CardDetailActivity::class.java)
                    context.startActivity(intent, options.toBundle())

                    notifyItemChanged(position, 1)
                }
            }
        }
    }


    private fun defaultFilter(card: CardListItem) =
        !card.is_included

    private var allExpanded = true

    fun expandAll() {
        allExpanded = !allExpanded

        cardList.forEach {
            it.isExpand = allExpanded
        }

        notifyItemRangeChanged(
            0,
            getFilteredData().size,
            ItemPayLoad.ExpandALL
        )
    }

    /**
     * 包含的skill
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