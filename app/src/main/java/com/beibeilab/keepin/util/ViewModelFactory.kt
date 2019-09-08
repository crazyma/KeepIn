package com.beibeilab.keepin.util

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beibeilab.filekits.FileCore
import com.beibeilab.keepin.frontpage.MainViewModel

class ViewModelFactory (
    application: Application
): ViewModelProvider.NewInstanceFactory(){

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: ViewModelFactory? = null

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

    private val fileCore: FileCore = FileCore(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(fileCore)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }

}