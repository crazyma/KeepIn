package com.beibeilab.keepin.frontpage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.file.FileManager
import com.beibeilab.keepin.util.SingleLiveEvent
import com.google.gson.Gson

class MainViewModel(
    fileCore: FileCore
) : ViewModel() {

    lateinit var accountDatabase: AccountDatabase
    private val fileOperator = FileOperator()
    private val fileManager = FileManager(fileCore, fileOperator)

    val showDialog = SingleLiveEvent<Boolean>()

    private val accountListObservable = MutableLiveData<Void>()
    val accountList: LiveData<List<AccountEntity>>

    init {
        accountList = Transformations.switchMap(accountListObservable) {
            accountDatabase.getAccountDao().getAllFromLiveData()
        }
    }

    fun loadAccountList() {
        accountListObservable.value = null
    }

    fun handleBackupRequest() {
        showDialog.value = accountList.value.isNullOrEmpty().also { isDataEmpty ->
            if (!isDataEmpty) {
                getBackup()
            }
        }
    }

    fun getBackup() {
        accountList.value?.run {
            val jsonString = Gson().toJson(this)
            Log.d("badu", jsonString)

            fileManager.backup(jsonString)
        }
    }

}