package io.github.apm29.driodgo.model.artifact.repository

import androidx.recyclerview.widget.DiffUtil
import io.github.apm29.driodgo.model.artifact.bean.CardListItem

class CardDiffer(
    private val oldItems:List<CardListItem>,
    private val newItems:List<CardListItem>
):DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].card_id == newItems[newItemPosition].card_id
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].isExpand == newItems[newItemPosition].isExpand
    }
}