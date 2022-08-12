package com.imorning.common.utils

import androidx.core.os.EnvironmentCompat
import com.imorning.chat.App
import java.io.File

class FileUtils {

    private val context = App.getContext()

    /**
     * Get external files root dir
     * @return /storage/emulated/0/Android/data/com.imorning.chat/files
     */
    fun getAndroidRoot(): File {
        return context.getExternalFilesDir(null)!!
    }

    fun getAvatarCache(jid: String) = File(getAvatarImagesDir(), MD5Utils.digest(jid)!!)

    /**
     * get avatar dir
     */
    private fun getAvatarImagesDir(): String {
        val dir = context.externalCacheDir!!.absolutePath.plus("/avatar/")
        if (!File(dir).exists()) {
            File(dir).mkdir()
        }
        return dir
    }

    companion object {
        private const val TAG = "FileUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileUtils()
        }
    }
}