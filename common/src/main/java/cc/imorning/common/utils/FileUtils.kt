package cc.imorning.common.utils

import android.os.Environment
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
     * get avatar cache dir
     */
    private fun getAvatarImagesDir(): String {
        val dir = getCacheDir()!!.absolutePath.plus("/avatarCache/")
        val file = File(dir)
        if (!file.exists()) {
            file.mkdir()
        }
        return dir
    }

    /**
     * get a file for message file
     */
    fun getChatMessageCache(fileName: String? = "file"): File {
        val dir = getCacheDir()!!.absolutePath.plus("/messageCache")
        val fileDir = File(dir)
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        return File(fileDir, fileName!!)
    }

    fun getAudioMessagePath(fileName: String): File {
        val audioFileDir = getChatMessageCache("audio")
        if (!audioFileDir.exists()) {
            audioFileDir.mkdir()
        }
        val audioFile = File(audioFileDir, fileName)
        if (!audioFile.exists()) {
            audioFile.delete()
        }
        return audioFile
    }

    fun getAudioPCMCacheDir(): String {
        val pcmFileDir = getChatMessageCache("pcm")
        if (!pcmFileDir.exists()) {
            pcmFileDir.mkdir()
        }
        return pcmFileDir.absolutePath
    }

    companion object {
        private const val TAG = "FileUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileUtils()
        }
    }
}