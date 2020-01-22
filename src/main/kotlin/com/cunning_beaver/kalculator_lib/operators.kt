package com.cunning_beaver.kalculator_lib

import kotlin.math.*

// binary operators
private data class BinaryOperatorData(
    val priority: Int,
    val function: (NumberToken, NumberToken)-> NumberToken
)

private val binaryOperatorsData = hashMapOf(
    '+' to BinaryOperatorData(priority = 2) { a, b -> a + b },
    '-' to BinaryOperatorData(priority = 2) { a, b -> a - b },
    '*' to BinaryOperatorData(priority = 3) { a, b -> a * b },
    '/' to BinaryOperatorData(priority = 3) { a, b -> a / b },
    '^' to BinaryOperatorData(priority = 4) { a, b -> NumberToken(a.value.pow(b.value)) }
)

fun getBinaryOperatorPriority(op: Char)
    = binaryOperatorsData[op]?.priority ?: throw MathException("unexpected operator $op")

fun getBinaryOperatorFunction(op: Char)
    = binaryOperatorsData[op]?.function ?: throw MathException("unexpected operator $op")

fun getValidBinaryOperators() = binaryOperatorsData.keys


// unary operators
private val unaryOperatorsFunctions = hashMapOf<Char, ((NumberToken) -> NumberToken)>(
    '-' to { num -> -num }
)

fun getUnaryOperatorFunction(op: Char)
    = unaryOperatorsFunctions[op] ?: throw MathException("unexpected operator $op")

fun getValidUnaryOperators() = unaryOperatorsFunctions.keys


// functions
data class FunctionData(
    val argsLength: Int,                                // can be -1 for vararg
    val function: (Array<NumberToken>) -> NumberToken

)

private val functionTokensData = hashMapOf<String, FunctionData>(
    "log" to FunctionData(argsLength = 2){ (a, b) ->  NumberToken(log(a.value, b.value)) },
    "floor" to FunctionData(argsLength = 1){ (num) -> NumberToken(floor(num.value)) },
    "round" to FunctionData(argsLength = 1){ (num) -> NumberToken(round(num.value)) },
    "ceil" to FunctionData(argsLength = 1){ (num) -> NumberToken(ceil(num.value)) }
)

fun getFunctionOfFunctionToken(fn: String)
    = functionTokensData[fn]?.function ?: throw MathException("unexpected function $fn")

fun getArgsLengthOgFunctions(fn: String)
        = functionTokensData[fn]?.argsLength ?: throw MathException("unexpected function $fn")

