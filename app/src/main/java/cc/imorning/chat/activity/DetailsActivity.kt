package cc.imorning.chat.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.imorning.chat.R
import cc.imorning.chat.compontens.Avatar
import cc.imorning.chat.compontens.ProfileProperty
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.theme.ChatTypography
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.chat.viewmodel.DetailsViewModel
import cc.imorning.chat.viewmodel.DetailsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DetailsActivity : BaseActivity() {

    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModelFactory()
    }

    companion object {
        private const val TAG = "DetailsActivity"
        const val KEY_UID = "user_jid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra(KEY_UID)
        if (uid != null) {
            viewModel.jid.value = uid
            MainScope().launch(Dispatchers.IO) {
                viewModel.init()
            }
        }
        setContent {
            if ((uid == null) || uid.isEmpty()) {
                ProfileError()
            } else {
                DetailsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(viewModel: DetailsViewModel) {

    val context = LocalContext.current
    val jid = viewModel.jid.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    ChatTheme {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 12.dp),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.information),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as Activity).finish() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            val scrollState = rememberScrollState()

            var showRemoveDialog by remember { mutableStateOf(false) }
            if (showRemoveDialog) {
                ComposeDialogUtils.InfoAlertDialog(
                    message = stringResource(R.string.message_delete_roster),
                    confirmTitle = stringResource(id = R.string.ok),
                    dismissTitle = stringResource(id = R.string.cancel),
                    onConfirm = {
                        viewModel.delete()
                        (context as Activity).finish()
                    },
                    onDismiss = { showRemoveDialog = false }
                )
            }

            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(avatarPath = AvatarUtils.instance.getAvatarPath(jidString = jid.value)) {}
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = uiState.value.nickName(),
                            maxLines = 1,
                            style = ChatTypography.titleMedium
                        )
                        Text(text = StatusHelper(uiState.value.status()).toString(), maxLines = 1)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Message,
                            contentDescription = stringResource(R.string.send_message)
                        )
                        Text(text = stringResource(R.string.send_message))
                    }
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Call, contentDescription = "audio call")
                    }
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.VideoCall,
                            contentDescription = "video call"
                        )
                    }
                }
                ProfileProperty(label = "JID", value = uiState.value.jid)
                ProfileProperty(
                    label = stringResource(R.string.nick_name),
                    value = uiState.value.nickName()
                )
                ProfileProperty(
                    label = stringResource(R.string.phone_number),
                    value = uiState.value.phone()
                )
                ProfileProperty(
                    label = stringResource(R.string.email),
                    value = uiState.value.email()
                )

                if (!uiState.value.isMe()) {
                    OutlinedIconButton(
                        onClick = { showRemoveDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = stringResource(R.string.delete),
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileError() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.profile_error))
    }
}