package cc.imorning.database.utils

import android.content.Context
import cc.imorning.database.db.MessageDB
import cc.imorning.database.db.MessageDB.Companion.getInstance

class MessageDatabaseHelper private constructor() {
    init {
        messageDBMap = HashMap()
    }

    fun initNewMessageDBInstance(context: Context?, user: String, me: String): MessageDB? {
        if (messageDBMap.containsKey(me)) {
            return messageDBMap[me]
        }
        val messageDB = getInstance(context!!, user, me)
        messageDBMap[user] = messageDB
        return messageDB
    }

    /**
     * Get the message database,create it if the object is not cached
     */
    fun getMessageDB(context: Context?, user: String, me: String): MessageDB? {
        var messageDB = messageDBMap[user]
        if (messageDB == null) {
            messageDB = initNewMessageDBInstance(context, user, me)
        }
        return messageDB
    }

    companion object {
        private const val TAG = "MessageDatabaseHelper"

        @Volatile
        private var messageDatabaseHelper: MessageDatabaseHelper? = null

        // use HashMap to cache MessageDB instance
        private lateinit var messageDBMap: MutableMap<String, MessageDB>

        val instance: MessageDatabaseHelper?
            get() {
                if (messageDatabaseHelper == null) {
                    synchronized(MessageDatabaseHelper::class.java) {
                        if (messageDatabaseHelper == null) {
                            messageDatabaseHelper = MessageDatabaseHelper()
                        }
                    }
                }
                return messageDatabaseHelper
            }
    }
}