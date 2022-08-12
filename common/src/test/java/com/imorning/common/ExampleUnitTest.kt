package com.imorning.common

import com.imorning.common.constant.ServerConfig
import com.imorning.common.utils.FileUtils
import com.imorning.common.utils.MD5Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.jxmpp.jid.impl.JidCreate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun debugP() {
        val jid = "admin@test.com"
        println(MD5Utils.digest(jid))
    }

}