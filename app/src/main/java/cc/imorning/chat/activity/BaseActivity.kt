package cc.imorning.chat.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cc.imorning.chat.ActivityCollector

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    override fun onResume() {
        super.onResume()
        Runtime.getRuntime().gc()
    }

    override fun onDestroy() {
        ActivityCollector.removeActivity(this)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}