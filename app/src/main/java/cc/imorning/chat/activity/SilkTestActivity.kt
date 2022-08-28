package cc.imorning.chat.activity

import android.os.Bundle
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
import cc.imorning.silk.SilkCoder

class SilkTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatTheme {
                var result by remember { mutableStateOf("") }
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                result = SilkCoder.getVersion()
                            }
                        ) {
                            Text(text = "Get Version")
                        }
                        Button(onClick = {

                        }) {
                            Text(text = "decode")
                        }
                    }
                    Text(text = result)

                }
            }
        }
    }

    companion object {
        private const val TAG = "SilkTestActivity"
    }
}