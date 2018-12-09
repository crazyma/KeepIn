package com.beibeilab.keepin.compose

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.util.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ComposeViewModel : ViewModel() {

    var accountDatabase: AccountDatabase? = null
    val color = MutableLiveData<Int>()
    val generatedPassword = SingleLiveEvent<String>()
    val insertDone = SingleLiveEvent<Void>()
    val updateDone = SingleLiveEvent<Void>()

    fun initColor(c: Int){
        color.value = c
    }

    fun commitNewAccount(accountEntity: AccountEntity) {
        accountDatabase?.apply {

            accountEntity.color = color.value!!

            Completable.fromRunnable {
                getAccountDao().insert(accountEntity)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("badu", "insert done")
                    insertDone.call()
                }, {
                    Log.d("badu", "insert fail")
                })
        }
    }

    fun updateAccount(accountEntity: AccountEntity) {
        accountDatabase?.apply {

            accountEntity.color = color.value!!

            Completable.fromRunnable {
                getAccountDao().update(accountEntity)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("badu", "update done")
                    updateDone.call()
                }, {
                    Log.d("badu", "update fail")
                })
        }
    }

}