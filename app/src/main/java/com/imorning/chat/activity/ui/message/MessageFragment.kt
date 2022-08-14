package com.imorning.chat.activity.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.imorning.chat.R
import com.imorning.chat.ui.theme.MainTheme

class MessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val messageViewModel =
            ViewModelProvider(this)[MessageViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                MainTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MessageScreen()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MessageFragment"
    }
}

@Preview
@Composable
fun MessageScreen() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(modifier = Modifier.fillMaxWidth(),
            onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_default_avatar),
                contentDescription = null
            )
            Text(text = "在线")
        }
    }
}