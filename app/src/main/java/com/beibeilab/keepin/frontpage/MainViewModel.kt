package com.beibeilab.keepin.frontpage

import androidx.lifecycle.*
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.file.FileManager
import com.beibeilab.keepin.util.SingleLiveEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    fileCore: FileCore,
    private val accountDatabase: AccountDatabase
) : ViewModel() {

    private val fileOperator = FileOperator()
    private val fileManager = FileManager(fileCore, fileOperator)

    val noDataEvent = SingleLiveEvent<Void>()
    val isBackupDone = SingleLiveEvent<Boolean>()
    val readBackupFailed = SingleLiveEvent<java.lang.Exception>()

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
        viewModelScope.launch {
            val accountList = accountList.value
            if (accountList.isNullOrEmpty()) {
                noDataEvent.call()
            } else {
                isBackupDone.value = backup(accountList)
            }
        }
    }

    fun handleRestoreRequest() {
        viewModelScope.launch {
            try {
                readBackupFile()?.run {
                    val list = transformJson(this)
                    storeToDatabase(list)
                } ?: run {
                    readBackupFailed.value = NullPointerException("Not read available backup file.")
                }

            } catch (e: Exception) {
                readBackupFailed.value = e
            }
        }
    }

    private suspend fun readBackupFile() = withContext(Dispatchers.IO) {
        fileManager.restore()
    }

    private suspend fun transformJson(jsonString: String) = withContext(Dispatchers.Default) {
        val listType = object : TypeToken<List<AccountEntity>>() {}.type
        Gson().fromJson<List<AccountEntity>>(jsonString, listType)
    }

    private suspend fun storeToDatabase(accounts: List<AccountEntity>) =
        withContext(Dispatchers.IO) {
            accounts.forEach {
                accountDatabase.getAccountDao().insert(it)
            }
        }

    private suspend fun backup(accountList: List<AccountEntity>) =
        withContext(Dispatchers.IO) {
            try {
                val jsonString = Gson().toJson(accountList)

                fileManager.backup(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
}