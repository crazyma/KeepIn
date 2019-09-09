package com.beibeilab.keepin.file

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import java.io.File
import java.io.FileOutputStream

class FileManager(
    private val fileCore: FileCore,
    private val fileOperator:FileOperator
) {

    companion object {
        const val DIR_BACKUP = "backup"
    }

    fun backup(content: String){
        val byteArray = content.toByteArray()
        val file = File(getBackupDir(),"backup.txt")
        fileOperator.writeByteViaOutputStream(file, byteArray)
    }

    fun testImage(bitmap: Bitmap){
        val file = File(getBackupDir(),"backup.jpg")
        FileOutputStream(file.path).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
    }

    fun getUri(){

    }

    private fun getBackupDir(): File {
        return fileCore.getPublicExternalDir(Environment.DIRECTORY_DOWNLOADS, DIR_BACKUP).apply { if (!exists()) mkdir() }
    }
}