package com.imorning.chat.activity

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        Runtime.getRuntime().gc()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    companion object {
        private const val TAG = "BaseActivity"
    }
}