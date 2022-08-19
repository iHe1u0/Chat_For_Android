package cc.imorning.chat.utils

import android.content.Context
import android.media.SoundPool
import cc.imorning.chat.R

object RingUtils {

    fun playNewMessage(context: Context) {
        val soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        val loadId = soundPool.load(context, R.raw.msg, 1)
        soundPool.setOnLoadCompleteListener { _soundPool, _, _ ->
            _soundPool.play(loadId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }
}