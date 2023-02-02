package cc.imorning.database.utils

import android.content.Context
import cc.imorning.common.utils.MD5Utils
import java.io.File

/**
 * 用户数据库常亮
 */
object DatabaseHelper {
    /**
     * database name
     */
    const val DATABASE_NAME = "data.db"

    /**
     * contact info table name
     */
    const val TABLE_CONTACT_INFO = "contact_info"

    /**
     * message table
     */
    const val TABLE_RECENT_MESSAGE = "recent_message"

    /**
     * get user database dir, e.g:
     * /data/user/0/cc.imorning.chat/databases/md5.encode(jid)
     */
    fun getDatabaseRootDir(context: Context, jid: String): String {
        val encodeUserDir = MD5Utils.digest(jid)
        return context.getDatabasePath(encodeUserDir).path
    }

    /**
     * get a database path
     */
    fun getDatabase(context: Context, jid: String, dbName: String): String {
        val rootDir = getDatabaseRootDir(context, jid)
        if (dbName.endsWith(".db")) {
            return rootDir.plus(File.separator).plus(dbName)
        }
        return rootDir.plus(File.separator).plus(dbName).plus(".db")
    }

}