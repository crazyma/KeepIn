package com.beibeilab.keepin.frontpage

import android.util.Log
import androidx.lifecycle.*
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.file.FileManager
import com.beibeilab.keepin.util.SingleLiveEvent
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    fileCore: FileCore
) : ViewModel() {

    lateinit var accountDatabase: AccountDatabase
    private val fileOperator = FileOperator()
    private val fileManager = FileManager(fileCore, fileOperator)

    val noDataEvent = SingleLiveEvent<Void>()
    val isBackupDone = SingleLiveEvent<Boolean>()

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
                val data = fileManager.restore()
                Log.i("badu", "data : $data")
            } catch (e: Exception){
                Log.e("badu", "$e")
            }
        }
    }

    private suspend fun backup(accountList: List<AccountEntity>) =
        withContext(Dispatchers.IO) {
            try {
                val jsonString = Gson().toJson(accountList)
                Log.d("badu", jsonString)

                fileManager.backup(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
            true
        }
}