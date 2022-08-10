package com.imorning.common.database.dao

import androidx.room.*
import com.imorning.common.constant.UserDatabaseConstant
import com.imorning.common.database.table.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {

    @Query("select * from ${UserDatabaseConstant.TABLE_USER_INFO}")
    fun getAllContact(): Flow<List<UserInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(userInfoEntity: UserInfoEntity)

    @Delete
    fun deleteContact(userInfoEntity: UserInfoEntity)

    @Update
    fun updateContact(userInfoEntity: UserInfoEntity)

    @Query("delete from ${UserDatabaseConstant.TABLE_USER_INFO}")
    fun deleteAllContact()

}