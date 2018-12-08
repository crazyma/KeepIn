package com.beibeilab.keepin.frontpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity

class MainViewModel: ViewModel() {

    lateinit var accountDatabase: AccountDatabase

    val accountList: LiveData<List<AccountEntity>>
        get() = accountDatabase.getAccountDao().getAllFromLiveData()

}