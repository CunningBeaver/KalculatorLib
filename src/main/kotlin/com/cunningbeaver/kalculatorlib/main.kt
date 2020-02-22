package com.cunningbeaver.kalculatorlib

import kotlin.math.truncate

/**
 * main function of library
 * @param expression it's a math expression like a `45+45 * (15)`
 * @return result of expression calculation
 */
fun calculate(expression: String): Double =
    expression
        .run(::parseToTokens)
        .run(::sortTokens)
        .run(::calculateTokens)
        .value.times(10000)
        .run(::truncate).div(10000)