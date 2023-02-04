package cc.imorning.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.common.CommonApp
import cc.imorning.database.converters.CommonConverter
import cc.imorning.database.dao.RecentDatabaseDao
import cc.imorning.database.entity.RecentMessageEntity
import cc.imorning.database.utils.DatabaseHelper

@Database(
    entities = [RecentMessageEntity::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(CommonConverter::class)
abstract class RecentDB : RoomDatabase() {

    abstract fun recentDatabaseDao(): RecentDatabaseDao

    companion object {
        private const val TAG = "RecentDB"
        private lateinit var recentDB: RecentDB
        fun getInstance(context: Context, jid: String): RecentDB {
            if (!this::recentDB.isInitialized) {
                //创建的数据库的实例
                recentDB = Room.databaseBuilder(
                    CommonApp.getContext(),
                    RecentDB::class.java,
                    DatabaseHelper.getDatabase(context, jid, DatabaseHelper.RECENT_MESSAGE_DB)
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return recentDB
        }
    }
}