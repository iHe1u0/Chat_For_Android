package cc.imorning.common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import cc.imorning.common.CommonApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.*

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

    fun getFileDir(): File? {
        return context.getExternalFilesDir("chatFile")

    }

    /**
     * get roster avatar file path with jidString
     */
    fun getAvatarCachePath(jid: String): File {
        return File(getAvatarImagesDir(), jid)
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

    fun copy(srcFile: File, dstFile: File) {
        if (!srcFile.exists() || (srcFile.length() == 0L)) {
            dstFile.createNewFile()
            return
        }
        val inputStream = FileInputStream(srcFile)
        val outputStream = FileOutputStream(dstFile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.os.FileUtils.copy(inputStream, outputStream)
        } else {
            val bufferedInputStream = BufferedInputStream(inputStream)
            val bufferedOutputStream = BufferedOutputStream(outputStream)
            val buffer = ByteArray(4096)
            var len: Int
            while (true) {
                len = bufferedInputStream.read(buffer)
                if (len == -1) {
                    break
                }
                bufferedOutputStream.write(buffer, 0, len)
            }
            bufferedInputStream.close()
            bufferedOutputStream.close()
        }
        inputStream.close()
        outputStream.close()
    }

    fun copy(srcFilePath: String, dstFilePath: String) {
        copy(File(srcFilePath), File(dstFilePath))
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

    fun getFileBytes(file: File): ByteArray {
        if (file.exists()) {
            return file.readBytes()
        }
        return ByteArray(0)
    }

    fun getFileMimeType(file: File): String {
        if (file.exists()) {
            return MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        }
        return "UNKNOWN"
    }

    /**
     * compress image
     * @param srcFile source file
     *
     * @return compressed file
     */
    fun compressImage(srcFile: File): File {
        assert(srcFile.exists())
        val srcFileLength = srcFile.length() / 1024
        // if source file is less than 512KB,then do nothing
        if (srcFileLength < 512) {
            return srcFile
        }
        // compress quality depends on source file size
        val quality =
            if (srcFileLength > 2048) {
                25
            } else if (srcFileLength > 1024) {
                50
            } else {
                75
            }
        val dir = getCacheDir()!!.absolutePath
        val file = File(dir, "tmp.jpg")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        file.deleteOnExit()
        val fileOutputStream = FileOutputStream(file)
        val bitmap = BitmapFactory.decodeFile(srcFile.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        Log.d(TAG, "compressImage: [${srcFile.length() / 1024}KB]>[${file.length() / 1024}KB]")
        if (srcFile.length() < file.length()) {
            return srcFile
        }
        return file
    }

    /**
     * Clean cache
     */
    fun cleanCache() {
        val avatarCacheFiles = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if ((avatarCacheFiles != null) && avatarCacheFiles.exists()) {
            val fileList = avatarCacheFiles.listFiles() ?: return
            MainScope().launch(Dispatchers.IO) {
                for (file in fileList) {
                    if (file.name.startsWith("cropped")) {
                        file.delete()
                    }
                }
            }
        }
    }

    fun processPicMessage(picList: MutableList<File>): String? {
        if (picList.size == 0) {
            return null
        }
        val picMessage = StringBuffer()
        picList.forEach {
            picMessage.append(
                Base64Utils.encodeFile(it)
            )
        }
        return picMessage.toString()
    }

    fun createTempFile(tempFileName: String?): File {
        var fileName = tempFileName
        if (fileName.isNullOrEmpty()) {
            fileName = "temp${System.currentTimeMillis()}"
        }
        val file = File(getCacheDir(), fileName)
        if (file.exists()) {
            file.delete()
        }
        file.deleteOnExit()
        return file
    }

    fun copy(inputStream: InputStream, outputStream: FileOutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (inputStream.read(buf).also { length = it } != -1) {
            outputStream.write(buf, 0, length)
        }
    }

    companion object {
        private const val TAG = "FileUtils"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FileUtils()
        }
    }
}