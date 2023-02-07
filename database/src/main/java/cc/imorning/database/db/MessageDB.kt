package cc.imorning.database.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.database.converters.CommonConverter
import cc.imorning.database.dao.MessageDatabaseDao
import cc.imorning.database.utils.DatabaseHelper

/**
 * message database for each user,the database name use jid
 */
@TypeConverters(CommonConverter::class)
abstract class MessageDB : RoomDatabase() {

    abstract fun databaseDao(): MessageDatabaseDao

    companion object {
        private const val TAG = "MessageDB"
        private lateinit var messageDB: MessageDB

        fun getInstance(context: Context, user: String, receiver: String): MessageDB {
            if (!this::messageDB.isInitialized) {
                // Create new instance for message database with jid
                messageDB = Room.databaseBuilder(
                    context, MessageDB::class.java,
                    DatabaseHelper.getDatabase(context, user, receiver)
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return messageDB
        }
    }
}