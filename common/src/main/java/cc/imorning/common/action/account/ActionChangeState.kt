package cc.imorning.common.action.account

import android.util.Log
import cc.imorning.common.BuildConfig
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.StatusCode
import org.jivesoftware.smack.SmackException.NotConnectedException

object ActionChangeState {

    private const val TAG = "ActionChangeState"

    private val connection = CommonApp.getTCPConnection()
    fun run(state: String): Int {
        val presenceBuilder = connection.stanzaFactory.buildPresenceStanza()
        presenceBuilder.status = state
        try {
            connection.sendStanza(presenceBuilder.build())
            return StatusCode.OK
        } catch (e: NotConnectedException) {
            return StatusCode.NETWORK_ERROR
        } catch (e: InterruptedException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, e.localizedMessage, e)
            }
            return StatusCode.ERROR
        }
    }
}