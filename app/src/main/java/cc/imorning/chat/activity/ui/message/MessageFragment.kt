package cc.imorning.chat.activity.ui.message

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.activity.SearchActivity
import cc.imorning.chat.compontens.RecentMessageItem
import cc.imorning.chat.network.ConnectionLiveData
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.CommonApp
import cc.imorning.database.db.RecentDB
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val TAG = "MessageFragment"

@OptIn(ExperimentalMaterial3Api::class)
class MessageFragment : Fragment() {

    private val messageViewModel: MessageViewModel by activityViewModels {
        val db = RecentDB.getInstance(
            CommonApp.getContext(),
            CommonApp.xmppTcpConnection.user.asEntityBareJidString()
        )
        MessageViewModelFactory(db.recentDatabaseDao())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        messageViewModel.updateView(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ChatTheme {
                    Scaffold(
                        topBar = {
                            TopBar(messageViewModel)
                        },
                        content = { paddingValues ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
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

    override fun onDestroy() {
        messageViewModel.removeListener()
        super.onDestroy()
    }
}

@Composable
fun MessageScreen(viewModel: MessageViewModel) {

    val context = LocalContext.current

    val messages = viewModel.messages.observeAsState()
    val isRefreshing = viewModel.isRefreshing.collectAsState()

    val connectionStatus = ConnectionLiveData(context).observeAsState().value

    Column {
        if ((connectionStatus != null) && (!connectionStatus)) {
            viewModel.removeListener()
            Text(
                text = stringResource(id = R.string.network_is_unavailable),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Red),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        } else if ((connectionStatus != null) && connectionStatus) {
            viewModel.addListener()
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    shape = MaterialTheme.shapes.extraLarge,
                )
            },
            onRefresh = {
                viewModel.updateStatus()
                viewModel.updateView(true)
            }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // if recent messages is not null, then show them.
                if (messages.value != null && messages.value!!.size > 0) {
                    val lastMessage: MutableSet<String> = mutableSetOf()
                    items(messages.value!!) { message ->
                        // sort by same message.sender
                        if (!lastMessage.contains(message.user)) {
                            RecentMessageItem(message)
                            lastMessage.add(message.user)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(messageViewModel: MessageViewModel) {
    val context = LocalContext.current
    var showBuildingDialog by remember { mutableStateOf(false) }
    if (showBuildingDialog) {
        ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
    }
    val avatarPath = messageViewModel.avatarPath.observeAsState()
    val status = messageViewModel.status.collectAsState()

    CenterAlignedTopAppBar(
        title = {
            TextButton(
                onClick = {
                    showModeDialog(context, messageViewModel)
                },
            ) {
                SubcomposeAsyncImage(
                    model = avatarPath.value,
                    contentDescription = stringResource(id = R.string.desc_contact_item_avatar),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    alignment = Alignment.Center,
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator()
                        }
                        is AsyncImagePainter.State.Error -> {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                        }
                        is AsyncImagePainter.State.Empty -> {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                Text(
                    text = status.value,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    )
}

fun showModeDialog(context: Context, messageViewModel: MessageViewModel) {
    val connection = App.getTCPConnection()
    var currentIndex =
        StatusHelper.getIndex(RosterAction.getRosterStatus(connection.user.asEntityBareJidString()))
    val array = context.resources.getStringArray(cc.imorning.common.R.array.mode)
    val modeDialog = AlertDialog.Builder(context)
    modeDialog.setTitle(context.getString(R.string.change_mode))
    modeDialog.setPositiveButton(context.getString(R.string.cancel), null)
    modeDialog.setSingleChoiceItems(
        array,
        currentIndex
    ) { _: DialogInterface, index: Int ->
        currentIndex = index
    }
    modeDialog.setPositiveButton(context.getString(R.string.ok)) { _: DialogInterface, _: Int ->
        RosterAction.updateMode(StatusHelper.getMode(currentIndex))
        messageViewModel.updateUser()
    }
    modeDialog.create().show()
}

@Composable
fun FloatingActionButton() {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            val searchActivity = Intent(context, SearchActivity::class.java)
            context.startActivity(searchActivity)
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.search_contact)
        )
    }
}

