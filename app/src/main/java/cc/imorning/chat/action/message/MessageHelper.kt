package cc.imorning.chat.action.message

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.action.RosterAction
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.Base64Utils
import cc.imorning.common.utils.RingUtils
import cc.imorning.database.db.MessageDB
import cc.imorning.database.db.RecentDB
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import cc.imorning.database.entity.RecentMessageEntity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Message.Type
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.File

private const val TAG = "MessageHelper"

object MessageHelper {

    private val recentDatabaseDao =
        RecentDB.getInstance(
            CommonApp.getContext(),
            App.getTCPConnection().user.asEntityBareJidString()
        ).recentDatabaseDao()

    private val connection = App.getTCPConnection()

    fun processMessage(
        messageEntity: MessageEntity,
        chat: Chat? = null
    ) {
        RingUtils.playNewMessage(
            context = CommonApp.getContext(),
            type = messageEntity.messageType
        )
        when (messageEntity.messageType) {
            Type.chat -> {
                processChatMessage(messageEntity, chat)
            }
            Type.groupchat -> {

            }
            Type.headline -> {

            }
            Type.normal -> {

            }
            Type.error -> {

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
            sender = connection.user.asEntityBareJidString(),
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

    /**
     * Process received message,insert message into database
     */
    private fun processChatMessage(messageEntity: MessageEntity, chat: Chat?) {
        val fromString = messageEntity.sender
        val nickName = RosterAction.getNickName(fromString)
        with(messageEntity) {
            insertRecentMessage(
                sender = receiver,
                nickName = nickName,
                messageBody = messageBody.text,
                messageType = messageType,
                dateTime = DateTime(sendTime).withZone(DateTimeZone.getDefault())
            )
            // insert into message database
            insertMessage(messageEntity)
        }
    }

    fun insertRecentMessage(
        sender: String,
        nickName: String,
        messageBody: String,
        messageType: Type,
        dateTime: DateTime
    ) {
        val recentMessage = RecentMessageEntity(
            sender = sender,
            nickName = nickName,
            type = messageType,
            message = messageBody,
            time = dateTime,
            isShow = true
        )
        // val messageEntity = MessageEntity()
        // update recent database
        MainScope().launch(Dispatchers.IO) {
            recentDatabaseDao.insertOrReplaceMessage(recentMessage)
        }
    }

    fun insertMessage(messageEntity: MessageEntity) {
        // if connection is not connect or authenticated,then return
        if (connection.isConnected && connection.isAuthenticated) {
            val sender = messageEntity.sender
            val receiver = messageEntity.receiver
             val messageDatabaseDao = MessageDB.getInstance(
                 context = CommonApp.getContext(),
                 user = sender,
                 receiver = receiver
             ).databaseDao()
            messageEntity.let {
                Log.d(TAG, "insertMessage:[$sender]>[$receiver]: ${it.messageBody.text}")
            }
            MainScope().launch(Dispatchers.IO) {

            }
        }
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