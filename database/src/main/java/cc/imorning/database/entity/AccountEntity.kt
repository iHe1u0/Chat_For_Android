package cc.imorning.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.imorning.database.utils.DatabaseHelper

/**
 * Represents an account entity with account details and authentication token.
 *
 * @property account The account name.
 * @property token The authentication token.
 * @property expireTime The expiration time of the token in milliseconds since the epoch.
 */
@Entity(tableName = DatabaseHelper.ACCOUNT_DB)
data class AccountEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "account", typeAffinity = ColumnInfo.TEXT)
    val account: String,

    @ColumnInfo(name = "token", typeAffinity = ColumnInfo.TEXT)
    val token: String,

    @ColumnInfo(name = "expired_time", typeAffinity = ColumnInfo.TEXT)
    val expiredTime: Long
)
