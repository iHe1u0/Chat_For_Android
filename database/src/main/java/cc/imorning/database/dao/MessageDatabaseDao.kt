package cc.imorning.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.imorning.database.entity.MessageTable
import cc.imorning.database.utils.DatabaseHelper

@Dao
interface MessageDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(messageTable: MessageTable)

    @Query("select * from ${DatabaseHelper.TABLE_MESSAGE}")
    fun queryMessage(): List<MessageTable>

}