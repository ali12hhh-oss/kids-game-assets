package com.ali12hhh.kidslearning.navigation

import android.content.Context

object AppSettings {
    private const val PREFS = "kids_learning_settings"
    private const val DARK_MODE = "dark_mode"
    private const val SPEECH_ENABLED = "speech_enabled"
    private const val SPEECH_RATE = "speech_rate"
    private const val CHILD_NAME = "child_name"
    private const val CHILD_STARS = "child_stars"
    private const val PARENT_PIN = "parent_pin"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isDarkMode(context: Context): Boolean = prefs(context).getBoolean(DARK_MODE, false)
    fun setDarkMode(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(DARK_MODE, value).apply()

    fun isSpeechEnabled(context: Context): Boolean = prefs(context).getBoolean(SPEECH_ENABLED, true)
    fun setSpeechEnabled(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(SPEECH_ENABLED, value).apply()

    fun speechRate(context: Context): Float = prefs(context).getFloat(SPEECH_RATE, 0.88f)
    fun setSpeechRate(context: Context, value: Float) =
        prefs(context).edit().putFloat(SPEECH_RATE, value.coerceIn(0.5f, 1.5f)).apply()

    fun childName(context: Context): String = prefs(context).getString(CHILD_NAME, "صديقي الصغير") ?: "صديقي الصغير"
    fun setChildName(context: Context, value: String) =
        prefs(context).edit().putString(CHILD_NAME, value.trim().ifBlank { "صديقي الصغير" }).apply()

    fun childStars(context: Context): Int = prefs(context).getInt(CHILD_STARS, 0)
    fun resetProgress(context: Context) =
        prefs(context).edit().putInt(CHILD_STARS, 0).apply()

    fun hasParentPin(context: Context): Boolean = prefs(context).contains(PARENT_PIN)
    fun setParentPin(context: Context, pin: String) =
        prefs(context).edit().putString(PARENT_PIN, pin).apply()
    fun verifyParentPin(context: Context, pin: String): Boolean =
        prefs(context).getString(PARENT_PIN, null) == pin

    fun clearAllData(context: Context) {
        prefs(context).edit().clear().apply()
    }
}

fun speakIfEnabled(
    context: Context,
    tts: android.speech.tts.TextToSpeech?,
    text: String,
    utteranceId: String
) {
    if (AppSettings.isSpeechEnabled(context)) {
        tts?.speak(
            text,
            android.speech.tts.TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId
        )
    }
}
