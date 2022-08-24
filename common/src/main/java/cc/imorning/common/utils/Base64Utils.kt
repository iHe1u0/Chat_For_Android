package cc.imorning.common.utils

import android.util.Base64
import android.util.Log
import cc.imorning.common.BuildConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "Base64Utils"

object Base64Utils {

    /**
     * encode plainText with Base64
     *
     * @return string after base64 encode
     */
    fun encodeString(plainText: String): String {
        return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT)
    }

    /**
     * decode an encoding string
     *
     * @return result if success or null for decode failed
     */
    fun decodeString(encodeString: String): String? {
        return try {
            val byte = Base64.decode(encodeString, Base64.DEFAULT)
            String(byte)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /**
     * encode file with base64
     *
     * @return null if file doesn't exist or file is a dir or something is wrong
     */
    fun encodeFile(file: File?): String {
        if (file == null || !file.exists() || !file.isFile) {
            return ""
        }
        val inputStream = FileInputStream(file)
        return try {
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            inputStream.close()
            Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "encode file failed: ${e.localizedMessage}")
            }
            ""
        }
    }

    /**
     * decode String to File
     *
     * @param fileString a file but encode with Base64
     *
     * @return file or null if failed or fileString is null
     */
    fun decodeStringToFile(fileString: String): File? {
        if (fileString.isEmpty() || fileString.isBlank()) {
            return null
        }
        val file = FileUtils.instance.getChatMessageCache(MD5Utils.digest(fileString))
        if (file.exists()) {
            file.delete()
        }
        try {
            val byteArray: ByteArray = Base64.decode(fileString, Base64.DEFAULT)
            for (index in byteArray.indices) {
                if (byteArray[index] < 0) {
                    byteArray[index] = (byteArray[index].plus(256)).toByte()
                }
            }
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()
            return file
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "decodeStringToFile failed: ${e.localizedMessage}")
            }
            return null
        }
    }
}