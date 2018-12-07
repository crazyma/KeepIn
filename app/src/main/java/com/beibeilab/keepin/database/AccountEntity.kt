package com.beibeilab.keepin.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @ColumnInfo(name = "service_name") var serviceName: String?,
    @ColumnInfo(name = "oauth") var oauth: String?,
    @ColumnInfo(name = "account") var account: String?,
    @ColumnInfo(name = "pwd1") var pwd1: String?,
    @ColumnInfo(name = "color") var color: Int
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
    @ColumnInfo(name = "pwd2")
    var pwd2: String? = null
    @ColumnInfo(name = "salt")
    var salt: String? = null
    @ColumnInfo(name = "user_name")
    var userName: String? = null
    @ColumnInfo(name = "email")
    var email: String? = null
    @ColumnInfo(name = "remark")
    var remark: String? = null
}