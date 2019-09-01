package com.beibeilab.keepin.file

import com.beibeilab.filekits.FileCore
import java.io.File

class FileManager(
    private val fileCore: FileCore
) {

    companion object {
        const val DIR_BACKUP = "backup"
    }

    fun getBackupDir(): File {
        return fileCore.getPublicExternalDir(DIR_BACKUP).apply { if (!exists()) mkdir() }
    }
}