package cc.imorning.chat.action.message

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.Base64Utils
import cc.imorning.common.utils.RingUtils
import cc.imorning.database.db.RecentDB
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import cc.imorning.database.entity.MessageTable
import cc.imorning.database.entity.RecentMessageEntity
import cc.imorning.database.utils.MessageDatabaseHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Message.Type
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
        with(messageEntity) {
            val nickName = RosterAction.getNickName(sender)
            insertRecentMessage(
                user = sender,
                nickName = nickName,
                messageBody = messageBody.text,
                messageType = messageType,
                dateTime = sendTime
            )
            // insert into message database
            insertMessage(messageEntity)
        }
    }

    fun insertRecentMessage(
        user: String,
        nickName: String,
        messageBody: String,
        messageType: Type,
        dateTime: Long
    ) {
        val recentMessage = RecentMessageEntity(
            sender = user,
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
            // val messageDatabaseDao = MessageDB.getInstance(
            //     context = CommonApp.getContext(),
            //     user = sender,
            //     me = receiver
            // ).databaseDao()
            val messageDatabaseDao = MessageDatabaseHelper.getInstance().getMessageDatabaseDao(
                CommonApp.getContext(),
                sender,
                receiver
            ).databaseDao()
            with(messageEntity) {
                MainScope().launch(Dispatchers.IO) {
                    messageDatabaseDao.insertMessage(
                        MessageTable(
                            sender = sender,
                            receiver = receiver,
                            message_type = messageType,
                            send_time = sendTime,
                            is_show = isShow,
                            is_recall = isRecall,
                            text = messageBody.text,
                            image = messageBody.image,
                            audio = messageBody.audio,
                            video = messageBody.video,
                            file = messageBody.file,
                            action = messageBody.action
                        )
                    )
                }
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