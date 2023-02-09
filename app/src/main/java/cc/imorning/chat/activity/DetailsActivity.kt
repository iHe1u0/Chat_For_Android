package cc.imorning.chat.activity

import android.os.Bundle
import android.util.Log

class DetailsActivity : BaseActivity() {

    companion object {
        private const val TAG = "DetailsActivity"
        const val KEY_UID = "user_jid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra(KEY_UID)
        Log.d(TAG, "get uid: $uid [$this]")
    }
}