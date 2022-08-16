package cc.imorning.common.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.common.constant.UserDatabaseConstant

@Entity(tableName = UserDatabaseConstant.TABLE_USER_INFO)
data class UserInfoEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "jid", typeAffinity = ColumnInfo.TEXT)
    val jid: String,

    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.TEXT)
    val username: String,

    @ColumnInfo(name = "email", typeAffinity = ColumnInfo.TEXT)
    val email: String? = "",

    @ColumnInfo(name = "status")
    val status: Int? = 0
)