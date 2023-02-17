package cc.imorning.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.database.converters.CommonConverter
import cc.imorning.database.converters.RosterConverter
import cc.imorning.database.dao.DataDatabaseDao
import cc.imorning.database.entity.RosterEntity
import cc.imorning.database.entity.UserInfoEntity
import cc.imorning.database.utils.DatabaseHelper

@Database(
    entities = [UserInfoEntity::class, RosterEntity::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(CommonConverter::class, RosterConverter::class)
abstract class DataDB : RoomDatabase() {

    abstract fun databaseDao(): DataDatabaseDao

    companion object {
        private const val TAG = "DataDB"
        private lateinit var dataDB: DataDB
        fun getInstance(context: Context, jid: String): DataDB {
            if (!this::dataDB.isInitialized) {
                // Create new instance for database
                dataDB = Room.databaseBuilder(
                    context,
                    DataDB::class.java,
                    DatabaseHelper.getDatabase(context, jid, DatabaseHelper.DATA_DB)
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return dataDB
        }
    }
}