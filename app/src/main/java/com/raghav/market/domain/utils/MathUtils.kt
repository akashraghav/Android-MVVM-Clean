package com.raghav.market.domain.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.toPrecision(precision: Int = 2): Double {
    return BigDecimal(this).setScale(precision, RoundingMode.DOWN).toDouble()
}

fun Float.toPrecision(precision: Int = 2): Float {
    return BigDecimal(this.toDouble()).setScale(precision, RoundingMode.DOWN).toFloat()
}

