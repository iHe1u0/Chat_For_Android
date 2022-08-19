package cc.imorning.chat

import android.content.Context
import cc.imorning.common.CommonApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : CommonApp() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

}