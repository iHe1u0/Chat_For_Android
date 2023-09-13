package cc.imorning.chat.action.message

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.action.RosterAction
import cc.imorning.chat.network.ConnectionManager
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.ServerConfig
import cc.imorning.common.utils.Base64Utils
import cc.imorning.common.utils.RingUtils
import cc.imorning.database.db.RecentDB
import cc.imorning.database.entity.MessageBody
import cc.imorning.database.entity.MessageEntity
import cc.imorning.database.entity.MessageTable
import cc.imorning.database.entity.RecentMessageEntity
import cc.imorning.database.utils.MessageDatabaseHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Message.Type
import org.jivesoftware.smackx.filetransfer.FileTransfer
import org.jivesoftware.smackx.filetransfer.FileTransferManager
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.io.File
import java.io.OutputStream

private const val TAG = "MessageHelper"

object MessageHelper {

    private val recentDatabaseDao =
        RecentDB.getInstance(CommonApp.getContext(), App.user).recentDatabaseDao()

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
        return Gson().fromJson(sourceString, MessageEntity::class.java)
    }

    fun sendFile(file: File, receiver: String) {
        if (!file.exists() || receiver.isEmpty()) {
            return
        }
        if (ConnectionManager.isConnectionAvailable(connection)) {
            val receiverUid = JidCreate.entityFullFrom(
                JidCreate.entityBareFrom(receiver),
                Resourcepart.from(ServerConfig.RESOURCE)
            )

            val fileTransfer = FileTransferManager
                .getInstanceFor(connection)
                .createOutgoingFileTransfer(receiverUid)
            fileTransfer.setCallback(object : OutgoingFileTransfer.NegotiationProgress {
                override fun statusUpdated(
                    oldStatus: FileTransfer.Status?,
                    newStatus: FileTransfer.Status?
                ) {
                    Log.d(TAG, "statusUpdated: [$oldStatus]>[$newStatus]")
                }

                override fun outputStreamEstablished(stream: OutputStream?) {

                }

                override fun errorEstablishingStream(e: Exception?) {
                    Log.e(TAG, "errorEstablishingStream: ${e?.message}", e)
                }

            })
            fileTransfer.sendFile(file, "file")
            MainScope().launch(Dispatchers.IO) {
                while (!fileTransfer.isDone) {
                    Log.w(TAG, "sendFile: [${fileTransfer.status}]:${fileTransfer.progress}")
                    delay(1000)
                }
                if (fileTransfer.status == FileTransfer.Status.error) {
                    Log.e(TAG, "send failed: ${fileTransfer.error}", fileTransfer.exception)
                }
            }
        }

    }

    /**
     * Process received message,insert message into database
     */
    private fun processChatMessage(messageEntity: MessageEntity, chat: Chat?) {
        with(messageEntity) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, this.toString())
            }
            val nickName = RosterAction.getNickName(sender)
            val messageText = StringBuffer()
            if (messageBody.image.isNullOrEmpty()) {
                if (messageBody.text.isNotEmpty()) {
                    messageText.append(messageBody.text)
                }
            } else {
                messageText.append("[图片]")
            }
            insertRecentMessage(
                user = sender,
                nickName = nickName,
                messageBody = messageText.toString(),
                messageType = messageType,
                dateTime = sendTime
            )
            // insert into message database
            insertChatMessage(messageEntity)
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

    fun insertChatMessage(messageEntity: MessageEntity) {
        // if connection is not connect or authenticated,then return
        if (connection.isConnected && connection.isAuthenticated) {
            val sender = messageEntity.sender
            val receiver = messageEntity.receiver
            // if sender is me, we need to process something different with the other
            val sendIsMe = sender == connection.user.asEntityBareJidString()
            val messageDatabaseDao = MessageDatabaseHelper.instance.getMessageDB(
                CommonApp.getContext(),
                sender,
                receiver
            )!!.databaseDao()
            with(messageEntity) {
                MainScope().launch(Dispatchers.IO) {
                    // if image is empty,then only send text message
                    if (messageBody.image.isNullOrEmpty()) {
                        messageDatabaseDao.insertMessage(
                            MessageTable(
                                sender = sender,
                                receiver = receiver,
                                messageType = messageType,
                                send_time = sendTime,
                                is_show = isShow,
                                is_recall = isRecall,
                                text = messageBody.text
                            )
                        )
                    } else {
                        // if image is not empty,then the text is local path,and the image is sender path
                        val imagePath = if (sendIsMe) {
                            messageBody.text
                        } else {
                            messageBody.image
                        }
                        messageDatabaseDao.insertMessage(
                            MessageTable(
                                sender = sender,
                                receiver = receiver,
                                messageType = messageType,
                                send_time = sendTime,
                                is_show = isShow,
                                is_recall = isRecall,
                                text = "",
                                image = imagePath
                            )
                        )
                    }
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