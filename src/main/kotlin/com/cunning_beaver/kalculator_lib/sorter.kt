package com.cunning_beaver.kalculator_lib

import java.util.*


private enum class SortCommands {
    TO_RESULT, TO_INNER, LAST_INNER_TO_RESULT, REMOVE_PAIRS, END
}


private class SorterState {
    private val resultStack: Stack<Token> = Stack()
    val innerStack: Stack<Token> = Stack()

    private var numbersCount: Int = 0
    private var binaryOperatorsCount: Int = 0
    var isOperand = true
    private var completeFlag: Boolean = false
    private var invalidExpression = false
    var i : Int = 0
    val result: Array<Token>
        get() {
            if (!completeFlag)
                throw KalculatorException("cannot get a result before a process ending")
            if (numbersCount - binaryOperatorsCount != 1)
                throw OrderException("The number of operators does not match the number of operands")
            return resultStack.toArray(arrayOf())
        }
    fun isComplete() = completeFlag
    fun complete() {
        completeFlag = true
    }
    fun setInvalid() {
        invalidExpression= true
    }
    fun pushToResult(token: Token) {
        if (completeFlag)
            throw OrderException("got the completed state")
        when (token) {
            is Operand -> {
                resultStack.push(token)
                ++numbersCount
            }
            is BinaryOperatorToken, is GrouperToken -> {
                if (numbersCount - binaryOperatorsCount < 1)
                    throw OrderException("unexpected operator $token")
                resultStack.push(token)
                ++binaryOperatorsCount
            }
            is UnaryOperatorToken, is FunctionToken -> {
                if (numbersCount < 1)
                    throw OrderException("unexpected operator $token")
                resultStack.push(token)
            }
            else -> throw KalculatorException("forbidden token $token")
        }

    }
}

fun sortTokens(tokens: Array<Token>): Array<Token> {
    val state = SorterState()

    if (tokens.first() !is StartToken)
        throw OrderException("first token is not a StartToken")
    if (tokens.last() !is EndToken)
        throw OrderException("last token is not a EndToken")

    state.innerStack.push(tokens.first())
    ++state.i

    var previous: Token? = null
    while (!state.isComplete()) {
        val token = tokens[state.i]
        val lastInStack = if (state.innerStack.empty()) null else state.innerStack.last()
        val command = getCommand(token, lastInStack)
        val func = getFunction(command)
        if (token !== previous)
            checkOrder(state, token)
        func(state, token)
        previous = token
    }
    return state.result
}

private fun checkOrder(state: SorterState, current: Token) {
    when (current) {
        is Operand -> {
            if (!state.isOperand)
                throw OrderException("unexpected operand $current")
            state.isOperand = false
        }
        is BinaryOperatorToken, is GrouperToken -> {
            if (state.isOperand)
                throw OrderException("unexpected operator $current")
            state.isOperand = true
        }
        is UnaryOperatorToken, is FunctionToken -> {
            if (!state.isOperand)
                throw OrderException("unexpected operator $current")
        }
        is OpenBracketToken -> {
            if (!state.isOperand)
                throw OrderException("unexpected token $current")
        }
        is CloseBracketToken -> {
            if (state.isOperand)
                throw OrderException("unexpected token $current")
        }
        is EndToken -> {
            if (state.isOperand)
                throw OrderException("unexpected end")
        }
    }
}

private fun getCommand(current: Token, lastInStack: Token?)
    = when (current) {
        is StartToken -> SortCommands.TO_INNER
        is EndToken -> when (lastInStack) {
            is StartToken -> SortCommands.END
            is BinaryOperatorToken, is GrouperToken -> SortCommands.LAST_INNER_TO_RESULT
            is UnaryOperatorToken, is FunctionToken -> SortCommands.LAST_INNER_TO_RESULT
            else -> throw OrderException("Unexpected token $current")
        }
        is BinaryOperatorToken, is GrouperToken -> when (lastInStack) {
            is StartToken -> SortCommands.TO_INNER
            is OpenBracketToken -> SortCommands.TO_INNER
            is UnaryOperatorToken, is FunctionToken -> SortCommands.LAST_INNER_TO_RESULT
            is BinaryOperatorToken, is GrouperToken ->
                if (current > lastInStack) SortCommands.TO_INNER else SortCommands.LAST_INNER_TO_RESULT
            else -> throw OrderException("Unexpected token $current")
        }
        is UnaryOperatorToken, is FunctionToken -> SortCommands.TO_INNER
        is NumberToken -> SortCommands.TO_RESULT
        is OpenBracketToken -> SortCommands.TO_INNER
        is CloseBracketToken -> when (lastInStack) {
            is StartToken -> throw OrderException("Unexpected token $current")
            is BinaryOperatorToken, is GrouperToken -> SortCommands.LAST_INNER_TO_RESULT
            is UnaryOperatorToken, is FunctionToken -> SortCommands.LAST_INNER_TO_RESULT
            is OpenBracketToken -> SortCommands.REMOVE_PAIRS
            else -> throw OrderException("Unexpected token $current")
        }
        else -> throw OrderException("Unexpected token $current")
    }

private fun getFunction(command: SortCommands): (SorterState, Token) -> Unit
    = when (command) {
        SortCommands.TO_RESULT -> { state, token ->
            state.pushToResult(token)
            ++state.i
        }
        SortCommands.TO_INNER -> { state, token ->
            state.innerStack.push(token)
            ++state.i
        }
        SortCommands.LAST_INNER_TO_RESULT -> { state, _ ->
            state.pushToResult(state.innerStack.pop())
        }
        SortCommands.REMOVE_PAIRS -> { state, _ ->
            state.innerStack.pop()
            ++state.i
        }
        SortCommands.END -> { state, _ ->
            state.complete()
        }
    }

fun main() {
    sortTokens(parseToTokens("-123+123"))
}
