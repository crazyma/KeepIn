package com.beibeilab.keepin.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Flowable

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface AccountDao {
    @Query(
        "SELECT DISTINCT account FROM accountEntity " +
                "WHERE account IS NOT NULL"
    )
    fun getDistinctAccountEntity(): List<String>

    @Query(
        "SELECT DISTINCT account FROM accountEntity " +
                "WHERE account IS NOT NULL"
    )
    fun getDistinctAccountEntityFlowable(): Flowable<List<String>>

    @Query("SELECT * FROM accountEntity")
    fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accountEntity")
    fun getAllFlowable(): Flowable<List<AccountEntity>>

    @Query("SELECT * FROM accountEntity ORDER BY service_name ASC")
    fun getAllFromLiveData(): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM accountEntity ORDER BY service_name ASC")
    fun getAllFromLegacy(): List<AccountEntity>

    @Query("SELECT * FROM accountEntity WHERE service_name LIKE '%' || :searchKey  || '%' ORDER BY service_name ASC")
    fun searchEntityFromLegacy(searchKey: String): List<AccountEntity>

    @Query("SELECT * FROM accountEntity WHERE uid == :uid")
    fun getAccountEntityByUidRx(uid: Long): Flowable<AccountEntity>

    @Query("SELECT * FROM accountEntity WHERE uid == :uid")
    fun getAccountEntityByUid(uid: Long): LiveData<AccountEntity>

    @Insert
    fun insert(accountEntity: AccountEntity)

    @Update
    fun update(accountEntity: AccountEntity)

    @Delete
    fun delete(accountEntity: AccountEntity)
}