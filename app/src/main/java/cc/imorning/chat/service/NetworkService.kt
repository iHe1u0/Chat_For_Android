package cc.imorning.chat.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.IBinder
import cc.imorning.chat.App
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.NetworkUtils

class NetworkService : Service() {

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null) {
                return
            }
            val action = intent.action
            if (action.equals(CONNECTIVITY_ACTION)) {
                if (NetworkUtils.isNetworkConnected(App.getContext())) {
                    val connection = App.getTCPConnection()
                    if ((!connection.isAuthenticated || !connection.isConnected) && NetworkUtils.isNetworkConnected(
                            context
                        )
                    ) {
                        ConnectionManager.connect(connection)
                    }
                }
            }
        }

    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter(CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(networkReceiver)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "NetworkService"
    }

}