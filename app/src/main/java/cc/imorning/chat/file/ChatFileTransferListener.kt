package cc.imorning.chat.file

import android.util.Log
import cc.imorning.chat.BuildConfig
import cc.imorning.common.utils.FileUtils.Companion.instance
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smackx.filetransfer.FileTransferListener
import org.jivesoftware.smackx.filetransfer.FileTransferRequest
import java.io.File
import java.io.IOException

class ChatFileTransferListener : FileTransferListener {

    override fun fileTransferRequest(request: FileTransferRequest) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "sender: ${request.requestor}")
            Log.d(TAG, "received file:  ${request.fileName}(${request.fileSize})")
        }
        val incomingFileTransfer = request.accept()
        val file = File(
            instance.getFileDir(),
            incomingFileTransfer.fileName
        )
        try {
            incomingFileTransfer.receiveFile(file)
        } catch (e: SmackException) {
            Log.e(TAG, "fileTransferRequest: ${e.message}", e)
        } catch (e: IOException) {
            Log.e(TAG, "fileTransferRequest: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "FileTransferListener"
    }
}