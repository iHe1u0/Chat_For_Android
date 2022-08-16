package cc.imorning.common.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.common.constant.DatabaseConstant

@Entity(tableName = DatabaseConstant.TABLE_RECENT_MESSAGE)
data class RecentMessageEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int? = null,

    val lastName: String?

)