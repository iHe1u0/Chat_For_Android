package cc.imorning.database.dao

import androidx.room.*
import cc.imorning.database.entity.RecentMessageEntity
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface RecentDatabaseDao {

    @Query("select * from ${DatabaseHelper.TABLE_RECENT_MESSAGE}")
    fun queryRecentMessage(): List<RecentMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceMessage(recentMessage: RecentMessageEntity)

    @Delete
    fun deleteMessage(recentMessage: RecentMessageEntity)
}