package com.beibeilab.keepin.file

import android.os.Environment
import com.beibeilab.filekits.FileCore
import com.beibeilab.filekits.FileOperator
import java.io.File
import java.io.IOException

class FileManager(
    private val fileCore: FileCore,
    private val fileOperator: FileOperator
) {

    companion object {
        const val DIR_APP_FOLDER = "KeepIn"
        const val FILE_BACKUP = "backup.txt"
    }

    @Throws(IOException::class)
    fun backup(content: String) {
        val byteArray = content.toByteArray()
        val file = File(getBackupDir(), FILE_BACKUP)
        fileOperator.writeByteViaOutputStream(file, byteArray)
    }

    @Throws(IOException::class)
    fun restore(): String? {
        val file = File(getBackupDir(), FILE_BACKUP)
        return if (file.isFile && file.exists() && file.canRead()) {
            fileOperator.readFileViaInputStream(file)
        } else {
            null
        }
    }

    private fun getBackupDir(): File {
        return fileCore.getPublicExternalDir(Environment.DIRECTORY_DOWNLOADS)
            .apply { if (!exists()) mkdir() }
    }
}