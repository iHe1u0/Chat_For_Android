package cc.imorning.chat.action.message

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.entity.MessageBody
import cc.imorning.common.entity.MessageEntity
import cc.imorning.common.utils.Base64Utils
import cc.imorning.common.utils.MD5Utils
import cc.imorning.common.utils.RingUtils
import cc.imorning.database.db.RecentDB
import cc.imorning.database.table.RecentMessageTable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.joda.time.DateTime
import java.io.File

private const val TAG = "MessageHelper"

object MessageHelper {

    private val databaseDao =
        RecentDB.getInstance(
            CommonApp.getContext(),
            App.getTCPConnection().user.asEntityBareJidString()
        ).recentDatabaseDao()
    private val connection = App.getTCPConnection()

    fun processMessage(
        message: Message,
        from: String? = null,
        chat: Chat? = null
    ) {
        if (message.body == null) {
            return
        }
        RingUtils.playNewMessage(
            context = CommonApp.getContext(),
            type = message.type
        )
        when (message.type) {
            Message.Type.chat -> {
                processChatMessage(message, from, chat)
            }
            Message.Type.groupchat -> {

            }
            Message.Type.headline -> {

            }
            Message.Type.normal -> {

            }
            Message.Type.error -> {

            }
            else -> {}
        }
    }

    /**
     * build a message entity with data
     */
    fun buildMsg(
        receiver: String,
        plainText: String = "",
        picFile: File? = null,
        audioFile: File? = null,
        videoFile: File? = null,
        fileFile: File? = null,
        action: String? = "",
    ): String {

        val encodeImage = picFile?.let { Base64Utils.encodeFile(it) }
        val encodeAudio = audioFile?.let { Base64Utils.encodeFile(it) }
        val encodeFile = fileFile?.let { Base64Utils.encodeFile(it) }
        val encodeVideo = videoFile?.let { Base64Utils.encodeFile(it) }
        val encodeAction = action?.let { Base64Utils.encodeString(it) }

        val messageBody = MessageBody(
            text = plainText,
            image = encodeImage,
            audio = encodeAudio,
            video = encodeVideo,
            file = encodeFile,
            action = encodeAction
        )
        val messageEntity = MessageEntity(
            sender = connection.user.asUnescapedString(),
            receiver = receiver,
            messageBody = messageBody,
        )
        return Base64Utils.encodeString(Gson().toJson(messageEntity, MessageEntity::class.java))
    }

    /**
     * decode base64 msg to a Message
     *
     * @return an MessageEntity if decode success, or null if failed.
     *
     */
    fun decodeMsg(msg: String): MessageEntity? {
        val sourceString = Base64Utils.decodeString(msg)
        return try {
            Gson().fromJson(sourceString, MessageEntity::class.java)
        } catch (e: JsonSyntaxException) {
            if (BuildConfig.DEBUG) {
                Log.e(
                    TAG,
                    "decode msg failed cause: ${e.localizedMessage}"
                )
            }
            null
        }
    }

    private fun processChatMessage(message: Message, from: String?, chat: Chat?) {
        val fromString = from ?: message.from.asUnescapedString()
        val receiver = connection.user.asUnescapedString()
        val nickName = cc.imorning.chat.action.UserAction.getNickName(fromString)
        val dateTime: DateTime = DateTime.now()
        val id = MD5Utils.digest(receiver.plus(from))!!
        val recentMessage = RecentMessageTable(
            id = id,
            nickName = nickName,
            sender = fromString,
            receiver = receiver,
            lastMessage = message.body,
            lastMessageTime = dateTime,
        )
        // update database
        MainScope().launch(Dispatchers.IO) { databaseDao.insertRecentMessage(recentMessage) }
    }

    private fun processGroupChatMessage(message: Message) {

    }

    private fun processHeadlineMessage(message: Message) {

    }

    private fun processNormalMessage(message: Message) {

    }

    private fun processErrorMessage(message: Message) {

    }

}