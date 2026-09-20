package com.ali12hhh.kidslearning.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningCatalogTest {
    @Test
    fun catalogContainsExpectedNumberOfItems() {
        assertEquals(LearningCatalog.ARABIC_LETTER_COUNT, LearningCatalog.arabicLetters.size)
        assertEquals(LearningCatalog.ENGLISH_LETTER_COUNT, LearningCatalog.englishLetters.size)
        assertEquals(10, LearningCatalog.digits.size)
    }

    @Test
    fun indexValidationAcceptsOnlyExistingItems() {
        assertTrue(LearningCatalog.isValidArabicIndex(0))
        assertTrue(LearningCatalog.isValidArabicIndex(27))
        assertFalse(LearningCatalog.isValidArabicIndex(-1))
        assertFalse(LearningCatalog.isValidArabicIndex(28))

        assertTrue(LearningCatalog.isValidEnglishIndex(25))
        assertFalse(LearningCatalog.isValidEnglishIndex(26))
        assertTrue(LearningCatalog.isValidDigitIndex(9))
        assertFalse(LearningCatalog.isValidDigitIndex(10))
    }
}
