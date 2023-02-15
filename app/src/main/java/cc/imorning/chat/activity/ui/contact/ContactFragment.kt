package cc.imorning.chat.activity.ui.contact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cc.imorning.chat.R
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.activity.SearchActivity
import cc.imorning.chat.compontens.NewRosterItem
import cc.imorning.chat.compontens.RosterItem
import cc.imorning.chat.compontens.SearchBar
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.common.CommonApp
import cc.imorning.database.db.DataDB
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val TAG = "ContactFragment"

@OptIn(ExperimentalMaterial3Api::class)
class ContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels {
        val db = DataDB.getInstance(
            CommonApp.getContext(),
            CommonApp.xmppTcpConnection!!.user.asEntityBareJidString()
        )
        ContactViewModelFactory(db.databaseDao())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.updateRosterView()
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
                        topBar = { TopBar() },
                        content = {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                ContactScreen(viewModel = viewModel)
                            }
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.title_contact),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            TextButton(
                onClick = {
                    val searchActivity = Intent(context, SearchActivity::class.java)
                    context.startActivity(searchActivity)
                }) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = stringResource(id = R.string.desc_add_contact),
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactScreen(viewModel: ContactViewModel) {

    val context = LocalContext.current

    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val rosters = viewModel.rosters.collectAsState()
    val newRoster = viewModel.newRosters.collectAsState()

    var showEditorDialog by remember { mutableStateOf(false) }
    var newJid by remember { mutableStateOf("") }
    if (showEditorDialog) {
        ComposeDialogUtils.EditorDialog(
            title = stringResource(R.string.add_roster_nick),
            hint = RosterAction.getNickName(newJid),
            positiveButton = stringResource(id = R.string.ok),
            negativeButton = stringResource(id = R.string.cancel),
            onConfirm = {
                showEditorDialog = false
                viewModel.acceptSubscribe(newJid, it)
            },
            onCancel = { showEditorDialog = false }
        )
    }
    Column {
        // search bar
        SearchBar(modifier = Modifier.fillMaxWidth())
        SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true,
                    shape = MaterialTheme.shapes.extraLarge,
                )
            },
            onRefresh = {
                viewModel.remove()
                viewModel.getRostersFromServer()
                viewModel.updateRosterView()
            }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    if (newRoster.value.isNotEmpty()) {
                        Column {
                            newRoster.value.forEach {
                                NewRosterItem(
                                    roster = it,
                                    onAccept = {
                                        showEditorDialog = true
                                        newJid = it.jid
                                    },
                                    onReject = {
                                        viewModel.rejectSubscribe(it.jid)
                                    }
                                )
                            }
                        }
                    }
                    if (rosters.value.isNotEmpty()) {
                        Column {
                            rosters.value.forEach { roster ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = {
                                                val dialog = androidx.appcompat.app.AlertDialog
                                                    .Builder(context)
                                                    .setTitle("个人信息")
                                                    .setMessage(RosterAction.getNickName(roster.jid))
                                                    .setPositiveButton(
                                                        context.getText(R.string.ok),
                                                        null
                                                    )
                                                    .create()
                                                dialog.show()
                                            }),
                                ) {
                                    RosterItem(roster = roster)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}