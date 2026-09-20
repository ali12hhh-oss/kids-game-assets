package com.ali12hhh.kidslearning.core

/** Stable identifiers and starter content for the learning areas. */
object LearningCatalog {
    const val ARABIC_LETTER_COUNT = 28
    const val ENGLISH_LETTER_COUNT = 26

    val arabicLetters: List<String> = "ابتثجحخدذرزسشصضطظعغفقكلمنهوي".map(Char::toString)
    val englishLetters: List<String> = ('A'..'Z').map(Char::toString)
    val digits: List<String> = (0..9).map(Int::toString)

    fun isValidArabicIndex(index: Int): Boolean = index in arabicLetters.indices
    fun isValidEnglishIndex(index: Int): Boolean = index in englishLetters.indices
    fun isValidDigitIndex(index: Int): Boolean = index in digits.indices
}
