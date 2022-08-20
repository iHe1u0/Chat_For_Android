package cc.imorning.chat

import android.content.Context
import cc.imorning.chat.monitor.ChatConnectionListener
import cc.imorning.chat.utils.ChatNotificationManager
import cc.imorning.common.CommonApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : CommonApp() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        ChatNotificationManager.manager.setUpNotificationChannels()
        getTCPConnection().addConnectionListener(ChatConnectionListener())
    }

}