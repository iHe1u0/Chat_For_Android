package cc.imorning.common.utils

import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
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

    fun getCacheDir(): File? {
        return if (Environment.isExternalStorageEmulated()) {
            context.externalCacheDir
        } else {
            context.cacheDir
        }
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
        val dir = getCacheDir()!!.absolutePath.plus("/avatar/")
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