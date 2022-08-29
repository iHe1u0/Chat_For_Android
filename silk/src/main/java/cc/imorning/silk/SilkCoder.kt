package cc.imorning.silk

import cc.imorning.common.utils.FileUtils
import cc.imorning.common.utils.MD5Utils
import java.io.File

class SilkCoder private constructor() {

    private external fun nativeGetSilkVersion(): String

    private external fun nativeEncode(input: String, output: String): Int

    private external fun nativeDecode(input: String, output: String): Int


    init {
        System.loadLibrary("silk")
    }

    fun getVersion(): String {
        return nativeGetSilkVersion()
    }

    /**
     * Encode a file with silk
     *
     * @param input the path of file
     *
     * @return a path of file has been encoded
     */
    fun encode(input: String): String? {
        val inputFile = File(input)
        if (inputFile.exists()) {
            val output = FileUtils.instance.getAudioMessagePath(MD5Utils.digest(input).orEmpty())
            nativeEncode(input, output.absolutePath.plus(".slk"))
            return output.absolutePath.plus(".slk")
        }
        return null
    }

    /**
     * Decode a file with silk
     *
     * @param input the path of file
     *
     * @return a path of file has been decoded
     */
    fun decode(input: String): String? {
        val inputFile = File(input)
        if (inputFile.exists()) {
            val output = FileUtils.instance.getAudioMessagePath(MD5Utils.digest(input).orEmpty())
            nativeDecode(input, output.absolutePath.plus(".pcm"))
            return output.absolutePath.plus(".pcm")
        }
        return null
    }

    private object InnerClass {
        val silkCoder = SilkCoder()
    }

    companion object {

        fun getInstance(): SilkCoder {
            return InnerClass.silkCoder
        }
    }
}