package cc.imorning.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.database.entity.RosterEntity
import cc.imorning.database.entity.UserInfoEntity
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface DataDatabaseDao {

    /**
     * insert roster
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoster(rosterEntity: RosterEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoster(rosterEntity: RosterEntity)

    @Query("select * from ${DatabaseHelper.TABLE_ROSTER}")
    fun queryRosters(): List<RosterEntity>

    @Query("delete from ${DatabaseHelper.TABLE_ROSTER}")
    fun deleteAll()

    @Delete
    fun deleteRoster(rosterEntity: RosterEntity)

    /**
     * query all user information
     */
    @Query("select * from ${DatabaseHelper.TABLE_USER_INFO}")
    fun queryUserInfo(): LiveData<List<UserInfoEntity>>

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