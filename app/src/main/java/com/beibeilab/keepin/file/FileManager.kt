package com.beibeilab.keepin.file

import android.os.Environment
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import java.io.File

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

    private fun getBackupDir(): File {
        return fileCore.getPublicExternalDir(Environment.DIRECTORY_DOWNLOADS, DIR_BACKUP).apply { if (!exists()) mkdir() }
    }
}