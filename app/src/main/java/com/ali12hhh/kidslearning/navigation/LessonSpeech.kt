package com.ali12hhh.kidslearning.navigation

import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/**
 * Shared speech configuration for all Arabic and English learning pages.
 *
 * It keeps the app on Android TTS, but selects the best available installed
 * voice for the requested language instead of relying on the device default
 * voice. This makes speech consistent across the learning screens.
 */
object LessonSpeech {
    val ARABIC_LOCALE: Locale = Locale.forLanguageTag("ar-SA")
    val ENGLISH_LOCALE: Locale = Locale.US

    fun configure(engine: TextToSpeech, locale: Locale) {
        val languageResult = engine.setLanguage(locale)
        if (languageResult == TextToSpeech.LANG_NOT_SUPPORTED ||
            languageResult == TextToSpeech.LANG_MISSING_DATA
        ) {
            engine.setLanguage(
                if (locale.language == "ar") Locale.forLanguageTag("ar") else Locale.ENGLISH
            )
        }

        val candidates = engine.voices
            .asSequence()
            .filter { it.locale.language == locale.language }
            .filter {
                locale.country.isEmpty() ||
                    it.locale.country.isEmpty() ||
                    it.locale.country.equals(locale.country, ignoreCase = true)
            }
            .sortedWith(
                compareByDescending<Voice> { it.quality }
                    .thenBy { it.latency }
                    .thenBy { it.isNetworkConnectionRequired }
            )
            .toList()

        candidates.firstOrNull()?.let { engine.voice = it }

        // A slightly slower, calmer delivery is easier for young learners.
        engine.setSpeechRate(if (locale.language == "ar") 0.78f else 0.80f)
        engine.setPitch(0.96f)
    }
}


/** Central letter pronunciation: normal interaction speaks the sound; names are explicit-only UI actions. */
object LetterSpeech {
    private val arabicSounds = mapOf(
        "ا" to "أَ", "أ" to "أَ", "إ" to "إِ", "آ" to "آ", "ب" to "بَ", "ت" to "تَ", "ث" to "ثَ",
        "ج" to "جَ", "ح" to "حَ", "خ" to "خَ", "د" to "دَ", "ذ" to "ذَ", "ر" to "رَ", "ز" to "زَ",
        "س" to "سَ", "ش" to "شَ", "ص" to "صَ", "ض" to "ضَ", "ط" to "طَ", "ظ" to "ظَ", "ع" to "عَ",
        "غ" to "غَ", "ف" to "فَ", "ق" to "قَ", "ك" to "كَ", "ل" to "لَ", "م" to "مَ", "ن" to "نَ",
        "ه" to "هَ", "و" to "وَ", "ي" to "يَ"
    )
    private val arabicNames = mapOf("ا" to "ألف", "ب" to "باء", "ت" to "تاء", "ث" to "ثاء", "ج" to "جيم", "ح" to "حاء", "خ" to "خاء", "د" to "دال", "ذ" to "ذال", "ر" to "راء", "ز" to "زاي", "س" to "سين", "ش" to "شين", "ص" to "صاد", "ض" to "ضاد", "ط" to "طاء", "ظ" to "ظاء", "ع" to "عين", "غ" to "غين", "ف" to "فاء", "ق" to "قاف", "ك" to "كاف", "ل" to "لام", "م" to "ميم", "ن" to "نون", "ه" to "هاء", "و" to "واو", "ي" to "ياء")
    private val englishSounds = mapOf(
        "a" to "æ", "b" to "buh", "c" to "kuh", "d" to "duh", "e" to "eh", "f" to "fuh", "g" to "guh",
        "h" to "huh", "i" to "ih", "j" to "juh", "k" to "kuh", "l" to "luh", "m" to "muh", "n" to "nuh",
        "o" to "ah", "p" to "puh", "q" to "kwuh", "r" to "ruh", "s" to "sss", "t" to "tuh", "u" to "uh",
        "v" to "vuh", "w" to "wuh", "x" to "ks", "y" to "yuh", "z" to "zuh"
    )
    fun arabic(letter: String): String = arabicSounds[letter.trim().firstOrNull()?.toString()] ?: letter
    fun arabicName(letter: String): String = arabicNames[letter.trim().firstOrNull()?.toString()] ?: letter
    fun englishName(letter: String): String = letter.trim().uppercase()
    fun english(letter: String): String = englishSounds[letter.trim().lowercase().firstOrNull()?.toString()] ?: letter
    fun speakArabic(tts: TextToSpeech?, letter: String, id: String = "letter_sound") =
        tts?.speak(arabic(letter), TextToSpeech.QUEUE_FLUSH, null, id)
    fun speakEnglish(tts: TextToSpeech?, letter: String, id: String = "letter_sound") =
        tts?.speak(english(letter), TextToSpeech.QUEUE_FLUSH, null, id)
}
