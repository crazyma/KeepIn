package com.beibeilab.keepin.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class AccountEntity(
    @SerializedName("service_name") @ColumnInfo(name = "service_name") var serviceName: String,
    @SerializedName("oauth") @ColumnInfo(name = "oauth") var oauth: String,
    @SerializedName("account") @ColumnInfo(name = "account") var account: String,
    @SerializedName("pwd1") @ColumnInfo(name = "pwd1") var pwd1: String,
    @SerializedName("color") @ColumnInfo(name = "color") var color: Int,
    @PrimaryKey(autoGenerate = true) var uid: Long,
    @SerializedName("pwd2") @ColumnInfo(name = "pwd2") var pwd2: String?,
    @SerializedName("salt") @ColumnInfo(name = "salt") var salt: String?,
    @SerializedName("user_name") @ColumnInfo(name = "user_name") var userName: String?,
    @SerializedName("email") @ColumnInfo(name = "email") var email: String?,
    @SerializedName("remark") @ColumnInfo(name = "remark") var remark: String?
) : Parcelable {

    constructor(serviceName: String, oauth: String, account: String, pwd1: String, color: Int) :
            this(
                serviceName, oauth, account, pwd1, color,
                0, null, null, null, null, null
            )

    constructor(serviceName: String, oauth: String, account: String, pwd1: String, color: Int,
                userName: String?, email: String?, remark: String?):
            this(
                serviceName, oauth, account, pwd1, color,
                0, null, null,userName, email, remark
            )


    fun isLessInfo() =
        account.isEmpty() && userName.isNullOrEmpty() && pwd1.isEmpty() &&
                email.isNullOrEmpty() && remark.isNullOrEmpty()

    fun getBackupEntity(password: String): AccountEntity{
        return AccountEntity(
            serviceName, oauth, account, password, color,
            userName, email,remark
        )
    }
}