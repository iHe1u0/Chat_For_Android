package cc.imorning.chat.compontens.conversation

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.VoiceChat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.action.message.MessageManager
import cc.imorning.chat.activity.DetailsActivity
import cc.imorning.chat.file.FileTransferUtils
import cc.imorning.chat.ui.view.ComposeDialogUtils.FunctionalityNotAvailablePopup
import cc.imorning.chat.utils.AvatarUtils
import cc.imorning.chat.utils.StatusHelper
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.FileUtils
import cc.imorning.common.utils.TimeUtils
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jivesoftware.smack.packet.Presence
import org.joda.time.DateTime
import java.io.File

private const val TAG = "Conversation"

private val connection = CommonApp.xmppTcpConnection

/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationContent(
    chatUid: String,
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    val context = LocalContext.current
    val authorMe = CommonApp.vCard?.from

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    var picList = mutableListOf<File>()

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                MessagesUI(
                    messages = uiState.messages,
                    navigateToProfile = navigateToProfile,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState
                )
                UserInput(
                    onMessageSent = { textMessage ->
                        // Send message
                        if (textMessage.isNotEmpty()) {
                            val message = MessageEntity(
                                sender = connection.user.asEntityBareJidString(),
                                receiver = chatUid,
                                messageBody = MessageBody(text = textMessage)
                            )
                            uiState.addMessageUI(
                                MessageEntity(
                                    sender = authorMe.toString(),
                                    receiver = chatUid,
                                    messageBody = MessageBody(text = textMessage)
                                )
                            )
                            MainScope().launch(Dispatchers.IO) {
                                val gson = Gson()
                                MessageManager.sendMessage(chatUid, message = gson.toJson(message))
                            }
                        }
                        if (picList.isNotEmpty()) {
                            picList.forEach { image ->
                                uiState.addMessageUI(
                                    MessageEntity(
                                        sender = authorMe.toString(),
                                        receiver = chatUid,
                                        messageBody = MessageBody(
                                            image = image.absolutePath
                                        )
                                    )
                                )
                                val fileRec = File(FileUtils.getChatFileFolder(context), image.name)
                                val message =
                                    MessageEntity(
                                        sender = connection.user.asEntityBareJidString(),
                                        receiver = chatUid,
                                        messageBody = MessageBody(
                                            text = image.absolutePath,
                                            image = fileRec.absolutePath
                                        )
                                    )
                                MainScope().launch(Dispatchers.IO) {
                                    FileTransferUtils.sendFile(
                                        connection,
                                        chatUid,
                                        image,
                                        FileTransferUtils.FileType.STICKER.name
                                    )
                                    delay(1500)
                                    val gson = Gson()
                                    MessageManager.sendMessage(
                                        chatUid,
                                        message = gson.toJson(message)
                                    )
                                }
                            }
                        }
                        picList.removeAll(picList)
                    },
                    onPictureSelected = {
                        picList = it
                    },
                    onSentFile = { file ->
                        FileTransferUtils.sendFile(
                            connection,
                            chatUid,
                            file = file,
                            FileTransferUtils.FileType.FILE.name
                        )
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
                    // navigation bar, and on-screen keyboard (IME)
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding(),
                )
            }
            // Channel name bar floats above the messages
            ChannelNameBar(
                nickName = uiState.nickName,
                friendStatus = uiState.friendStatus,
                jid = chatUid,
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
                // Use statusBarsPadding() to move the app bar content below the status bar
                modifier = Modifier.statusBarsPadding(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    nickName: String,
    friendStatus: Presence.Mode,
    jid: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { }
) {
    val context = LocalContext.current
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }
    ChatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                Text(
                    text = nickName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = StatusHelper(friendStatus).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Call icon
            Icon(
                imageVector = Icons.Outlined.VoiceChat,
                contentDescription = "通话",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
            )
            // Info icon
            Icon(
                imageVector = Icons.Outlined.Info,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {
                        val intent = Intent(context, DetailsActivity::class.java)
                        intent.putExtra(DetailsActivity.KEY_UID, jid)
                        context.startActivity(intent)
                    })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = "信息"
            )
        }
    )
}

@Composable
fun MessagesUI(
    messages: List<MessageEntity>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            // Add content padding so that the content can be scrolled (y-axis)
            // below the status bar + app bar
            contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp))
                .asPaddingValues(),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = messages.size) { index ->
                val prevAuthor = messages.getOrNull(index - 1)?.sender
                val nextAuthor = messages.getOrNull(index + 1)?.sender
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.sender
                val isLastMessageByAuthor = nextAuthor != content.sender
                MessageItemUI(
                    onAuthorClick = { uid ->
                        navigateToProfile(uid)
                    },
                    msg = content,
                    isUserMe = content.sender == App.user,
                    isFirstMessageByAuthor = isFirstMessageByAuthor,
                    isLastMessageByAuthor = isLastMessageByAuthor,
                )
            }
        }
        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MessageItemUI(
    onAuthorClick: (String) -> Unit,
    msg: MessageEntity,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier

    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            AsyncImage(
                model = AvatarUtils.getAvatarPath(user = msg.sender),
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.sender) })
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                contentDescription = null,
            )
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(74.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: MessageEntity,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            AuthorNameTimestamp(sendTime = msg.sendTime, sender = msg.sender)
        }
        ChatItemBubble(
            message = msg.messageBody,
            isUserMe = isUserMe,
            authorClicked = authorClicked
        )
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

/**
 * avatar and timestamp
 */
@Composable
private fun AuthorNameTimestamp(sendTime: Long, sender: String) {
    // Combine author and timestamp for a11y.
    val format = if (TimeUtils.isToday(sendTime)) {
        "HH:mm:ss"
    } else {
        "yyyy-MM-dd HH:mm:ss"
    }
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = RosterAction.getNickName(sender),
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = DateTime(sendTime).toString(format),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(6.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun DayHeader(dateTime: DateTime) {
    val title = if (dateTime.isEqualNow) {
        "今天"
    } else {
        TimeUtils.getFormatDateTime(dateTime)
        // DateTime(dateTime).toString("yyyy-MM-dd")
    }
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

/**
 * chat message item UI
 */
@Composable
fun ChatItemBubble(
    message: MessageBody,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
            if (message.text.isNotEmpty()) {
                ClickableMessage(
                    message = message.text,
                    isUserMe = isUserMe,
                    authorClicked = authorClicked,
                )
            }
            if (!message.image.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                AsyncImage(
                    model = message.image,
                    modifier = Modifier.background(backgroundBubbleColor),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ClickableMessage(
    message: String,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message,
        primary = isUserMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

private val JumpToBottomThreshold = 56.dp

private fun ScrollState.atBottom(): Boolean = value == 0
