package com.ali12hhh.kidslearning.navigation

import android.content.Context
import android.media.MediaPlayer

/**
 * Short gameplay sound effects for Reebo's adventure game.
 * The WAV assets are generated locally for this project, so no third-party
 * audio license or network download is required.
 */
object GameSoundPlayer {
    enum class Sound {
        COIN_PICKUP,
        TRAP_IMPACT
    }

    fun play(context: Context, sound: Sound) {
        val resourceId = when (sound) {
            Sound.COIN_PICKUP -> com.ali12hhh.kidslearning.R.raw.coin_pickup
            Sound.TRAP_IMPACT -> com.ali12hhh.kidslearning.R.raw.trap_impact
        }

        runCatching {
            MediaPlayer.create(context.applicationContext, resourceId)?.apply {
                setOnCompletionListener { it.release() }
                setOnErrorListener { player, _, _ ->
                    player.release()
                    true
                }
                start()
            }
        }
    }
}
