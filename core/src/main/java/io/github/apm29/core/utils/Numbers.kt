package io.github.apm29.core.utils

fun Number?.addUnit(unit: String = "元"): String {
    if (null == this) {
        return "--$unit"
    }
    return "${this}$unit"
}

fun Double?.toCentInteger(): Int {
    return if (null == this) {
        0
    } else {
        (String.format("%.2f", this).toDouble() * 100f).toInt()
    }
}

fun Double?.toDigitString(digits: Int = 2): String {
    return if (null == this) {
        "0.00"
    } else {
        String.format("%.${digits}f", this)
    }
}