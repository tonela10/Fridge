package com.sedilant.cachosfridge.ui

import java.text.NumberFormat
import java.util.Locale

private val euroFormatter = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

fun Int.toEuroString(): String {
    val asEuros = this / 100.0
    return euroFormatter.format(asEuros)
}

