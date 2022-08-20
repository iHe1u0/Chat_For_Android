package cc.imorning.common.action

import android.util.Log
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.StatusCode
import cc.imorning.common.constant.StatusCode.OK
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
    ): Int {
        var retCode: Int
        val connection: AbstractXMPPConnection = CommonApp.getTCPConnection()
        if (NetworkUtils.isNetworkNotConnected(CommonApp.getContext())) {
            retCode = StatusCode.NETWORK_ERROR
        } else {
            runBlocking(Dispatchers.IO) {
                supervisorScope {
                    val job = async(Dispatchers.IO) {
                        if (!connection.isConnected) {
                            connection.connect()
                        }
                        connection.login(account, password)
                        val presence =
                            connection.stanzaFactory.buildPresenceStanza()
                                .ofType(Presence.Type.unavailable)
                                .build()
                        connection.sendStanza(presence)
                    }
                    try {
                        job.await()
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "login success @ ${DateTime.now()}")
                        }
                        retCode = OK
                    } catch (e: SmackException.AlreadyConnectedException) {
                        retCode = OK
                    } catch (e: SmackException.AlreadyLoggedInException) {
                        retCode = OK
                    } catch (e: SmackException) {
                        Log.e(TAG, "network exception", e)
                        retCode = StatusCode.NETWORK_ERROR
                    } catch (e: SASLErrorException) {
                        retCode = StatusCode.LOGIN_AUTH_FAILED
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}", e)
                        retCode = StatusCode.ERROR
                    }
                }
            }
        }
        return retCode
    }

}