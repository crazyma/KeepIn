package com.beibeilab.keepin.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class AccountEntity(
    @ColumnInfo(name = "service_name") var serviceName: String,
    @ColumnInfo(name = "oauth") var oauth: String,
    @ColumnInfo(name = "account") var account: String,
    @ColumnInfo(name = "pwd1") var pwd1: String,
    @ColumnInfo(name = "color") var color: Int,
    @PrimaryKey(autoGenerate = true) var uid: Long,
    @ColumnInfo(name = "pwd2") var pwd2: String?,
    @ColumnInfo(name = "salt") var salt: String?,
    @ColumnInfo(name = "user_name") var userName: String?,
    @ColumnInfo(name = "email") var email: String?,
    @ColumnInfo(name = "remark") var remark: String?
) : Parcelable {

    constructor(serviceName: String, oauth: String, account: String, pwd1: String, color: Int) :
            this(
                serviceName, oauth, account, pwd1, color,
                0, null, null, null, null, null
            )


    fun isLessInfo() =
        account.isEmpty() && userName.isNullOrEmpty() && pwd1.isEmpty() &&
                email.isNullOrEmpty() && remark.isNullOrEmpty()
}