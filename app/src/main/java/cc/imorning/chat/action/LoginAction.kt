package cc.imorning.chat.action

import android.util.Log
import cc.imorning.chat.App
import cc.imorning.common.CommonApp
import cc.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.sasl.SASLErrorException
import org.joda.time.DateTime

object LoginAction {

    private const val TAG = "LoginAction"

    @Synchronized
    fun run(
        account: String,
        password: String
    ) {
        val connection: AbstractXMPPConnection = App.getTCPConnection()
        if (NetworkUtils.isNetworkNotConnected(CommonApp.getContext())) {
            return
        } else {
            runBlocking(Dispatchers.IO) {
                supervisorScope {
                    val job = async(Dispatchers.IO) {
                        try {
                            if (!connection.isConnected) {
                                connection.connect()
                            }
                            if (!connection.isAuthenticated) {
                                connection.login(account, password)
                            }
                            val presence =
                                connection.stanzaFactory.buildPresenceStanza()
                                    .ofType(Presence.Type.unavailable)
                                    .build()
                            connection.sendStanza(presence)
                        } catch (throwable: Throwable) {
                            Log.e(TAG, "connect or login failed: ${throwable.localizedMessage}")
                        }
                    }
                    try {
                        job.await()
                        Log.d(TAG, "login success @ ${DateTime.now()}")
                    } catch (e: SmackException.AlreadyConnectedException) {
                    } catch (e: SmackException.AlreadyLoggedInException) {
                    } catch (e: SmackException) {
                        Log.e(TAG, "network exception", e)
                    } catch (e: SASLErrorException) {
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}", e)
                    }
                }
            }
        }
    }

}