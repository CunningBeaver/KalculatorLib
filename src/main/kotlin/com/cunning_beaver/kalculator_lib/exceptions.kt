package com.cunning_beaver.kalculator_lib


open class KalculatorException(message: String = "") : Exception()

class ParseException(message: String = "") : KalculatorException(message)

class OrderException(message: String = "") : KalculatorException(message)

class MathException(message: String = "") : KalculatorException(message)

