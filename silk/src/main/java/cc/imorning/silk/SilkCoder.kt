package cc.imorning.silk

object SilkCoder {

    private val silk16 = Silk16()

    external fun getVersion(): String

    init {
        System.loadLibrary("silk")
    }

    object Silk {

        fun open(compression: Int): Int {
            return silk16.open(compression)
        }

        fun decode(encoded: ByteArray, line: ShortArray, size: Int): Int {
            return silk16.decode(encoded, line, size)
        }

        fun encode(line: ShortArray, offset: Int, encoded: ByteArray, size: Int): Int {
            return silk16.encode(line, offset, encoded, size)
        }

        fun close() {
            silk16.close()
        }

    }
}