package com.beibeilab.keepin.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beibeilab.batukits.EncryptKit
import com.beibeilab.filekits.FileCore
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.frontpage.MainViewModel
import com.beibeilab.keepin.util.pref.PrefUtils

class ViewModelFactory(
    application: Application
) : ViewModelProvider.NewInstanceFactory() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(
            application: Application
        ) = INSTANCE ?: synchronized(ViewModelFactory::class.java) {
            INSTANCE ?: ViewModelFactory(
                application = application
            ).also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private val encryptKit = EncryptKit.Factory(application).create()
    private val fileCore: FileCore = FileCore(application)
    private val accountDatabase: AccountDatabase = AccountDatabase.getInstance(application)
    private val defaultPrefs: SharedPreferences = PrefUtils.getDefaultPrefs(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(fileCore, accountDatabase, encryptKit, defaultPrefs)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }

}