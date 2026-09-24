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
    val ARABIC_LOCALE: Locale = Locale("ar", "SA")
    val ENGLISH_LOCALE: Locale = Locale.US

    fun configure(engine: TextToSpeech, locale: Locale) {
        val languageResult = engine.setLanguage(locale)
        if (languageResult == TextToSpeech.LANG_NOT_SUPPORTED ||
            languageResult == TextToSpeech.LANG_MISSING_DATA
        ) {
            engine.setLanguage(
                if (locale.language == "ar") Locale("ar") else Locale.ENGLISH
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
