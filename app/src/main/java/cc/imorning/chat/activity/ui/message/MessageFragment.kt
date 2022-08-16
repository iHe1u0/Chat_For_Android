package cc.imorning.chat.activity.ui.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.R
import cc.imorning.chat.activity.ChatActivity
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.common.constant.Config
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val TAG = "MessageFragment"

@OptIn(ExperimentalMaterial3Api::class)
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
                ChatTheme {
                    Scaffold(
                        topBar = {
                            TopBar()
                        },
                        content = {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                MessageScreen(messageViewModel)
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton()
                        }
                    )
                }
            }
        }
    }

    companion object
}

@Composable
fun MessageScreen(viewModel: MessageViewModel) {

    val context = LocalContext.current
    val messages = viewModel.messages.observeAsState()
    val isRefreshing = viewModel.isRefreshing.collectAsState()

    Column {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    // Pass the SwipeRefreshState + trigger through
                    state = state,
                    refreshTriggerDistance = trigger,
                    // Enable the scale animation
                    scale = true,
                    shape = MaterialTheme.shapes.small,
                )
            },
            onRefresh = {
                viewModel.refresh()
            }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (!messages.value.isNullOrEmpty()) {
                    items(messages.value!!) { message ->
                        MessageItem(message)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val chatActivity = Intent(context, ChatActivity::class.java)
                chatActivity.putExtra(Config.Action.START_CHAT_JID, message)
                context.startActivity(chatActivity)
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_default_avatar),
            contentDescription = message,
            alignment = Alignment.Center
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true
)
@Composable
fun TopBar() {
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    CenterAlignedTopAppBar(
        title = {
            TextButton(
                onClick = { showBuildingDialog = true },
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.ic_default_avatar),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                    )
                    Text(text = "在线")
                }
            }
        }
    )
}

@Composable
fun FloatingActionButton() {
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    FloatingActionButton(
        onClick = {
            showBuildingDialog = true
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = ""
        )
    }
}

