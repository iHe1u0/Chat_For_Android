package cc.imorning.chat.activity.ui.contact

import android.content.Intent
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.activity.SearchActivity
import cc.imorning.chat.compontens.ContactItem
import cc.imorning.chat.compontens.NewUserCard
import cc.imorning.chat.compontens.SearchBar
import cc.imorning.chat.ui.theme.ChatTheme
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

@Composable
fun ContactScreen(viewModel: ContactViewModel) {

    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val contacts = viewModel.contacts.collectAsState()

    Column {
        // search bar
        SearchBar(modifier = Modifier.fillMaxWidth())
        // Show this content if have new friend
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Column {
                    contacts.value.forEach { newContact ->
                        NewUserCard(
                            contact = newContact,
                            onAccept = {
                                Log.i(TAG, "ContactScreen: accept")
                            },
                            onReject = {
                                Log.i(TAG, "ContactScreen: reject")
                            }
                        )
                    }
                }
            }
        }
    }

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