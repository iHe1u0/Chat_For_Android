package cc.imorning.common.manager

import android.util.Log
import cc.imorning.common.ActivityCollector
import cc.imorning.common.CommonApp
import cc.imorning.common.action.LoginAction
import cc.imorning.common.constant.Config
import cc.imorning.common.utils.NetworkUtils
import cc.imorning.common.utils.SessionManager
import kotlinx.coroutines.*
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection

object ConnectionManager {

    private const val TAG = "ConnectionManager"

    fun isConnectionAuthenticated(connection: XMPPConnection? = CommonApp.getTCPConnection()): Boolean {

        if (connection == null || !connection.isConnected || !connection.isAuthenticated) {
            return false
        }
        return true
    }

    @Synchronized
    fun connect(connection: XMPPTCPConnection) {
        MainScope().launch(Dispatchers.IO) {
            supervisorScope {
                val connectJob = async(Dispatchers.IO) {
                    if (!connection.isConnected) {
                        connection.connect()
                    }
                    val sessionManager = SessionManager(Config.LOGIN_INFO)
                    if (!connection.isAuthenticated &&
                        sessionManager.fetchAccount() != null &&
                        sessionManager.fetchAuthToken() != null &&
                        !ActivityCollector.activities.last.localClassName.contains("LoginActivity")
                    ) {
                        LoginAction.run(
                            account = sessionManager.fetchAccount()!!,
                            password = sessionManager.fetchAuthToken()!!
                        )
                    }
                }
                try {
                    if (!connection.isConnected && NetworkUtils.isNetworkConnected(CommonApp.getContext())) {
                        connectJob.await()
                    } else {
                        connectJob.cancel(message = "No network")
                    }
                } catch (e: SmackException.AlreadyConnectedException) {
                    Log.d(TAG, "${e.message}")
                } catch (e: SmackException.AlreadyLoggedInException) {
                    Log.d(TAG, "${e.message}")
                } catch (throwable: Throwable) {
                    Log.e(TAG, "TCP connect failed")
                }
            }

        }
    }

}