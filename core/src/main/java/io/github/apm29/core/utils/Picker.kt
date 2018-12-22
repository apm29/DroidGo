package io.github.apm29.core.utils

import android.view.View
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import io.github.apm29.core.R

fun <T> TextView.setupOneOptPicker(
    list: ArrayList<Pair<T, String>>,
    defaultSelection: Int = -1,
    showRange: IntRange = 0 until list.size,
    selectedOp: ((T, String, View) -> Unit)? = null
) {
    val dataList = arrayListOf<Pair<T, String>>()
    list.forEachIndexed { index, pair ->
        if (index in showRange) {
            dataList.add(pair)
        }
    }
    val pickerViewOption = OptionsPickerBuilder(context, OnOptionsSelectListener { options1, _, _, _ ->
        val pair = (dataList[options1])
        this.text = pair.second
        this.setTag(R.id.TAG_PICKER_CODE, pair.first)
        selectedOp?.invoke(pair.first, pair.second, this)
    }).build<String>()

    if (defaultSelection >= 0) {
        pickerViewOption.setSelectOptions(defaultSelection)
    }
    val stringList = arrayListOf<String>()
    list.forEachIndexed { index, pair ->
        if (index in showRange) {
            stringList.add(pair.second)
        }
    }
    pickerViewOption.setPicker(stringList)
    isEnabled = true
    this.setOnClickListener {
        it.hideSoftInput()
        pickerViewOption.show()
    }
}

fun TextView.getPickerTagInt(): Int? {
    return getTag(R.id.TAG_PICKER_CODE) as? Int
}

fun TextView.setTextByIndex(index: Int?, list: ArrayList<Pair<Int, String>>) {
    val pair = list.firstOrNull { it.first == index }
    if (pair != null) {
        this.text = pair.second
        this.setTag(R.id.TAG_PICKER_CODE, pair.first)
    } else {
        this.text = context.getString(R.string.text_unselected)
    }
}
