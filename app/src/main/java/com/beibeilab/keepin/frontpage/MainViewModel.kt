package com.beibeilab.keepin.frontpage

import androidx.lifecycle.*
import com.beibeilab.batukits.EncryptKit
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
    private val accountDatabase: AccountDatabase,
    private val encryptKit: EncryptKit
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
                val backupJsonString = transToJson(accountList)
                isBackupDone.value = backup(backupJsonString)
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


    private suspend fun transToJson(accountList: List<AccountEntity>) =
        withContext(Dispatchers.Default) {
            accountList.map {

                val decryptedPassword = if (!it.pwd2.isNullOrEmpty()) { // encrypted password exist
                    encryptKit.runDecryption(it.pwd2!!)
                } else {
                    null
                }

                val finalPassword = if (decryptedPassword != null && it.pwd1.isEmpty()) {
                    decryptedPassword
                } else {
                    it.pwd1
                }

                it.getBackupEntity(finalPassword)
            }.let {
                Gson().toJson(it)
            }
        }!!

    private suspend fun backup(jsonString: String) = withContext(Dispatchers.IO) {
        try {
            fileManager.backup(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
        true
    }
}