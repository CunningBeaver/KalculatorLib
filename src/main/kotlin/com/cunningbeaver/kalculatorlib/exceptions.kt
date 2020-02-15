package com.cunningbeaver.kalculatorlib


open class KalculatorException(message: String = "") : Exception(message)

class ParseException(message: String = "") : KalculatorException(message)

class OrderException(message: String = "") : KalculatorException(message)

class MathException(message: String = "") : KalculatorException(message)

