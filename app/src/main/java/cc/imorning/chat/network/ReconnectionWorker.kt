package cc.imorning.chat.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import cc.imorning.chat.App
import cc.imorning.common.BuildConfig
import org.joda.time.DateTime

class ReconnectionWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        val connection = App.getTCPConnection()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "reconnect @ ${DateTime.now()}")
        }
        if (ConnectionManager.isConnectionAuthenticated(connection)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "reconnect success")
            }
            return Result.success()
        }
        with(ConnectionManager.connect(connection)) {
            if (ConnectionManager.isConnectionAuthenticated(connection)) {
                Log.w(TAG, "reconnect failed, try it later")
                return Result.retry()
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "reconnect success")
            }
            return Result.success()
        }
    }

    companion object {
        private const val TAG = "ReconnectionWorker"
    }
}