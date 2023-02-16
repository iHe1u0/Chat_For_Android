package cc.imorning.chat.file

import android.util.Log
import cc.imorning.chat.BuildConfig
import org.jivesoftware.smackx.filetransfer.FileTransfer
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer.NegotiationProgress
import java.io.OutputStream

class SendProgress : NegotiationProgress {
    override fun statusUpdated(oldStatus: FileTransfer.Status, newStatus: FileTransfer.Status) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "statusUpdated: [$oldStatus]>[$newStatus]")
        }
    }

    override fun outputStreamEstablished(stream: OutputStream) {
    }

    override fun errorEstablishingStream(e: Exception) {
        Log.e(TAG, "errorEstablishingStream: ${e.message},e")
    }

    companion object {
        private const val TAG = "SendProgress"
    }
}