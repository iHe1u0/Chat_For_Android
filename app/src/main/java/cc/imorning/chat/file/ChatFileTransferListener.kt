package cc.imorning.chat.file

import android.util.Log
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.ui.view.ToastUtils
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smackx.filetransfer.FileTransferListener
import org.jivesoftware.smackx.filetransfer.FileTransferRequest
import java.io.File
import java.io.IOException

class ChatFileTransferListener : FileTransferListener {

    override fun fileTransferRequest(request: FileTransferRequest) {
        val incomingFileTransfer = request.accept()
        val file = File(
            FileUtils.getChatFileFolder(CommonApp.getContext()),
            File(incomingFileTransfer.fileName).name
        )
        try {
            MainScope().launch(Dispatchers.IO) {
                incomingFileTransfer.receiveFile(file)
            }
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