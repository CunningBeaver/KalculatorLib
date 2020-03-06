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


fun main(args: Array<String>): Unit =
    when (args.size) {
        0 -> {
            while (true) {
                print("Input: ")
                val expression = readLine() ?: ""
                if (expression == "exit")
                    break
                try {
                    println(calculate(expression))
                } catch (ex: KalculatorException) {
                    println(ex.message)
                }
            }
        }
        1 -> println(calculate(args.first()))
        else -> println(calculate(args.joinToString(" ")))
    }
