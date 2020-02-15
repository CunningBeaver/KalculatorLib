package com.cunningbeaver.kalculatorlib

import java.util.*


fun calculateTokens(tokensList: Array<Token>): NumberToken {
    val operandStack = Stack<Operand>()

    try {
        for (token in tokensList) {
            when (token) {
                is NumberToken -> {
                    operandStack.push(token)
                }
                is UnaryOperatorToken -> {
                    val num = operandStack.pop()
                    if (num !is NumberToken)
                        throw OrderException("unexpected operand $num")
                    val result = token(num)
                    operandStack.push(result)
                }
                is BinaryOperatorToken -> {
                    val num2 = operandStack.pop()
                    val num1 = operandStack.pop()
                    if (num1 !is NumberToken || num2 !is NumberToken)
                        throw OrderException("$num1 or $num2 is not a number")
                    val result = token(num1, num2)
                    operandStack.push(result)
                }
                is GrouperToken -> {
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    val result = token(operand1, operand2)
                    operandStack.push(result)
                }
                is FunctionToken -> {
                    val numberList = operandStack.pop().toNumberList()
                    val result = token(numberList)
                    operandStack.push(result)
                }
                else -> throw MathException("unexpected token $token")
            }
        }
    } catch (e: EmptyStackException) {
        throw MathException("order of tokens not a postfix")
    }
    if (operandStack.size != 1)
        throw MathException("order of tokens not a a postfix")

    val last = operandStack.last()
    if (last !is NumberToken)
        throw OrderException("unexpected token $last")
    return last
}
