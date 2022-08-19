package cc.imorning.chat.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.App
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.NetworkUtils

class NetworkService : Service() {

    private val binder = NetworkServiceBinder()

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null || null == intent.action) {
                return
            }
            val action = intent.action
            if (action.equals(CONNECTIVITY_ACTION)) {
                if (NetworkUtils.isNetworkConnected(App.getContext())) {
                    val connection = App.getTCPConnection()
                    if ((!connection.isAuthenticated || !connection.isConnected) &&
                        NetworkUtils.isNetworkConnected(context)
                    ) {
                        ConnectionManager.connect(connection)
                    }
                }
            }
        }

    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: ")
        val intentFilter = IntentFilter(CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
        return binder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        unregisterReceiver(networkReceiver)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "NetworkService"
    }

    inner class NetworkServiceBinder : Binder() {
        fun getService(): NetworkService = this@NetworkService
    }

}