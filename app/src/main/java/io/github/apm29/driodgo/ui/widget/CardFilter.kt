package io.github.apm29.driodgo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.LinearLayout
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.model.artifact.CardType
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.ui.home.FilterTag
import kotlinx.android.synthetic.main.view_card_filter_layout.view.*

class CardFilter(
    ctx: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : LinearLayout(
    ctx,
    attrs,
    defStyleAttr
) {

    private var cardFilter: MutableMap<FilterTag, (CardListItem) -> Boolean> = mutableMapOf()

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    private val listenerColor = CompoundButton.OnCheckedChangeListener { view, checked ->
        when (view) {
            checkerBlack -> {
                colorSelection = if (checked) {
                    colorSelection or black
                } else {
                    colorSelection and black.inv()
                }
            }
            checkerBlue -> {
                colorSelection = if (checked) {
                    colorSelection or blue
                } else {
                    colorSelection and blue.inv()
                }
            }
            checkerGreen -> {
                colorSelection = if (checked) {
                    colorSelection or green
                } else {
                    colorSelection and green.inv()
                }
            }
            checkerRed -> {
                colorSelection = if (checked) {
                    colorSelection or red
                } else {
                    colorSelection and red.inv()
                }
            }
            else -> {

            }
        }

        notifyFilterChange()
    }

    private val listenerType = CompoundButton.OnCheckedChangeListener { view, checked ->
        when (view) {
            checkerHero -> {
                typeSelection = if (checked) {
                    typeSelection or hero
                } else {
                    typeSelection and hero.inv()
                }
            }
            checkerSpell -> {
                typeSelection = if (checked) {
                    typeSelection or spell
                } else {
                    typeSelection and spell.inv()
                }
            }
            checkerImprovement -> {
                typeSelection = if (checked) {
                    typeSelection or improvement
                } else {
                    typeSelection and improvement.inv()
                }
            }
            checkerItem -> {
                typeSelection = if (checked) {
                    typeSelection or item
                } else {
                    typeSelection and item.inv()
                }
            }
            checkerCreep -> {
                typeSelection = if (checked) {
                    typeSelection or creep
                } else {
                    typeSelection and creep.inv()
                }
            }
            else -> {

            }
        }

        notifyFilterChange()
    }


    private var colorSelection = 0b1111
    private var typeSelection = 0b11111

    private val black = 0b1000
    private val blue = 0b0100
    private val red = 0b0010
    private val green = 0b0001

    private val hero = 0b10000
    private val spell = 0b01000
    private val improvement = 0b00100
    private val item = 0b00010
    private val creep = 0b00001


    init {
        LayoutInflater.from(ctx).inflate(R.layout.view_card_filter_layout, this, true)

        checkerBlack.setOnCheckedChangeListener(listenerColor)
        checkerBlue.setOnCheckedChangeListener(listenerColor)
        checkerGreen.setOnCheckedChangeListener(listenerColor)
        checkerRed.setOnCheckedChangeListener(listenerColor)

        checkerHero.setOnCheckedChangeListener(listenerType)
        checkerCreep.setOnCheckedChangeListener(listenerType)
        checkerImprovement.setOnCheckedChangeListener(listenerType)
        checkerItem.setOnCheckedChangeListener(listenerType)
        checkerSpell.setOnCheckedChangeListener(listenerType)
    }

    var onFilterChangeCallBack: ((MutableMap<FilterTag, (CardListItem) -> Boolean>) -> Unit)? = null

    private fun notifyFilterChange() {
        val isBlack = colorSelection and black == black
        val isBlue = colorSelection and blue == blue
        val isRed = colorSelection and red == red
        val isGreen = colorSelection and green == green

        val isHero = typeSelection and hero == hero
        val isItem = typeSelection and item == item
        val isImprovement = typeSelection and improvement == improvement
        val isSpell = typeSelection and spell == spell
        val isCreep = typeSelection and creep == creep


        cardFilter[FilterTag.Color] = {
            isBlack == it.is_black || isBlue == it.is_blue
                    || isRed == it.is_red || isGreen == it.is_green || (it.getType() is CardType.Item)
        }

        cardFilter[FilterTag.Type] = {
            if (isHero) {
                it.getType() is CardType.Hero
            } else {
                isHero
            } || if (isItem) {
                it.getType() is CardType.Item
            } else {
                isItem
            } || if (isImprovement) {
                it.getType() is CardType.Improvement
            } else {
                isImprovement
            } || if (isSpell) {
                it.getType() is CardType.Spell
            } else {
                isSpell
            } || if (isCreep) {
                it.getType() is CardType.Creep
            } else {
                isCreep
            }
        }

        onFilterChangeCallBack?.invoke(cardFilter)
    }


}