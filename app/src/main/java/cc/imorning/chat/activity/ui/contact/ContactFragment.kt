package cc.imorning.chat.activity.ui.contact

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.compontens.ContactItem
import cc.imorning.chat.compontens.SearchBar
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val TAG = "ContactFragment"

@OptIn(ExperimentalMaterial3Api::class)
class ContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as App).appDatabase.appDatabaseDao()
        )
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
            Text(
                text = stringResource(id = R.string.title_contact),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            TextButton(
                onClick = {
                    showBuildingDialog = true
                }) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = stringResource(id = R.string.desc_add_contact),
                )
            }
        },
    )
}

@Composable
fun ContactScreen(viewModel: ContactViewModel) {

    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val contacts = viewModel.contacts.collectAsState()

    Column {
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
            onRefresh = { viewModel.refresh() }) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (contacts.value.isNotEmpty()) {
                    item {
                        Column {
                            contacts.value.forEach { contact ->
                                ContactItem(contact = contact)
                            }
                        }
                    }
                }
            }
        }
    }
}