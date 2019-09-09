package com.beibeilab.keepin.frontpage

import android.graphics.*
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
import java.io.FileOutputStream

class MainViewModel(
    fileCore: FileCore
) : ViewModel() {

    lateinit var accountDatabase: AccountDatabase
    private val fileOperator = FileOperator()
    private val fileManager = FileManager(fileCore, fileOperator)

    val testSingleEvent = SingleLiveEvent<Bitmap>()
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

    fun test(size: Float){
        val half = size * .5f

        val bitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.BLUE
        }

        val path = Path().apply {
            moveTo(half, 0f)
            lineTo(size, half)
            lineTo(half, size)
            lineTo(0f, half)
            close()
        }
        canvas.drawPath(path, paint)

        fileManager.testImage(bitmap)

    }

}