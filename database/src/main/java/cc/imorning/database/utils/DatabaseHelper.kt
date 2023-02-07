package cc.imorning.database.utils

import android.content.Context
import cc.imorning.common.utils.MD5Utils
import java.io.File

/**
 * 用户数据库常量
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
     * message table
     */
    const val TABLE_MESSAGE = "message"


    /**
     * user info table name
     */
    const val TABLE_USER_INFO = "user_info"

    /**
     * recent message table
     */
    const val TABLE_RECENT_MESSAGE = "recent_message"

    /**
     * roster table
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
     * @param context The context
     * @param user current login user
     * @param dbName database name
     *
     */
    fun getDatabase(context: Context, user: String, dbName: String): String {
        val rootDir = getDatabaseRootDir(context, user)
        if (dbName.endsWith(".db")) {
            return rootDir.plus(File.separator).plus(dbName)
        }
        return rootDir.plus(File.separator).plus(dbName).plus(".db")
    }

}