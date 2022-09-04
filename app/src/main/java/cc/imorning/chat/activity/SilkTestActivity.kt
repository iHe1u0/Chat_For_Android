package cc.imorning.chat.activity

import android.os.Bundle
import android.os.Environment
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.utils.AudioMessageUtils
import cc.imorning.silk.SilkCoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class SilkTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatTheme {
                // var result by remember { mutableStateOf("/sdcard/pcm16k.pcm") }
                var result by remember { mutableStateOf("/sdcard/download/test.mp3") }
                var recordStatus by remember { mutableStateOf("Record") }
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                result = SilkCoder.getInstance().getVersion()
                            }
                        ) {
                            Text(text = "Get Version")
                        }
                        Button(onClick = {
                            result = SilkCoder.getInstance().decode(result).orEmpty()
                        }) {
                            Text(text = "Decode")
                        }
                        Button(onClick = {
                            result = ""
                            MainScope().launch(Dispatchers.IO) {
                                result =
                                    SilkCoder.getInstance().encode(
                                        File(
                                            Environment.getExternalStorageDirectory(),
                                            "music.mp3"
                                        ).absolutePath
                                    ).orEmpty()
                            }
                        }) {
                            Text(text = "Encode")
                        }
                        Button(onClick = {
                            play(result)
                        }) {
                            Text(text = "Play")
                        }
                        Button(onClick = {
                            record()
                            recordStatus = if (recordStatus == "Record") {
                                "Stop"
                            } else {
                                "Record"
                            }
                        }) {
                            Text(text = recordStatus)
                        }
                    }
                    Text(text = result)
                }
            }
        }
    }

    private fun play(result: String) {
        if (result.isEmpty()) {
            return
        }
        val audioMessageUtils = AudioMessageUtils.getInstance()
        if (audioMessageUtils.isIdle) {
            audioMessageUtils.startPlayPCM(result)
        } else {
            audioMessageUtils.stopPlay()
        }
    }

    private fun record() {
        val audioMessageUtils = AudioMessageUtils.getInstance()
        if (audioMessageUtils.isIdle) {
            audioMessageUtils.startRecord(true)
        } else {
            audioMessageUtils.stopRecord()
        }
    }

    companion object {
        private const val TAG = "SilkTestActivity"
    }
}