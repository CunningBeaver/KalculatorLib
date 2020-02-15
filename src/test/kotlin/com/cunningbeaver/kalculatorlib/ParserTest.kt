package com.cunningbeaver.kalculatorlib

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.*
import org.junit.jupiter.api.*


class ParserTest {
    @DisplayName("parse spaces")
    @Test fun parseSpaces() {
        val expected = arrayOf(
            StartToken(),
            NumberToken(12.0),
            BinaryOperatorToken('+'),
            NumberToken(23.0),
            BinaryOperatorToken('*'),
            OpenBracketToken(),
            NumberToken(45.0),
            BinaryOperatorToken('-'),
            NumberToken(56.0),
            CloseBracketToken(),
            BinaryOperatorToken('*'),
            NumberToken(8.0),
            EndToken()
        )
        val actual = parseToTokens("12\t+23*(45   - 56  ) \n *8")
        assertArrayEquals(expected, actual)
    }

    @DisplayName("deny forbidden symbols")
    @Test fun denyForbiddenSymbols() {
        val expression = "фыва"
        assertThrows<ParseException> {
            parseToTokens(
                expression
            )
        }
    }

    @DisplayName("parse numbers and binary operators")
    @Test fun parseNumbersAndBinaryOperators() {
        val expected = mapOf(
            "1 + 1" to arrayOf(
                StartToken(),
                NumberToken(1.0),
                BinaryOperatorToken('+'),
                NumberToken(1.0),
                EndToken()
            ),
            "23 + 452 - 2" to arrayOf(
                StartToken(),
                NumberToken(23.0),
                BinaryOperatorToken('+'),
                NumberToken(452.0),
                BinaryOperatorToken('-'),
                NumberToken(2.0),
                EndToken()
            ),
            "45  /45" to arrayOf(
                StartToken(),
                NumberToken(45.0),
                BinaryOperatorToken('/'),
                NumberToken(45.0),
                EndToken()
            ),
            "2 * 2 * 2 * 2" to arrayOf(
                StartToken(),
                NumberToken(2.0),
                BinaryOperatorToken('*'),
                NumberToken(2.0),
                BinaryOperatorToken('*'),
                NumberToken(2.0),
                BinaryOperatorToken('*'),
                NumberToken(2.0),
                EndToken()
            )
        )
        val executables = expected.entries.map { (k, v) -> Executable() { assertArrayEquals(v,
            parseToTokens(k)
        ) } }
        assertAll(executables)
    }

    @DisplayName("parse unary operators")
    @Test fun parseUnaryOperators() {
        val expected = arrayOf(
            StartToken(),
            UnaryOperatorToken('-'),
            NumberToken(134.0),
            BinaryOperatorToken('+'),
            UnaryOperatorToken('-'),
            UnaryOperatorToken('-'),
            NumberToken(245.0),
            BinaryOperatorToken('/'),
            NumberToken(45.0),
            EndToken()
        )
        val actual = parseToTokens("-134 + --245 / 45")
        assertArrayEquals(expected, actual)
    }

    @DisplayName("parse floating points")
    @Test fun parseFloatingPoints() {
        val expected = arrayOf(
            StartToken(),
            NumberToken(1.23),
            BinaryOperatorToken('*'),
            NumberToken(234.5),
            EndToken()
        )
        val actual = parseToTokens("1.23 * 234.5")
        assertArrayEquals(expected, actual)
    }

    @DisplayName("throws exception in second floating point")
    @Test fun throwsExceptionInSecondPoint() {
        assertThrows<ParseException> {
            parseToTokens(
                "1*1-1.1.2 + 123.34"
            )
        }
    }

    @DisplayName("parse brackets")
    @Test fun parseBrackets() {
        val expected = mapOf(
            "(-12-23)" to arrayOf(
                StartToken(),
                OpenBracketToken(),
                UnaryOperatorToken('-'),
                NumberToken(12.0),
                BinaryOperatorToken('-'),
                NumberToken(23.0),
                CloseBracketToken(),
                EndToken()
            ),
            "(1+23) + 23" to arrayOf(
                StartToken(),
                OpenBracketToken(),
                NumberToken(1.0),
                BinaryOperatorToken('+'),
                NumberToken(23.0),
                CloseBracketToken(),
                BinaryOperatorToken('+'),
                NumberToken(23.0),
                EndToken()
            ),
            "76 / ((345 - 45.5) + 34)" to arrayOf(
                StartToken(),
                NumberToken(76.0),
                BinaryOperatorToken('/'),
                OpenBracketToken(),
                OpenBracketToken(),
                NumberToken(345.0),
                BinaryOperatorToken('-'),
                NumberToken(45.5),
                CloseBracketToken(),
                BinaryOperatorToken('+'),
                NumberToken(34.0),
                CloseBracketToken(),
                EndToken()
            )
        )
        val executables = expected.entries.map { (k, v) -> Executable() { assertArrayEquals(v,
            parseToTokens(k)
        ) } }
        assertAll(executables)
    }

    @DisplayName("parse functions")
    @Test fun parseFunctions() {
        val expected = mapOf(
            "floor(34.5)" to arrayOf(
                StartToken(),
                FunctionToken("floor"),
                OpenBracketToken(),
                NumberToken(34.5),
                CloseBracketToken(),
                EndToken()
            ),
            "(floor(45+(65*ceil(34.6)))/5" to arrayOf(
                StartToken(),
                OpenBracketToken(),
                FunctionToken("floor"),
                OpenBracketToken(),
                NumberToken(45.0),
                BinaryOperatorToken('+'),
                OpenBracketToken(),
                NumberToken(65.0),
                BinaryOperatorToken('*'),
                FunctionToken("ceil"),
                OpenBracketToken(),
                NumberToken(34.6),
                CloseBracketToken(),
                CloseBracketToken(),
                CloseBracketToken(),
                BinaryOperatorToken('/'),
                NumberToken(5.0),
                EndToken()
            )
        )
        val executables = expected.entries.map { (k, v) -> Executable() { assertArrayEquals(v,
            parseToTokens(k)
        ) } }
        assertAll(executables)
    }
    @DisplayName("parse function arguments")
    @Test fun parseFunctionArguments() {
        val expression = "log(floor(1.2), 2/8)"
        val expected = arrayOf(
            StartToken(),
            FunctionToken("log"),
            OpenBracketToken(),
            FunctionToken("floor"),
            OpenBracketToken(),
            NumberToken(1.2),
            CloseBracketToken(),
            GrouperToken(),
            NumberToken(2.0),
            BinaryOperatorToken('/'),
            NumberToken(8.0),
            CloseBracketToken(),
            EndToken()
        )
        assertArrayEquals(expected, parseToTokens(expression))
    }
}
