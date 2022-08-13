package com.imorning.common.action

import android.util.Log
import com.imorning.chat.App
import com.imorning.common.BuildConfig
import com.imorning.common.constant.StatusCode
import com.imorning.common.constant.StatusCode.LOGIN_FAILED_CAUSE_ONLINE
import com.imorning.common.constant.StatusCode.OK
import com.imorning.common.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.sasl.SASLErrorException

object LoginAction {

    private const val TAG = "LoginAction"

    fun run(
        account: String,
        password: String
    ): Int {
        var retCode: Int
        val connection: AbstractXMPPConnection = App.getTCPConnection()
        if (NetworkUtils.isNetworkNotConnected(App.getContext())) {
            retCode = StatusCode.NETWORK_ERROR
        } else {
            runBlocking(Dispatchers.IO) {
                supervisorScope {
                    val job = async(Dispatchers.IO) {
                        if (!connection.isConnected) {
                            try {
                                connection.connect()
                            } catch (assertError: AssertionError) {
                                return@async
                            }
                        }
                        if (!connection.isAuthenticated) {
                            connection.login(account, password)
                            val presence =
                                connection.stanzaFactory.buildPresenceStanza()
                                    .ofType(Presence.Type.unavailable)
                                    .build()
                            connection.sendStanza(presence)
                            retCode = OK
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "user has been online")
                            }
                            retCode = LOGIN_FAILED_CAUSE_ONLINE
                        }
                    }
                    try {
                        job.await()
                        retCode = OK
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "login success")
                        }
                    } catch (e: SmackException) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "network exception", e)
                        }
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