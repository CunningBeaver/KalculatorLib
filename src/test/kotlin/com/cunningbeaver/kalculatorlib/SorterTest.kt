package com.cunningbeaver.kalculatorlib

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable


class SorterTest {
    @DisplayName("sort numbers binary operators and unary minus")
    @Test fun numbersAndBinaryOperators() {
        // 1 + -2 * 3 - 4
        val start = arrayOf(
            StartToken(),
            NumberToken(1.0),
            BinaryOperatorToken('+'),
            UnaryOperatorToken('-'),
            NumberToken(2.0),
            BinaryOperatorToken('*'),
            NumberToken(3.0),
            BinaryOperatorToken('-'),
            NumberToken(4.0),
            EndToken()
        )
        // 1 2- 3 * + 4 -
        val expected = arrayOf(
            NumberToken(1.0),
            NumberToken(2.0),
            UnaryOperatorToken('-'),
            NumberToken(3.0),
            BinaryOperatorToken('*'),
            BinaryOperatorToken('+'),
            NumberToken(4.0),
            BinaryOperatorToken('-')
        )
        val actual = sortTokens(start)
        assertArrayEquals(expected, actual)
    }

    @DisplayName("sort numbers with brackets")
    @Test fun numbersWithBrackets() {
        // 1 + 2 * (3 + 4 - (5 + 6)) / 7 ^ 8
        val start = arrayOf(
            StartToken(),
            NumberToken(1.0),
            BinaryOperatorToken('+'),
            NumberToken(2.0),
            BinaryOperatorToken('*'),
            OpenBracketToken(),
            NumberToken(3.0),
            BinaryOperatorToken('+'),
            NumberToken(4.0),
            BinaryOperatorToken('-'),
            OpenBracketToken(),
            NumberToken(5.0),
            BinaryOperatorToken('+'),
            NumberToken(6.0),
            CloseBracketToken(),
            CloseBracketToken(),
            BinaryOperatorToken('/'),
            NumberToken(7.0),
            BinaryOperatorToken('^'),
            NumberToken(8.0),
            EndToken()
        )
        // 1 2 3 4 + 5 6 + - * 7 8 ^ / +
        val expected = arrayOf(
            NumberToken(1.0),
            NumberToken(2.0),
            NumberToken(3.0),
            NumberToken(4.0),
            BinaryOperatorToken('+'),
            NumberToken(5.0),
            NumberToken(6.0),
            BinaryOperatorToken('+'),
            BinaryOperatorToken('-'),
            BinaryOperatorToken('*'),
            NumberToken(7.0),
            NumberToken(8.0),
            BinaryOperatorToken('^'),
            BinaryOperatorToken('/'),
            BinaryOperatorToken('+')
        )
        val actual = sortTokens(start)
        assertArrayEquals(expected, actual)
    }

    @DisplayName("sort functions")
    @Test fun sortFunctions() {
        // floor(1/2) + ceil(3/4)
        val start = arrayOf(
            StartToken(),
            FunctionToken("floor"),
            OpenBracketToken(),
            NumberToken(1.0),
            BinaryOperatorToken('/'),
            NumberToken(2.0),
            CloseBracketToken(),
            BinaryOperatorToken('+'),
            FunctionToken("ceil"),
            OpenBracketToken(),
            NumberToken(3.0),
            BinaryOperatorToken('/'),
            NumberToken(4.0),
            CloseBracketToken(),
            EndToken()
        )
        // 1 2 / floor 3 4 / ceil +
        val expected = arrayOf(
            NumberToken(1.0),
            NumberToken(2.0),
            BinaryOperatorToken('/'),
            FunctionToken("floor"),
            NumberToken(3.0),
            NumberToken(4.0),
            BinaryOperatorToken('/'),
            FunctionToken("ceil"),
            BinaryOperatorToken('+')
        )
        assertArrayEquals(expected, sortTokens(start))
    }

    @DisplayName("sort functions with arguments")
    @Test fun sortFunctionsWithArguments() {
        // log(1, 2)
        val start = arrayOf(
            StartToken(),
            FunctionToken("log"),
            OpenBracketToken(),
            NumberToken(1.0),
            GrouperToken(),
            NumberToken(2.0),
            CloseBracketToken(),
            EndToken()
        )
        // 1 2 , log
        val expected = arrayOf(
            NumberToken(1.0),
            NumberToken(2.0),
            GrouperToken(),
            FunctionToken("log")
        )
        assertArrayEquals(expected, sortTokens(start))
    }

    @DisplayName("throws an exception when does'nt find a StartToken")
    @Test fun throwsStartTokensOrderException() {
        // 1 + 1
        val start = arrayOf(
            NumberToken(1.0),
            BinaryOperatorToken('+'),
            NumberToken(1.0),
            EndToken()
        )
        assertThrows<OrderException> {
            sortTokens(
                start
            )
        }
    }

    @DisplayName("throws an exception when does'nt find an EndToken")
    @Test fun throwsEndTokensOrderException() {
        // 1 + 1
        val start = arrayOf(
            StartToken(),
            NumberToken(1.0),
            BinaryOperatorToken('+'),
            NumberToken(1.0)
        )
        assertThrows<OrderException> {
            sortTokens(
                start
            )
        }
    }

    @DisplayName("throws an exception when finds the wrong binary operators order")
    @Test fun throwsBinaryOperatorsOrderException() {
        // 1 + + 1
        // + 2
        // 3 +
        val start = arrayOf(
            arrayOf(
                StartToken(),
                NumberToken(1.0),
                BinaryOperatorToken('+'),
                BinaryOperatorToken('+'),
                NumberToken(1.0)
            ),
            arrayOf(
                StartToken(),
                BinaryOperatorToken('+'),
                NumberToken(2.0),
                EndToken()
            ),
            arrayOf(
                StartToken(),
                NumberToken(3.0),
                BinaryOperatorToken('+'),
                EndToken()
            )
        )
        val asserts = start.map { arr ->
            Executable {
                assertThrows<OrderException> {
                    sortTokens(
                        arr
                    )
                }
            }
        }
        assertAll(asserts)
    }

    @DisplayName("throws an exception when finds an unpaired braces")
    @Test fun throwsUnpairedBracesOrderException() {
        // ((35 + 10)
        // (24 * 54))
        val start = arrayOf(
            arrayOf(
                StartToken(),
                OpenBracketToken(),
                OpenBracketToken(),
                NumberToken(35.0),
                BinaryOperatorToken('+'),
                NumberToken(10.0),
                CloseBracketToken(),
                EndToken()
            ),
            arrayOf(
                StartToken(),
                OpenBracketToken(),
                NumberToken(24.0),
                BinaryOperatorToken('*'),
                NumberToken(54.0),
                CloseBracketToken(),
                CloseBracketToken(),
                EndToken()
            )
        )
        val asserts = start.map { arr ->
            Executable {
                assertThrows<OrderException> {
                    sortTokens(
                        arr
                    )
                }
            }
        }
        assertAll(asserts)
    }

}