package cc.imorning.database.utils

import android.content.Context
import cc.imorning.common.CommonApp
import java.io.File

/**
 * 用户数据库常量
 */
object DatabaseHelper {

    /**
     * account database, record account, token and expired time.
     */
    const val ACCOUNT_DB = "account.db"

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
     * /data/user/0/cc.imorning.chat/databases/md5.encode(jid)/
     */
    fun getDatabaseRootDir(context: Context, jid: String): String {
        var user = jid
        if (user.isEmpty()) {
            if (CommonApp.xmppTcpConnection.user != null) {
                user = CommonApp.xmppTcpConnection.user.asEntityBareJidString()
            }
        }
        if (user.isEmpty()) {
            CommonApp.exitApp(-1)
        }
        val userDir = user.replace("@", "_")
        return context.getDatabasePath(userDir).path.plus(File.separator)
    }

    /**
     * get a database path
     * @param context The context
     * @param user current login user
     * @param dbName database name
     *
     */
    fun getDatabase(context: Context, me: String, dbName: String): String {
        val rootDir = getDatabaseRootDir(context, me)
        if (dbName.endsWith(".db")) {
            return rootDir.plus(dbName)
        }
        return rootDir.plus(dbName).plus(".db")
    }

    fun getMessageDatabase(context: Context, user: String, me: String): String {
        return getDatabaseRootDir(context, me)
            .plus("message")
            .plus(File.separator)
            .plus(user.replace("@", "_"))
            .plus(".db")
    }

}