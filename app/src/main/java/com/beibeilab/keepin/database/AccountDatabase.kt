package com.beibeilab.keepin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccountEntity::class], version = 1)
abstract class AccountDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AccountDatabase? = null

        fun getInstance(context: Context): AccountDatabase = INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            AccountDatabase::class.java,
            "db-account"
        ).build().apply { INSTANCE = this }
    }

    abstract fun getAccountDao(): AccountDao
}