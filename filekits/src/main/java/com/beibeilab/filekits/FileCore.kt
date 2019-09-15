package com.beibeilab.filekits

import android.content.Context
import android.os.Environment
import java.io.File

//  TODO by Batu: check free size
class FileCore(
    val context: Context?
) {

    fun getInternalDir(dirName: String? = null): File {
        if (context == null)
            throw java.lang.NullPointerException("You have to set Context to FileCore to access internal storage")

        return if (dirName.isNullOrEmpty()) {
            context.filesDir
        } else {
            File(context.filesDir, dirName).apply { if (!exists()) mkdirs() }
        }
    }

    fun getInternalCacheDir(dirName: String? = null): File {
        if (context == null)
            throw java.lang.NullPointerException("You have to set Context to FileCore to access internal storage")

        return if (dirName.isNullOrEmpty()) {
            context.cacheDir
        } else {
            File(context.filesDir, dirName).apply { if (!exists()) mkdirs() }
        }
    }

    fun getPrivateExternalDir(
        environment: String? = null,
        dirName: String? = null
    ): File {

        if (context == null)
            throw java.lang.NullPointerException("You have to set Context to FileCore to access private external storage")

        val root = context.getExternalFilesDir(environment)
            ?: throw NullPointerException("got Null Pointer when calling context.getExternalFilesDir")

        return if (dirName.isNullOrEmpty()) {
            root
        } else {
            File(root, dirName)
        }
    }

    fun getPublicExternalDir(
        environment: String? = null,
        dirName: String? = null
    ): File {
        val root =
            Environment.getExternalStoragePublicDirectory(environment)
                ?: throw NullPointerException("get Null Pointer when calling Environment.getExternalFilesDir")

        return if (dirName.isNullOrEmpty()) {
            root
        } else {
            File(root, dirName)
        }
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

}