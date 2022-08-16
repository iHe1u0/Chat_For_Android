package cc.imorning.chat.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.IBinder
import android.util.Log
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.common.action.LoginAction
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.NetworkUtils
import cc.imorning.common.utils.SessionManager
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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
                    val sessionManager = SessionManager(Config.LOGIN_INFO)
                    if ((!connection.isAuthenticated || !connection.isConnected) && NetworkUtils.isNetworkConnected(
                            context
                        )
                    ) {
                        MainScope().launch(Dispatchers.IO) {
                            try {
                                connection.connect()
                                if (sessionManager.fetchAccount() != null && sessionManager.fetchAuthToken() != null) {
                                    LoginAction.run(
                                        account = sessionManager.fetchAccount()!!,
                                        password = sessionManager.fetchAuthToken()!!
                                    )
                                }
                            } catch (throwable: Throwable) {
                                Log.e(TAG, "connect failed", throwable)
                            }
                        }
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