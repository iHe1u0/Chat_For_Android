package cc.imorning.chat.file

import android.util.Log
import cc.imorning.chat.R
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
                while (true) {
                    if (incomingFileTransfer.isDone) {
                        break
                    }
                    continue
                }
                val context = CommonApp.getContext()
                if (context != null) {
                    ToastUtils.showMessage(context, context.getString(R.string.receive_new_file))
                }
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