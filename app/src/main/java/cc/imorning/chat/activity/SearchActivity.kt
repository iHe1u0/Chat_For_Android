package cc.imorning.chat.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cc.imorning.chat.compontens.Avatar
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.chat.viewmodel.SearchViewModel
import cc.imorning.chat.utils.AvatarUtils

class SearchActivity : BaseActivity() {

    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(viewModel = searchViewModel)
        }
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    ChatTheme {
        Scaffold(
            topBar = {},
            content = { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContentScreen(viewModel = viewModel)
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContentScreen(viewModel: SearchViewModel) {

    val context = LocalContext.current
    val key = viewModel.key.observeAsState()
    val selectedUser = viewModel.selectedUserJidString.observeAsState()
    val userList = viewModel.result.observeAsState()
    val showWaitingDialog = viewModel.shouldShowWaitingDialog.observeAsState()
    if (showWaitingDialog.value == true) {
        ComposeDialogUtils.ShowWaitingDialog(title = "搜索 [${key.value}]")
    }
    val shouldShowVCard = viewModel.shouldShowVCardDialog.observeAsState()
    if (shouldShowVCard.value == true && !selectedUser.value.isNullOrBlank()) {
        ComposeDialogUtils.ShowVCard(jidString = selectedUser.value!!) {
            viewModel.closeVCard()
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = key.value!!,
            onValueChange = { viewModel.setKey(it.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            singleLine = true,
            maxLines = 1,
            label = { Text(text = "账号、昵称") },
            trailingIcon = {
                Button(onClick = {
                    if (key.value!!.isBlank()) {
                        Toast.makeText(context, "输入不能为空", Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.search()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "搜索",
                    )
                    Text(text = "搜一搜")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        )
        if (userList.value == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "",
                    Modifier.size(128.dp)
                )
            }
        } else if (userList.value!!.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "无结果",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        } else if (userList.value!!.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                userList.value?.let { users ->
                    items(items = users,
                        key = { user ->
                            user.jid
                        }
                    ) { user ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Green.copy(0.1f))
                            .animateItemPlacement()
                            .clickable {
                                viewModel.showVCard(user)
                            }
                        ) {
                            Avatar(
                                avatarPath = AvatarUtils.instance.getAvatarPath(user.jid)
                            )
                            Text(
                                text = "${user.username} [${user.jid}]",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 8.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}