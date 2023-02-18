package cc.imorning.chat.file

import android.util.Log
import cc.imorning.chat.BuildConfig
import cc.imorning.common.utils.FileUtils.Companion.instance
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "file:  ${request.fileName}(${request.fileSize}KB)")
        }
        val incomingFileTransfer = request.accept()
        val file = File(instance.getFileDir(), File(incomingFileTransfer.fileName).name)
        if (file.exists()) {
            file.delete()
        }
        try {
            MainScope().launch(Dispatchers.IO) {
                incomingFileTransfer.receiveFile(file)
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "file [${file.absolutePath}] has been saved")
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