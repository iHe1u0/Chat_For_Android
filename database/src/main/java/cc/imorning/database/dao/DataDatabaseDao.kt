package cc.imorning.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.database.entity.UserInfoEntity
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface DataDatabaseDao {

    /**
     * query all rosters
     */
    @Query("select * from ${DatabaseHelper.TABLE_USER_INFO}")
    fun queryRoster(): LiveData<List<UserInfoEntity>>

    /**
     * update local database after adding a roster
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfoEntity: UserInfoEntity)

    /**
     * remove [UserInfoEntity] entity when logout
     */
    @Delete
    suspend fun deleteUserInfo(userInfoEntity: UserInfoEntity)

    @Update
    suspend fun updateUserInfo(userInfoEntity: UserInfoEntity)
}