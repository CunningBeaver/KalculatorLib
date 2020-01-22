package com.cunning_beaver.kalculator_lib


interface Operand {
    fun toNumberList(): NumberList
}

enum class TokenTypes {
    NUMBER, OPEN_BRACKET, CLOSE_BRACKET, FUNCTION, OPERATOR;

    companion object {
        private val operandBegins = setOf(NUMBER, OPEN_BRACKET, FUNCTION)
        private val validOperators = getValidBinaryOperators() + getValidUnaryOperators()
        private val validChars = ('a'..'z') + ('A'..'Z')
        private val validNumbers = '0'..'9'

        fun getTypeByFirstLetter(c: Char): TokenTypes {
            return when (c) {
                in validChars -> FUNCTION
                in validNumbers -> NUMBER
                ',', in validOperators -> OPERATOR
                '(' -> OPEN_BRACKET
                ')' -> CLOSE_BRACKET
                else -> throw ParseException("Unexpected symbol $c")
            }
        }
        fun isOperandBegin(token: TokenTypes) = token in operandBegins
    }
}

sealed class Token {
    open val priority = 0
    override fun toString() = "${this::class.simpleName}()"
    open operator fun compareTo(other: Token): Int = throw KalculatorException("you can't compare $this and $other")
}

class StartToken : Token() {
    val value = 'S'
    override fun equals(other: Any?) = other is StartToken
    override fun hashCode() = value.hashCode()
}

data class NumberToken(val value: Double) : Token(), Operand {
    operator fun unaryMinus() = NumberToken(-value)
    operator fun plus(v: NumberToken) = NumberToken(value + v.value)
    operator fun minus(v: NumberToken) = NumberToken(value - v.value)
    operator fun times(v: NumberToken) = NumberToken(value * v.value)
    operator fun div(v: NumberToken) = NumberToken(value * v.value)
    override fun toNumberList() = NumberList(arrayOf(this))
}

data class NumberList(val numbers: Array<NumberToken>) : Token(), Operand {
    override fun equals(other: Any?): Boolean {
        if (other !is NumberList) return false
        return this === other || numbers.contentEquals(other.numbers)
    }
    override fun hashCode() = numbers.contentHashCode()
    override fun toNumberList() = this
}

data class BinaryOperatorToken(val value: Char) : Token() {
    override val priority = getBinaryOperatorPriority(value)
    private val function = getBinaryOperatorFunction(value)
    operator fun invoke(a: NumberToken, b: NumberToken) = function(a, b)
    override operator fun compareTo(other: Token)
        = if (other is BinaryOperatorToken || other is GrouperToken) priority - other.priority
          else throw OrderException("unexpected operator $other")


}

data class UnaryOperatorToken(val value: Char) : Token() {
    private val function = getUnaryOperatorFunction(value)
    operator fun invoke(num: NumberToken) = function(num)
}

data class FunctionToken(val value: String) : Token() {
    private val function = getFunctionOfFunctionToken(value)
    private val argsLength = getArgsLengthOgFunctions(value)

    operator fun invoke(numbers: NumberList): NumberToken {
        if (argsLength != -1 && argsLength != numbers.numbers.size)
            throw MathException("expected $argsLength parameters, found ${numbers.numbers.size}")
        return function(numbers.numbers)
    }
}

class GrouperToken : Token() {
    private val value = ','
    override val priority = 1
    operator fun invoke(a: Operand, b: Operand) = NumberList(a.toNumberList().numbers + b.toNumberList().numbers)
    override fun equals(other: Any?) = other is GrouperToken
    override fun hashCode() = value.hashCode()

}

class OpenBracketToken : Token() {
    private val value = '('
    override fun equals(other: Any?) = other is OpenBracketToken
    override fun hashCode() = value.hashCode()
}

class CloseBracketToken : Token() {
    private val value = ')'
    override fun equals(other: Any?) = other is CloseBracketToken
    override fun hashCode() =  value.hashCode()
}

class EndToken : Token() {
    private val value = 'E'
    override fun equals(other: Any?) = other is EndToken
    override fun hashCode() = value.hashCode()
}

