package cc.imorning.chat.file

import android.util.Log
import cc.imorning.common.constant.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.filetransfer.FileTransfer
import org.jivesoftware.smackx.filetransfer.FileTransferManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.io.File
import java.io.FileInputStream

private const val TAG = "FileTransferUtils"

object FileTransferUtils {

    fun sendFile(
        connection: XMPPTCPConnection,
        receiver: String,
        file: File,
        description: String = ""
    ) {
        if (file.exists()) {
            val transferManager = FileTransferManager.getInstanceFor(connection)
            val receiverUid = JidCreate.entityFullFrom(
                JidCreate.entityBareFrom(receiver),
                Resourcepart.from(ServerConfig.RESOURCE)
            )
            val out = transferManager.createOutgoingFileTransfer(receiverUid)
            MainScope().launch(Dispatchers.IO) {
                try {
                    out.sendStream(FileInputStream(file), file.name, file.length(), description)
                } catch (smackException: SmackException) {
                    Log.e(TAG, "send file failed", smackException)
                }
                while (!out.isDone) {
                    when (out.status) {
                        FileTransfer.Status.cancelled -> {
                            Log.d(TAG, "cancelled")
                        }

                        FileTransfer.Status.error -> {
                            Log.d(TAG, "send file error:${out.error}")
                            out.exception.printStackTrace()
                        }

                        FileTransfer.Status.in_progress -> {
                            Log.d(TAG, "sending file:${out.progress * 100}%")
                        }

                        FileTransfer.Status.complete -> {
                            Log.d(TAG, "complete")
                        }

                        else -> {}
                    }
                    delay(1000)
                }
            }
        } else {
            Log.d(TAG, "file not exist")
        }
    }

    enum class FileType {
        STICKER, FILE, IMAGE, AUDIO, VIDEO, OTHER
    }
}