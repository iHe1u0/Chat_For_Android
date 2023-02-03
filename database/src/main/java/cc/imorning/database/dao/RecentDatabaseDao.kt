package cc.imorning.database.dao

import androidx.room.*
import cc.imorning.database.table.RecentMessageTable
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface RecentDatabaseDao {

    @Query("select * from ${DatabaseHelper.TABLE_RECENT_MESSAGE}")
    fun queryRecentMessage(): List<RecentMessageTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentMessage(recentMessage: RecentMessageTable)

}