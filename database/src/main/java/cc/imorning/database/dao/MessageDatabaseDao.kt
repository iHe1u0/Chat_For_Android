package cc.imorning.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MessageDatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage()
}