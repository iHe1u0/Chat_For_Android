package cc.imorning.chat.monitor

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import cc.imorning.chat.App
import cc.imorning.chat.BuildConfig
import cc.imorning.chat.activity.LoginActivity
import cc.imorning.chat.service.MessageMonitorService
import cc.imorning.common.CommonApp
import cc.imorning.common.constant.ServerConfig
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.iqversion.VersionManager
import org.jivesoftware.smackx.vcardtemp.VCardManager

class ChatConnectionListener : ConnectionListener {

    private val context: Context = CommonApp.getContext()
    private var messageMonitor: Intent? = null

    override fun connected(connection: XMPPConnection?) {
        super.connected(connection)
        if ((connection != null) && connection.isConnected) {
            // set client version
            VersionManager.getInstanceFor(connection)
                .setVersion(
                    ServerConfig.RESOURCE, BuildConfig.VERSION_NAME,
                    "Android ${Build.VERSION.RELEASE}"
                )
        }
    }

    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        if (messageMonitor == null) {
            messageMonitor = Intent(CommonApp.getContext(), MessageMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(messageMonitor)
            } else {
                context.startService(messageMonitor)
            }
        }
        CommonApp.vCard = VCardManager.getInstanceFor(connection).loadVCard()
        if (connection != null) {
            App.user = connection.user.asEntityBareJidString()
        }
    }

    override fun connectionClosed() {
        super.connectionClosed()
        context.stopService(messageMonitor)
        App.user = ""
    }

    /**
     * java.net.SocketException: Software caused connection abort >>> Close cause network error
     * org.jivesoftware.smack.XMPPException$StreamErrorException  >>> Close cause sign in elsewhere
     */
    override fun connectionClosedOnError(e: Exception?) {
        Log.e(TAG, "connectionClosedOnError: ${e?.message}", e)
        context.stopService(messageMonitor)
        messageMonitor = null
        App.user = ""
        if (e is XMPPException.StreamErrorException) {
            val loginActivity = Intent(context, LoginActivity::class.java)
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(loginActivity)
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "登录过期，请重新登录", Toast.LENGTH_LONG).show()
            }
        }
        super.connectionClosedOnError(e)
    }

    companion object {
        private const val TAG = "ChatConnectionListener"
    }
}