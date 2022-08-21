package cc.imorning.chat.activity.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cc.imorning.chat.R
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.view.ui.ComposeDialogUtils
import cc.imorning.common.CommonApp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

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
@Preview(
    showBackground = true
)
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
        modifier = Modifier.fillMaxSize()
    ) {

        val avatarPath = profileViewModel.avatarPath.observeAsState().value
        val nickName = profileViewModel.nickname.observeAsState()
        val phoneNumber = profileViewModel.phoneNumber.observeAsState()
        val jidString = profileViewModel.jidString.observeAsState()
        val status = profileViewModel.status.observeAsState()

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
            SubcomposeAsyncImage(
                model = avatarPath,
                contentDescription = "avatar",
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(72.dp)
                    ),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center,
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }
                    is AsyncImagePainter.State.Error -> {
                        Log.w(TAG, "on error for get avatar: $avatarPath")
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
                        Toast.makeText(context, phoneNumber.value, Toast.LENGTH_SHORT).show()
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
        Text(
            text = "${status.value}",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(2.dp)
                .background(color = Color.Cyan)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
                .clickable { showAboutDialog = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_code),
                contentDescription = "关于",
                modifier = Modifier
                    .size(36.dp, 36.dp)
                    .background(
                        color = Color.Blue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(36.dp)
                    )
            )
            Text(
                text = "关于",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
                .clickable { showBuildingDialog = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bug_report),
                contentDescription = "报告问题",
                modifier = Modifier
                    .size(36.dp, 36.dp)
                    .background(
                        color = Color.Blue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(36.dp)
                    )
            )
            Text(
                text = "报告问题",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp)
                .clickable { CommonApp.exitApp(0) }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_exit_to_app),
                contentDescription = "退出",
                modifier = Modifier
                    .size(36.dp, 36.dp)
                    .background(
                        color = Color.Blue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(36.dp)
                    )
            )
            Text(
                text = "退出",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}