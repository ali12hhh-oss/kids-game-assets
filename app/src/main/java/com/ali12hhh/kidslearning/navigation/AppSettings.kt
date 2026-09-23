package com.ali12hhh.kidslearning.navigation

import android.content.Context

object AppSettings {
    private const val PREFS = "kids_learning_settings"
    private const val DARK_MODE = "dark_mode"
    private const val SPEECH_ENABLED = "speech_enabled"
    private const val SPEECH_RATE = "speech_rate"
    private const val CHILD_NAME = "child_name"
    private const val CHILD_STARS = "child_stars"
    private const val CHILD_IMAGE_URI = "child_image_uri"
    private const val LAST_SESSION_ID = "last_session_id"
    private const val OWNED_ITEMS = "owned_items"
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
    fun addStars(context: Context, amount: Int) = prefs(context).edit().putInt(CHILD_STARS, (childStars(context) + amount).coerceAtLeast(0)).apply()
    fun awardSessionStars(context: Context, sessionId: String) {
        if (prefs(context).getString(LAST_SESSION_ID, null) != sessionId) {
            addStars(context, 10)
            prefs(context).edit().putString(LAST_SESSION_ID, sessionId).apply()
        }
    }
    fun awardCorrectAnswer(context: Context) = addStars(context, 3)
    fun childImageUri(context: Context): String? = prefs(context).getString(CHILD_IMAGE_URI, null)
    fun setChildImageUri(context: Context, uri: String?) = prefs(context).edit().putString(CHILD_IMAGE_URI, uri).apply()
    fun ownedItems(context: Context): Set<String> = prefs(context).getStringSet(OWNED_ITEMS, emptySet()) ?: emptySet()
    fun buyItem(context: Context, itemId: String, price: Int): Boolean {
        val owned = ownedItems(context)
        if (itemId in owned || childStars(context) < price) return false
        prefs(context).edit().putInt(CHILD_STARS, childStars(context) - price).putStringSet(OWNED_ITEMS, owned + itemId).apply()
        return true
    }
    fun resetProgress(context: Context) =
        prefs(context).edit()
            .putInt(CHILD_STARS, 0)
            .remove(LAST_SESSION_ID)
            .apply()

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
        tts?.setSpeechRate(AppSettings.speechRate(context))
        tts?.speak(
            text,
            android.speech.tts.TextToSpeech.QUEUE_FLUSH,
            null,
            utteranceId
        )
    }
}
