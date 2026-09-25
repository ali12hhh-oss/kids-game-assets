package com.ali12hhh.kidslearning

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import com.ali12hhh.kidslearning.navigation.AppSettings

object HomeGreetingSpeech {
    private const val UTTERANCE_ID = "home_greeting"
    private var tts: TextToSpeech? = null
    private var initialized = false
    private var initializing = false
    private var pendingText: String? = null
    private var pendingOnStart: (() -> Unit)? = null
    private var pendingOnDone: (() -> Unit)? = null

    @Synchronized
    fun initialize(context: Context) {
        if (initialized || initializing) return
        initializing = true
        val appContext = context.applicationContext

        val initListener = TextToSpeech.OnInitListener { status ->
            synchronized(this) {
                initializing = false
                if (status != TextToSpeech.SUCCESS) {
                    tts = null
                    return@OnInitListener
                }

                val speaker = tts ?: return@OnInitListener
                val result = speaker.setLanguage(Locale.forLanguageTag("ar-XA"))
                if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.LANG_MISSING_DATA
                ) {
                    speaker.language = Locale.forLanguageTag("ar-SA")
                }
                speaker.setSpeechRate(AppSettings.speechRate(appContext))
                speaker.setPitch(0.96f)
                selectArabicMaleVoice(speaker)?.let { speaker.voice = it }

                speaker.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        if (utteranceId == UTTERANCE_ID) pendingOnStart?.invoke()
                    }

                    override fun onDone(utteranceId: String?) {
                        if (utteranceId == UTTERANCE_ID) {
                            pendingOnDone?.invoke()
                            clearPending()
                        }
                    }

                    @Deprecated("Deprecated by Android; kept for API compatibility.")
                    override fun onError(utteranceId: String?) {
                        if (utteranceId == UTTERANCE_ID) {
                            pendingOnDone?.invoke()
                            clearPending()
                        }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        if (utteranceId == UTTERANCE_ID) {
                            pendingOnDone?.invoke()
                            clearPending()
                        }
                    }
                })

                pendingText?.let { text -> speakNow(speaker, text) }
            }
        }

        val engine = speakerEnginePackage(appContext)
        tts = if (engine != null) {
            TextToSpeech(appContext, initListener, engine)
        } else {
            TextToSpeech(appContext, initListener)
        }
    }

    @Synchronized
    fun speak(context: Context, text: String, onStart: () -> Unit, onDone: () -> Unit) {
        if (!AppSettings.isSpeechEnabled(context)) {
            onDone()
            return
        }

        initialize(context)
        pendingText = text
        pendingOnStart = onStart
        pendingOnDone = onDone

        if (initialized) tts?.let { speakNow(it, text) }
    }

    @Synchronized
    fun cancel() {
        tts?.stop()
        clearPending()
    }

    private fun speakNow(speaker: TextToSpeech, text: String) {
        initialized = true
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID)
        }
        if (speaker.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID) == TextToSpeech.ERROR) {
            pendingOnDone?.invoke()
            clearPending()
        }
    }

    @Synchronized
    private fun clearPending() {
        pendingText = null
        pendingOnStart = null
        pendingOnDone = null
    }

    private fun speakerEnginePackage(context: Context): String? =
        runCatching {
            context.packageManager
                .queryIntentServices(
                    android.content.Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE), 0
                )
                .firstOrNull { it.serviceInfo.packageName == "com.google.android.tts" }
                ?.serviceInfo?.packageName
        }.getOrNull()

    private fun selectArabicMaleVoice(tts: TextToSpeech): android.speech.tts.Voice? {
        val voices = tts.voices.orEmpty()
            .filter { it.locale.language == "ar" }
            .filterNot { it.isNetworkConnectionRequired }

        val maleMarkers = listOf(
            "male", "man", "maged", "majed", "tarik",
            "standard-b", "standard-c", "wavenet-b", "wavenet-c",
            "chirp3-hd-achird", "chirp3-hd-algenib", "chirp3-hd-algieba",
            "chirp3-hd-alnilam", "chirp3-hd-charon", "chirp3-hd-enceladus",
            "chirp3-hd-fenrir", "chirp3-hd-iapetus", "chirp3-hd-orus",
            "chirp3-hd-puck", "chirp3-hd-rasalgethi", "chirp3-hd-sadachbia",
            "chirp3-hd-sadaltager", "chirp3-hd-schedar", "chirp3-hd-umbriel",
            "chirp3-hd-zubenelgenubi"
        )
        return voices.firstOrNull { voice ->
            val name = voice.name.lowercase(Locale.ROOT)
            maleMarkers.any(name::contains)
        } ?: voices.firstOrNull { it.locale == Locale.forLanguageTag("ar-XA") }
    }
}
