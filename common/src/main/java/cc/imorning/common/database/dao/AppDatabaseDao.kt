package cc.imorning.common.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.imorning.common.constant.DatabaseConstant
import cc.imorning.common.database.table.UserInfoEntity

@Dao
interface AppDatabaseDao {

    @Query("select * from ${DatabaseConstant.TABLE_CONTACT_INFO}")
    fun getAllContact(): LiveData<List<UserInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(userInfoEntity: UserInfoEntity)

    @Delete
    fun deleteContact(userInfoEntity: UserInfoEntity)

    @Update
    fun updateContact(userInfoEntity: UserInfoEntity)

    @Query("delete from ${DatabaseConstant.TABLE_CONTACT_INFO}")
    fun deleteAllContact()

}