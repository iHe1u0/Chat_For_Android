package cc.imorning.common.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.common.constant.UserDatabaseConstant

@Entity(tableName = UserDatabaseConstant.TABLE_MESSAGE)
data class MessageEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int? = null,

    val lastName: String?

)