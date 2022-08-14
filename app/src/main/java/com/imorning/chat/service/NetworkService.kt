package com.imorning.chat.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.IBinder
import com.imorning.chat.App
import com.imorning.chat.BuildConfig
import com.imorning.common.action.LoginAction
import com.imorning.common.constant.Config
import com.imorning.common.utils.NetworkUtils
import com.imorning.common.utils.SessionManager
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*

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
                    if (BuildConfig.DEBUG) {
                        Logger.d(
                            "server is connected:${connection.isConnected} isAuthed:${connection.isAuthenticated}"
                        )
                    }
                    if (!connection.isAuthenticated &&
                        !connection.isConnected &&
                        NetworkUtils.isNetworkConnected(context)
                    ) {
                        MainScope().launch(Dispatchers.IO) {
                            runBlocking {
                                supervisorScope {
                                    val connectJob = async(Dispatchers.IO) {
                                        connection.connect()
                                        if (sessionManager.fetchAccount() != null && sessionManager.fetchAuthToken() != null) {
                                            LoginAction.run(
                                                account = sessionManager.fetchAccount()!!,
                                                password = sessionManager.fetchAuthToken()!!
                                            )
                                        }
                                    }
                                    try {
                                        connectJob.await()
                                    } catch (assertionError: AssertionError) {
                                        Logger.e("get TCP Connection failed", assertionError)
                                    }
                                }
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