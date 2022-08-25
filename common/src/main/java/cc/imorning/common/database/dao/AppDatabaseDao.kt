package cc.imorning.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.common.constant.DatabaseConstant
import cc.imorning.common.database.table.RecentMessageEntity
import cc.imorning.common.database.table.UserInfoEntity

@Dao
interface AppDatabaseDao {

    @Query("select * from ${DatabaseConstant.TABLE_CONTACT_INFO}")
    fun getAllContact(): LiveData<List<UserInfoEntity>>

    @Query("select * from ${DatabaseConstant.TABLE_RECENT_MESSAGE}")
    suspend fun getAllRecentMessages(): List<RecentMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(userInfoEntity: UserInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentMessage(recentMessage: RecentMessageEntity)

    @Delete
    suspend fun deleteContact(userInfoEntity: UserInfoEntity)

    @Update
    suspend fun updateContact(userInfoEntity: UserInfoEntity)

    @Query("delete from ${DatabaseConstant.TABLE_CONTACT_INFO}")
    suspend fun deleteAllContact()

    @Query("delete from ${DatabaseConstant.TABLE_RECENT_MESSAGE}")
    suspend fun deleteAllRecentMessage()

}