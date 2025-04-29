package ru.viktorgezz.fallingobjectsgame.service

import android.content.Context
import android.media.MediaPlayer
import ru.viktorgezz.fallingobjectsgame.R

class SoundManager(context: Context) {
    private val coinCollectSound: MediaPlayer = MediaPlayer.create(context, R.raw.monetka)

    fun playCoinSound() {
        if (coinCollectSound.isPlaying) {
            coinCollectSound.seekTo(0)
        }
        coinCollectSound.start()
    }

    fun release() {
        coinCollectSound.release()
    }
}