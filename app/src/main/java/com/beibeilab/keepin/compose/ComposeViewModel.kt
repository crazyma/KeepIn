package com.beibeilab.keepin.compose

import android.util.Log
import androidx.lifecycle.ViewModel
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class ComposeViewModel : ViewModel() {

    var accountDatabase: AccountDatabase? = null

    fun commitNewAccount(accountEntity: AccountEntity) {
        accountDatabase?.apply {

            Completable.fromRunnable {
                getAccountDao().insert(accountEntity)
            }.subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("badu", "insert done")
                }, {
                    Log.d("badu", "insert fail")
                })
        }
    }

}