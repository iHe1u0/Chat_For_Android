package cc.imorning.database.utils

import android.content.Context
import cc.imorning.common.utils.MD5Utils
import java.io.File

/**
 * 用户数据库常亮
 */
object DatabaseHelper {
    /**
     * data database, include [TABLE_USER_INFO] and [TABLE_ROSTER]
     */
    const val DATA_DB = "data.db"

    /**
     * recent message database,include [TABLE_RECENT_MESSAGE]
     */
    const val RECENT_MESSAGE_DB = "recent_message.db"

    /**
     * message database
     */
    const val MESSAGE_DB = "message.db"

    /**
     * contact info table name
     */
    const val TABLE_USER_INFO = "user_info"

    /**
     * message table
     */
    const val TABLE_RECENT_MESSAGE = "recent_message"

    /**
     * message table
     */
    const val TABLE_ROSTER = "roster"

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