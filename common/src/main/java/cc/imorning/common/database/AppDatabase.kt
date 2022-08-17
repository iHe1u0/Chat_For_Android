package cc.imorning.common.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cc.imorning.chat.App
import cc.imorning.common.constant.DatabaseConstant
import cc.imorning.common.database.converters.DateTimeConverter
import cc.imorning.common.database.dao.AppDatabaseDao
import cc.imorning.common.database.table.RecentMessageEntity
import cc.imorning.common.database.table.UserInfoEntity

@Database(
    entities = [UserInfoEntity::class, RecentMessageEntity::class],
    version = 2,
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
                    App.getContext(),
                    AppDatabase::class.java,
                    DatabaseConstant.DATABASE_NAME
                )
                    .build()
            }
            return appDatabase
        }
    }
}