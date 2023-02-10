package cc.imorning.common.utils

import android.os.Environment
import cc.imorning.common.CommonApp
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class FileUtils private constructor() {

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

    /**
     * get roster avatar file path with jidString
     */
    fun getAvatarCachePath(jid: String): File {
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

    /**
     * read string from assets
     */
    fun readStringFromAssets(fileName: String): String {
        if (fileName.isEmpty()) {
            return ""
        }
        val assetManager = context.assets
        val stream = assetManager.open(fileName)
        val lines = BufferedReader(InputStreamReader(stream)).readLines()
        val content = StringBuilder()
        lines.forEach {
            if (it.isEmpty()) {
                content.append("\n")
            } else {
                content.append(it)
            }
        }
        return content.toString()
    }

    private fun getInputStreamCode(inputStream: InputStream): String {
        val head = ByteArray(3)
        inputStream.read(head)
        return when {
            (head[0].toInt() == -1 && head[1].toInt() == -2) -> Charsets.UTF_16.name()
            (head[0].toInt() == -2 && head[1].toInt() == -1) -> "Unicode"
            (head[0].toInt() == -17 && head[1].toInt() == -69 && head[2].toInt() == -65) -> Charsets.UTF_8.name()
            else -> "GBK"
        }
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

    companion object {
        private const val TAG = "FileUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileUtils()
        }
    }
}