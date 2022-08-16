package cc.imorning.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.common.constant.UserDatabaseConstant
import cc.imorning.common.database.table.UserInfoEntity

@Dao
interface UserInfoDao {

    @Query("select * from ${UserDatabaseConstant.TABLE_USER_INFO}")
    fun getAllContact(): LiveData<List<UserInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(userInfoEntity: UserInfoEntity)

    @Delete
    fun deleteContact(userInfoEntity: UserInfoEntity)

    @Update
    fun updateContact(userInfoEntity: UserInfoEntity)

    @Query("delete from ${UserDatabaseConstant.TABLE_USER_INFO}")
    fun deleteAllContact()

}