package cc.imorning.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.database.converters.CommonConverter
import cc.imorning.database.dao.MessageDatabaseDao
import cc.imorning.database.entity.MessageTable
import cc.imorning.database.utils.DatabaseHelper

/**
 * message database for each user,the database name use jid
 */
@Database(
    entities = [MessageTable::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(CommonConverter::class)
abstract class MessageDB : RoomDatabase() {

    abstract fun databaseDao(): MessageDatabaseDao

    companion object {
        private const val TAG = "MessageDB"
        private lateinit var messageDB: MessageDB

        /**
         * get message database
         * @param context Context object for get database dir
         * @param user the other one
         * @param me Current login user
         */
        fun getInstance(context: Context, user: String, me: String): MessageDB {
            if (!this::messageDB.isInitialized) {
                // Create new instance for message database with jid
                messageDB = Room.databaseBuilder(
                    context, MessageDB::class.java,
                    DatabaseHelper.getMessageDatabase(context, user, me)
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return messageDB
        }
    }
}