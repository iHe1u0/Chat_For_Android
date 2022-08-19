package cc.imorning.common.utils

import android.util.Log
import cc.imorning.common.CommonApp
import com.orhanobut.logger.BuildConfig
import java.io.File

class FileUtils {

    private val context = CommonApp.getContext()

    /**
     * Get external files root dir
     * @return /storage/emulated/0/Android/data/cc.imorning.chat/files
     */
    fun getAndroidRoot(): File {
        return context.getExternalFilesDir(null)!!
    }

    fun getAvatarCachePath(jid: String): File {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getAvatarCache: $jid")
        }
        return File(getAvatarImagesDir(), MD5Utils.digest(jid)!!)
    }

    /**
     * return true if file exist
     */
    fun isFileExist(filePath: String?): Boolean {
        if (null == filePath) {
            return false
        }
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            return true
        }
        return false
    }

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