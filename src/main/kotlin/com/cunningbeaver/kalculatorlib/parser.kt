package com.cunningbeaver.kalculatorlib

import java.util.*

private class ParserState {
    val tokens = Stack<Token>()
    val buffer = StringBuffer()
    var typeOfCurrentToken: TokenTypes? = null
    var wasFloatPoint = false
    var isOperand = true
    var wasUnaryMinus = false

    fun clearState() {
        buffer.setLength(0)
        wasFloatPoint = false
        wasUnaryMinus = false
        typeOfCurrentToken = null
    }

}

private val handlers = hashMapOf<TokenTypes, (ParserState, Char, Char?) -> Unit>(
    TokenTypes.OPEN_BRACKET to { state, _, _ ->
        state.tokens.add(OpenBracketToken())
        state.clearState()
        state.isOperand = true
    },
    TokenTypes.CLOSE_BRACKET to { state, _, _ ->
        state.tokens.add(CloseBracketToken())
        state.clearState()
        state.isOperand = false
    },
    TokenTypes.OPERATOR to { state, current, _ ->
        if (state.isOperand) {
            state.tokens.add(UnaryOperatorToken(current))
            state.clearState()
        } else {
            if (current == ',')
                state.tokens.add(GrouperToken())
            else
                state.tokens.add(BinaryOperatorToken(current))
            state.clearState()
            state.isOperand = true
        }
    },
    TokenTypes.NUMBER to { state, current, next ->
        if (!state.isOperand)
            throw OrderException("unexpected number")
        state.buffer.append(current)
        if (next == '.') {
            if (state.wasFloatPoint)
                throw ParseException("Unexpected floating point")
            else
                state.wasFloatPoint = true
        }
        else if (!isNextANumberToken(next)) {
            state.tokens.add(NumberToken(state.buffer.toString().toDouble()))
            state.clearState()
            state.isOperand = false
        }
    },
    TokenTypes.FUNCTION to { state, current, next ->
        state.buffer.append(current)
        if (!isNextAFunctionToken(next)) {
            state.tokens.add(FunctionToken(state.buffer.toString()))
            state.clearState()
        }
    }
)

private fun isNextAFunctionToken(next: Char?): Boolean {
    return next != null && TokenTypes.getTypeByFirstLetter(
        next
    ) in arrayOf(
        TokenTypes.FUNCTION,
        TokenTypes.NUMBER
    )
}

private fun isNextANumberToken(next: Char?): Boolean {
    return next != null && (TokenTypes.getTypeByFirstLetter(
        next
    ) == TokenTypes.NUMBER || next == '.')
}

fun parseToTokens(input: String): Array<Token> {
    val state = ParserState()
    val str = input.replace(Regex("\\s"), "")

    state.tokens.add(StartToken())
    for (i in str.indices) {
        val current: Char = str[i]
        val next: Char? = if (i < str.length - 1) str[i + 1] else null
        if (state.typeOfCurrentToken == null)
            state.typeOfCurrentToken =
                TokenTypes.getTypeByFirstLetter(current)
        handlers[state.typeOfCurrentToken!!]?.invoke(state, current, next)
    }
    state.tokens.add(EndToken())
    return state.tokens.toArray(arrayOf<Token>())
}
