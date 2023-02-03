package cc.imorning.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.database.table.UserInfoTable
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface DataDatabaseDao {

    /**
     * query all rosters
     */
    @Query("select * from ${DatabaseHelper.TABLE_USER_INFO}")
    fun queryRoster(): LiveData<List<UserInfoTable>>

    /**
     * update local database after adding a roster
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfoTable: UserInfoTable)

    /**
     * remove [UserInfoTable] table when logout
     */
    @Delete
    suspend fun deleteUserInfo(userInfoTable: UserInfoTable)

    @Update
    suspend fun updateUserInfo(userInfoTable: UserInfoTable)
}