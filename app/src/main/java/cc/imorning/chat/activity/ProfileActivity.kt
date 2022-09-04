package cc.imorning.chat.activity

import android.os.Bundle
import androidx.activity.compose.setContent

private const val TAG = "ProfileActivity"

class ProfileActivity : BaseActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContent { }
    }
}