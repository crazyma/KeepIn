package com.beibeilab.keepin.compose

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beibeilab.batukits.EncryptKit
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.util.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ComposeViewModel(
    private val encryptKit: EncryptKit,
    private val accountDatabase: AccountDatabase
) : ViewModel() {

    val color = MutableLiveData<Int>()
    val generatedPassword = SingleLiveEvent<String>()
    val insertDone = SingleLiveEvent<Void>()
    val updateDone = SingleLiveEvent<Void>()

    fun initColor(c: Int) {
        color.value = c
    }

    fun commitNewAccount(accountEntity: AccountEntity) {
        accountDatabase.apply {
            Completable.fromRunnable {
                val pwd1 = accountEntity.pwd1
                if (pwd1.isNotEmpty()) {
                    accountEntity.pwd2 = encryptKit.runEncryption(pwd1)
                    accountEntity.pwd1 = ""
                }

                accountEntity.color = color.value!!

                getAccountDao().insert(accountEntity)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("badu", "insert done")
                    insertDone.call()
                }, {
                    Log.d("badu", "insert fail")
                    Log.e("badu", it.toString())
                })
        }
    }

    fun commitSample(accountList: List<AccountEntity>) {
        accountDatabase.apply {
            Flowable.fromIterable(accountList)
                .flatMapCompletable {
                    Completable.fromCallable {
                        getAccountDao().insert(it)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("badu", "insert done")
                    insertDone.call()
                }, {
                    Log.d("badu", "insert fail")
                    Log.e("badu", it.toString())
                })
        }
    }

    fun updateAccount(accountEntity: AccountEntity) {
        accountDatabase.apply {
            Completable.fromRunnable {
                val pwd1 = accountEntity.pwd1
                if (pwd1.isNotEmpty()) {
                    accountEntity.pwd2 = encryptKit.runEncryption(pwd1)
                    accountEntity.pwd1 = ""
                }

                accountEntity.color = color.value!!

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