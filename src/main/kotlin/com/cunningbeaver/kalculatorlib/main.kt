package com.cunningbeaver.kalculatorlib

import kotlin.math.truncate

fun calculate(expression: String): Double
        = truncate(
    calculateTokens(
        sortTokens(
            parseToTokens(expression)
        )
    ).value * 10000) / 10000
