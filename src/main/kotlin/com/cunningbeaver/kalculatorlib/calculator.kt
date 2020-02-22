package com.cunningbeaver.kalculatorlib

import java.util.*

/**
 * calculation part of reverse polish notation
 * uses stack to calculate a result
 * @param tokensList Array of [tokens][Token] received from [sortTokens] function
 * @return result [number token][NumberToken]
 */
fun calculateTokens(tokensList: Array<Token>): NumberToken {
    val operandStack = Stack<Operand>()

    try {
        for (token in tokensList) {
            // iterates over tokens, puts them on the stack or converts them according to the
            // last elements of the stack
            when (token) {
                is NumberToken -> {
                    // just pushes a token to stack
                    operandStack.push(token)
                }

                is UnaryOperatorToken -> {
                    // pops last token from stack and invokes the function of current token
                    // then pushes the result to stack
                    val num = operandStack.pop()
                    if (num !is NumberToken)
                        throw OrderException("unexpected operand $num")
                    val result = token(num)
                    operandStack.push(result)
                }

                is BinaryOperatorToken -> {
                    // pops two last tokens and invokes the function of current BinaryOperatorToken with tokens from stack
                    // then pushes the result to stack
                    val num2 = operandStack.pop()
                    val num1 = operandStack.pop()
                    if (num1 !is NumberToken || num2 !is NumberToken)
                        throw OrderException("$num1 or $num2 is not a number")
                    val result = token(num1, num2)
                    operandStack.push(result)
                }

                is GrouperToken -> {
                    // groups a two last tokens for function and pushes to stack
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    val result = token(operand1, operand2)
                    operandStack.push(result)
                }

                is FunctionToken -> {
                    // converts a last token in stack to NumberList and invokes the function of current FunctionToken
                    // then pushes the result to stack
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
    return if (last is NumberToken) last else throw OrderException("unexpected token $last")
}
