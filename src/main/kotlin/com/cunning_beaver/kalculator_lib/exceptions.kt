package com.cunning_beaver.kalculator_lib

open class KalculatorException(description: String = "") : Exception(description)

class ParseException(description: String = "") : KalculatorException(description)

class OrderException(description: String = "") : KalculatorException(description)

class MathException(description: String = "") : KalculatorException(description)

