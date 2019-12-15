package com.beibeilab.keepin.util.pref

import android.content.Context

object PrefUtils {

    private const val Default = "KeepIn"

    fun getDefaultPrefs(context: Context) =
        context.getSharedPreferences(Default, Context.MODE_PRIVATE)


}