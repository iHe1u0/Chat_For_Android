package cc.imorning.chat.activity.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.compontens.Avatar
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.common.CommonApp

private const val TAG = "ProfileFragment"

@OptIn(ExperimentalMaterial3Api::class)
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // val userId = arguments?.getString("userId")
        viewModel.getUserInfo()
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
                                ProfileScreen(viewModel)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.title_profile),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    )
}

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val avatarPath = profileViewModel.avatarPath.observeAsState().value
        val nickName = profileViewModel.nickname.observeAsState()
        val phoneNumber = profileViewModel.phoneNumber.observeAsState()
        val jidString = profileViewModel.jidString.observeAsState()

        var showBuildingDialog by remember { mutableStateOf(false) }
        if (showBuildingDialog) {
            ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
        }

        var showAboutDialog by remember { mutableStateOf(false) }
        if (showAboutDialog) {
            ComposeDialogUtils.ShowAbout { showAboutDialog = false }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Avatar(avatarPath = avatarPath.orEmpty())
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "${nickName.value}",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium,
                )
                ClickableText(
                    text = AnnotatedString(
                        text = "${phoneNumber.value}",
                        spanStyle = SpanStyle(
                            color = Color.Blue,
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        Toast.makeText(context, "${phoneNumber.value}", Toast.LENGTH_SHORT).show()
                    }
                )
                ClickableText(
                    text = AnnotatedString(
                        text = "${jidString.value}",
                        spanStyle = SpanStyle(
                            color = Color.Blue,
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        Toast.makeText(context, "${jidString.value}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
//        Text(
//            text = "${status.value}",
//            modifier = Modifier.fillMaxWidth(),
//            style = MaterialTheme.typography.titleSmall,
//            textAlign = TextAlign.Center
//        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(color = Color.Blue.copy(0.04f))
        )
        MenuItem(
            icon = R.drawable.ic_code,
            title = "关于",
            action = {
                showAboutDialog = true
            }
        )
        MenuItem(
            icon = R.drawable.ic_bug_report,
            title = "反馈问题",
            action = {
                showBuildingDialog = true
            }
        )
        MenuItem(
            icon = R.drawable.ic_exit_to_app,
            title = stringResource(id = R.string.close),
            action = {
                App.exitApp(0)
            }
        )
    }
}

@Composable
fun MenuItem(
    @DrawableRes icon: Int,
    title: String,
    action: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp)
            .clickable { /*CommonApp.exitApp(0)*/ action() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier
                .size(36.dp, 36.dp)
                .background(
                    color = Color.Blue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(36.dp)
                )
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}