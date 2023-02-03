package cc.imorning.database.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.common.CommonApp
import cc.imorning.database.dao.RecentDatabaseDao
import cc.imorning.database.BuildConfig
import cc.imorning.database.converters.DateTimeConverter
import cc.imorning.database.table.RecentMessageTable
import cc.imorning.database.table.UserInfoTable
import cc.imorning.database.utils.DatabaseHelper

@Database(
    entities = [RecentMessageTable::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(DateTimeConverter::class)
abstract class RecentDB : RoomDatabase() {

    abstract fun recentDatabaseDao(): RecentDatabaseDao

    companion object {
        private const val TAG = "RecentDB"
        private lateinit var recentDB: RecentDB
        fun getInstance(context: Context, jid: String): RecentDB {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "get database Instance: $jid")
            }
            if (!this::recentDB.isInitialized) {
                //创建的数据库的实例
                recentDB = Room.databaseBuilder(
                    CommonApp.getContext(),
                    RecentDB::class.java,
                    DatabaseHelper.getDatabase(context, jid, DatabaseHelper.RECENT_MESSAGE_DB)
                ).build()
            }
            return recentDB
        }
    }
}