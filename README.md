#KalculatorLib

KalculatorLib (not a typo) is a calculator library written in Kotlin
language, based on the Polish inverse notation algorithm.

## Install
See https://jitpack.io/

## Using
Only one function is required to working
```kotlin
fun calculate(expression: String): Double
```
You can input a common math expressions to it:
```kotlin
calculate("1.5 + -(8 * 4)")  // -31.5
calculate("((34 + 34) - 34) / 34")  // 1
```
It supports binary operators like `1 + 1`, unary operators like `-2`, brackets
and functions like `log(8, 2)`. There are few functions now, but in the future 
i want to add new ones.
