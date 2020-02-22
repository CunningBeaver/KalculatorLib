package com.cunningbeaver.kalculatorlib

import java.util.*


private enum class SortCommands {
    TO_RESULT, TO_INNER, LAST_INNER_TO_RESULT, REMOVE_PAIRS, END
}

/**
 * keeps state of current sorting process
 */
private class SorterState {
    // the reverse polish notation needs a two stacks
    private val resultStack: Stack<Token> = Stack()
    val innerStack: Stack<Token> = Stack()

    private var numbersCount: Int = 0
    private var binaryOperatorsCount: Int = 0
    var isOperand = true

    // you cannot get the result until completion and cannot set new values after completion
    private var completeFlag: Boolean = false
    fun isComplete() = completeFlag
    fun complete() {
        completeFlag = true
    }

    var index: Int = 0
    val result: Array<Token>
        get() {
            if (!completeFlag)
                throw KalculatorException("cannot get a result before a process ending")
            if (numbersCount - binaryOperatorsCount != 1)
                throw OrderException("The number of operators does not match the number of operands")
            return resultStack.toArray(arrayOf())
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

/**
 * main function of this file: sorts a [tokens][Token] to reverse polish notation
 * @param tokens [Array] of [tokens][Token] from [parseToTokens] function
 * @return sorted [Array] of [tokens][Token]
 */
fun sortTokens(tokens: Array<Token>): Array<Token> {
    val state = SorterState()

    // checks if there is a StartToken and an EndToken
    if (tokens.first() !is StartToken)
        throw OrderException("first token is not a StartToken")
    if (tokens.last() !is EndToken)
        throw OrderException("last token is not a EndToken")

    state.innerStack.push(tokens.first())
    ++state.index

    var previous: Token? = null
    while (!state.isComplete()) {
        val token = tokens[state.index]
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

/**
 * table of relationships between [tokens][Token]
 */
private fun getCommand(current: Token, lastInStack: Token?) =
    when (current) {
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
            is StartToken -> throw OrderException(
                "Unexpected token $current"
            )
            is BinaryOperatorToken, is GrouperToken -> SortCommands.LAST_INNER_TO_RESULT
            is UnaryOperatorToken, is FunctionToken -> SortCommands.LAST_INNER_TO_RESULT
            is OpenBracketToken -> SortCommands.REMOVE_PAIRS
            else -> throw OrderException("Unexpected token $current")
        }
        else -> throw OrderException("Unexpected token $current")
    }

/**
 * gives functions with different actions depending on the received [command][SortCommands]
 * @param [command][SortCommands]
 * @return lambda that takes [SorterState] and [Token] then makes action
 */
private fun getFunction(command: SortCommands): (SorterState, Token) -> Unit =
    when (command) {
        SortCommands.TO_RESULT -> { state, token ->
            // just pushes token to result
            state.pushToResult(token)
            ++state.index
        }

        SortCommands.TO_INNER -> { state, token ->
            // just pushes token to inner stack
            state.innerStack.push(token)
            ++state.index
        }

        SortCommands.LAST_INNER_TO_RESULT -> { state, _ ->
            // pops last token from inner stack and pushes to result stack
            state.pushToResult(state.innerStack.pop())
        }

        SortCommands.REMOVE_PAIRS -> { state, _ ->
            // removes last token from stack and ignores current token
            state.innerStack.pop()
            ++state.index
        }

        SortCommands.END -> { state, _ ->
            // completes the process
            state.complete()
        }
    }
