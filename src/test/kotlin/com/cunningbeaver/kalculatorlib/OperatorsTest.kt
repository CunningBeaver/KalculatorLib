package com.cunningbeaver.kalculatorlib

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*


class OperatorsTest {
    @DisplayName("plus not less and not greater than minus")
    @Test fun plusAndMinus() {
        assertTrue(
            BinaryOperatorToken('+') >= BinaryOperatorToken(
                '-'
            )
        )
        assertTrue(
            BinaryOperatorToken('-') >= BinaryOperatorToken(
                '+'
            )
        )
        assertFalse(
            BinaryOperatorToken('+') > BinaryOperatorToken(
                ('-')
            )
        )
        assertFalse(
            BinaryOperatorToken('-') > BinaryOperatorToken(
                '+'
            )
        )
    }

    @DisplayName("multiply not less and not greater than division")
    @Test fun divAndMul() {
        assertTrue(
            BinaryOperatorToken('*') >= BinaryOperatorToken(
                '/'
            )
        )
        assertTrue(
            BinaryOperatorToken('/') >= BinaryOperatorToken(
                '*'
            )
        )
        assertFalse(
            BinaryOperatorToken('*') > BinaryOperatorToken(
                ('/')
            )
        )
        assertFalse(
            BinaryOperatorToken('/') > BinaryOperatorToken(
                '*'
            )
        )
    }

    @DisplayName("multiply and division greater than plus and minus")
    @Test fun mulAndDivGreaterThanPlusAndMinus() {
        assertTrue(
            BinaryOperatorToken('*') > BinaryOperatorToken(
                '+'
            )
        )
        assertTrue(
            BinaryOperatorToken('*') > BinaryOperatorToken(
                '-'
            )
        )
        assertTrue(
            BinaryOperatorToken('/') > BinaryOperatorToken(
                '+'
            )
        )
        assertTrue(
            BinaryOperatorToken('/') > BinaryOperatorToken(
                '-'
            )
        )

        assertFalse(
            BinaryOperatorToken('+') > BinaryOperatorToken(
                '*'
            )
        )
        assertFalse(
            BinaryOperatorToken('+') > BinaryOperatorToken(
                '/'
            )
        )
        assertFalse(
            BinaryOperatorToken('-') > BinaryOperatorToken(
                '*'
            )
        )
        assertFalse(
            BinaryOperatorToken('-') > BinaryOperatorToken(
                '/'
            )
        )
    }

    @DisplayName("power greater than div, mul, plus & minus")
    @Test fun powGreaterThatAll() {
        assertTrue(
            BinaryOperatorToken('^') > BinaryOperatorToken(
                '*'
            )
        )
        assertTrue(
            BinaryOperatorToken('^') > BinaryOperatorToken(
                '/'
            )
        )
        assertTrue(
            BinaryOperatorToken('^') > BinaryOperatorToken(
                '+'
            )
        )
        assertTrue(
            BinaryOperatorToken('^') > BinaryOperatorToken(
                '-'
            )
        )

        assertFalse(
            BinaryOperatorToken('+') > BinaryOperatorToken(
                '^'
            )
        )
        assertFalse(
            BinaryOperatorToken('-') > BinaryOperatorToken(
                '^'
            )
        )
        assertFalse(
            BinaryOperatorToken('*') > BinaryOperatorToken(
                '^'
            )
        )
        assertFalse(
            BinaryOperatorToken('/') > BinaryOperatorToken(
                '^'
            )
        )
    }

    @DisplayName("do binary operators get the right answers")
    @Test fun testBinOpAnswers() {
        assertEquals(
            BinaryOperatorToken('+')(
                NumberToken(
                    23.5
                ), NumberToken(46.5)
            ), NumberToken(70.0)
        )
        assertEquals(
            BinaryOperatorToken('-')(
                NumberToken(
                    75.5
                ), NumberToken(5.5)
            ), NumberToken(70.0)
        )
        assertEquals(
            BinaryOperatorToken('*')(
                NumberToken(
                    2.5
                ), NumberToken(3.0)
            ), NumberToken(7.5)
        )
        assertEquals(
            BinaryOperatorToken('/')(
                NumberToken(
                    45.0
                ), NumberToken(3.0)
            ), NumberToken(15.0)
        )
        assertEquals(
            BinaryOperatorToken('^')(
                NumberToken(
                    2.0
                ), NumberToken(4.0)
            ), NumberToken(16.0)
        )
    }

    @DisplayName("do unary operators get the right answers")
    @Test fun testUnOpAnswers() {
        assertEquals(
            UnaryOperatorToken('-')(
                NumberToken(
                    45.0
                )
            ), NumberToken(-45.0)
        )
    }
}
