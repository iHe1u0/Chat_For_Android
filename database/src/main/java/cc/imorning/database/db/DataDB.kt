package cc.imorning.database.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.common.CommonApp
import cc.imorning.database.BuildConfig
import cc.imorning.database.converters.DateTimeConverter
import cc.imorning.database.converters.RosterTypeConverter
import cc.imorning.database.dao.DataDatabaseDao
import cc.imorning.database.table.RosterTable
import cc.imorning.database.table.UserInfoTable
import cc.imorning.database.utils.DatabaseHelper

@Database(
    entities = [UserInfoTable::class, RosterTable::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class, RosterTypeConverter::class)
abstract class DataDB : RoomDatabase() {

    abstract fun databaseDao(): DataDatabaseDao

    companion object {
        private const val TAG = "DataDB"
        private lateinit var dataDB: DataDB
        fun getInstance(context: Context, jid: String): DataDB {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "get database Instance: $jid")
            }
            if (!this::dataDB.isInitialized) {
                //创建的数据库的实例
                dataDB = Room.databaseBuilder(
                    CommonApp.getContext(),
                    DataDB::class.java,
                    DatabaseHelper.getDatabase(context, jid, DatabaseHelper.RECENT_MESSAGE_DB)
                ).build()
            }
            return dataDB
        }
    }
}