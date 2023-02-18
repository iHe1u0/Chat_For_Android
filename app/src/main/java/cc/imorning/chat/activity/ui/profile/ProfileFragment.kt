package cc.imorning.chat.activity.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
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
import cc.imorning.chat.compontens.GlideAvatar
import cc.imorning.chat.ui.theme.ChatTheme
import cc.imorning.chat.ui.view.ComposeDialogUtils
import cc.imorning.chat.ui.view.ToastUtils
import cc.imorning.common.constant.Config
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment"

@OptIn(ExperimentalMaterial3Api::class)
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MainScope().launch(Dispatchers.IO) {
            viewModel.updateUserConfigure()
        }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val cropLauncher =
            rememberLauncherForActivityResult(CropImageContract()) { result ->
                if (result.isSuccessful) {
                    val newAvatarFilePath = result.getUriFilePath(context)
                    if (newAvatarFilePath != null) {
                        profileViewModel.updateAvatar(context, newAvatarFilePath)
                    }
                }
            }
        val uriHandler = LocalUriHandler.current

        val avatarPath = profileViewModel.avatarPath.collectAsState().value
        val nickName = profileViewModel.nickname.collectAsState()
        val phoneNumber = profileViewModel.phoneNumber.collectAsState()
        val jidString = profileViewModel.jidString.collectAsState()

        var showBuildingDialog by remember { mutableStateOf(false) }
        if (showBuildingDialog) {
            ComposeDialogUtils.FunctionalityNotAvailablePopup { showBuildingDialog = false }
        }

        var showAboutDialog by remember { mutableStateOf(false) }
        if (showAboutDialog) {
            ComposeDialogUtils.AboutDialog { showAboutDialog = false }
        }

        var showExitDialog by remember { mutableStateOf(false) }
        if (showExitDialog) {
            ComposeDialogUtils.InfoAlertDialog(
                message = stringResource(R.string.exit_message),
                confirmTitle = stringResource(id = R.string.ok),
                dismissTitle = stringResource(id = R.string.cancel),
                onConfirm = { App.exitApp(0) },
                onDismiss = { showExitDialog = false }
            )
        }

        var showSetNickNameDialog by remember { mutableStateOf(false) }
        var showSetPhoneNumberDialog by remember { mutableStateOf(false) }
        if (showSetNickNameDialog) {
            ComposeDialogUtils.EditorDialog(
                title = stringResource(R.string.new_nick_name),
                hint = nickName.value,
                positiveButton = stringResource(id = R.string.ok),
                negativeButton = stringResource(id = R.string.cancel),
                onConfirm = { newName ->
                    if (newName.length > Config.NAME_MAX_LENGTH) {
                        ToastUtils.showMessage(
                            context,
                            context.getString(R.string.name_too_long)
                                .format(Config.NAME_MAX_LENGTH)
                        )
                    }
                    if (newName.isEmpty()) {
                        ToastUtils.showMessage(
                            context,
                            context.getString(R.string.input_is_empty)
                        )
                    } else {
                        showSetNickNameDialog = false
                        profileViewModel.updateNickName(newName)
                    }
                },
                onCancel = { showSetNickNameDialog = false }
            )
        }
        if (showSetPhoneNumberDialog) {
            ComposeDialogUtils.EditorDialog(
                title = stringResource(R.string.change_phone_number),
                hint = phoneNumber.value,
                positiveButton = stringResource(id = R.string.ok),
                negativeButton = stringResource(id = R.string.cancel),
                onConfirm = {
                    showSetPhoneNumberDialog = false
                    profileViewModel.updatePhoneNumber(it)
                },
                onCancel = { showSetPhoneNumberDialog = false }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            var selectedImageUri by remember {
                mutableStateOf<Uri?>(null)
            }
            val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    selectedImageUri = uri
                    cropLauncher.launch(
                        CropImageContractOptions(
                            uri = selectedImageUri,
                            cropImageOptions = CropImageOptions(),
                        ),
                    )
                }
            )

            GlideAvatar(avatarPath = avatarPath) {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = AnnotatedString(
                        text = nickName.value.ifEmpty { stringResource(R.string.set_nick_name) },
                        spanStyle = SpanStyle(
                            color = Color.Blue,
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                showSetNickNameDialog = true
                            }
                        )
                )
                ClickableText(
                    text = AnnotatedString(
                        text = phoneNumber.value.ifEmpty { stringResource(R.string.set_phone_number) },
                        spanStyle = SpanStyle(
                            color = Color.Blue,
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showSetPhoneNumberDialog = true
                    },
                )
                ClickableText(
                    text = AnnotatedString(
                        text = jidString.value,
                        spanStyle = SpanStyle(
                            color = Color.Blue,
                        )
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        Toast.makeText(context, jidString.value, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(color = Color.Blue.copy(0.04f))
        )
        MenuItem(
            icon = R.drawable.ic_code,
            title = stringResource(R.string.about),
            action = {
                showAboutDialog = true
            }
        )
        MenuItem(
            icon = R.drawable.ic_bug_report,
            title = stringResource(R.string.feedback),
            action = {
                uriHandler.openUri(Config.ISSUES_URL)
            }
        )
        MenuItem(
            icon = R.drawable.ic_exit_to_app,
            title = stringResource(id = R.string.close),
            action = { showExitDialog = true }
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