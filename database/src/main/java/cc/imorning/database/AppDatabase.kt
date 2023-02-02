package cc.imorning.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.common.CommonApp
import cc.imorning.database.converters.DateTimeConverter
import cc.imorning.database.dao.AppDatabaseDao
import cc.imorning.database.table.RecentMessageEntity
import cc.imorning.database.table.UserInfoEntity
import cc.imorning.database.utils.DatabaseConstant

@Database(
    entities = [UserInfoEntity::class, RecentMessageEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDatabaseDao(): AppDatabaseDao

    companion object {
        private lateinit var appDatabase: AppDatabase

        fun getInstance(): AppDatabase {
            if (!this::appDatabase.isInitialized) {
                //创建的数据库的实例
                appDatabase = Room.databaseBuilder(
                    CommonApp.getContext(),
                    AppDatabase::class.java,
                    DatabaseConstant.DATABASE_NAME
                ).build()
            }
            return appDatabase
        }
    }
}