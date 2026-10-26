package com.ehan.app4

import java.math.BigDecimal
import java.util.Locale

/**
 * Small expression evaluator for the calculator keypad. It follows normal
 * operator precedence and treats the percent key as a postfix / 100.
 */
object CalculatorEngine {
    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null

        return runCatching {
            ExpressionParser(expression).parse().also {
                require(it.isFinite()) { "Hasil tidak valid" }
            }
        }.getOrNull()
    }

    fun formatNumber(value: Double): String {
        if (!value.isFinite()) return "Error"
        if (value == 0.0) return "0"

        val absoluteValue = kotlin.math.abs(value)
        if (absoluteValue >= 1.0e12 || absoluteValue < 1.0e-8) {
            return String.format(Locale.US, "%.6g", value)
        }

        val plain = BigDecimal.valueOf(value)
            .stripTrailingZeros()
            .toPlainString()
        return if (plain.length > 16) {
            String.format(Locale.US, "%.6g", value)
        } else {
            plain
        }
    }
}

private class ExpressionParser(private val source: String) {
    private var position = 0

    fun parse(): Double {
        val result = parseExpression()
        require(position == source.length) { "Ekspresi belum lengkap" }
        return result
    }

    private fun parseExpression(): Double {
        var value = parseTerm()
        while (position < source.length) {
            value = when (source[position]) {
                '+' -> {
                    position++
                    value + parseTerm()
                }
                '-' -> {
                    position++
                    value - parseTerm()
                }
                else -> return value
            }
        }
        return value
    }

    private fun parseTerm(): Double {
        var value = parsePercent()
        while (position < source.length) {
            value = when (source[position]) {
                '*' -> {
                    position++
                    value * parsePercent()
                }
                '/' -> {
                    position++
                    val divisor = parsePercent()
                    require(divisor != 0.0) { "Tidak bisa membagi dengan nol" }
                    value / divisor
                }
                else -> return value
            }
        }
        return value
    }

    private fun parsePercent(): Double {
        var value = parseUnary()
        while (position < source.length && source[position] == '%') {
            position++
            value /= 100.0
        }
        return value
    }

    private fun parseUnary(): Double {
        if (position < source.length) {
            when (source[position]) {
                '+' -> {
                    position++
                    return parseUnary()
                }
                '-' -> {
                    position++
                    return -parseUnary()
                }
            }
        }
        return parseNumber()
    }

    private fun parseNumber(): Double {
        val start = position
        var hasDigit = false
        var hasDecimalPoint = false

        while (position < source.length) {
            when (source[position]) {
                in '0'..'9' -> {
                    hasDigit = true
                    position++
                }
                '.' -> {
                    require(!hasDecimalPoint) { "Angka desimal tidak valid" }
                    hasDecimalPoint = true
                    position++
                }
                else -> break
            }
        }

        require(hasDigit) { "Angka tidak ditemukan" }
        if (position < source.length && source[position] in "eE") {
            position++
            if (position < source.length && source[position] in "+-") {
                position++
            }
            val exponentStart = position
            while (position < source.length && source[position] in '0'..'9') {
                position++
            }
            require(position > exponentStart) { "Eksponen tidak valid" }
        }
        return source.substring(start, position).toDouble()
    }
}