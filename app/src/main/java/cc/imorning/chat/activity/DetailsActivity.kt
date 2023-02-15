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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import cc.imorning.chat.utils.AvatarUtils
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

    @OptIn(ExperimentalComposeUiApi::class)
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
                        Text(text = uiState.value.status().name, maxLines = 1)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.Message, contentDescription = "")
                        Text(text = "发送消息")
                    }
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Call, contentDescription = "")
                    }
                    FilledIconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.VideoCall, contentDescription = "")
                    }
                }
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                ProfileProperty(label = "label", value = "value")
                if (!uiState.value.isMe()) {
                    FilledIconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteForever, contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileError() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(stringResource(R.string.profile_error))
    }
}