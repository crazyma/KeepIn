package com.beibeilab.keepin.extension

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.setupFragment(layoutResId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {

        add(layoutResId, fragment)

    }.commit()
}

fun AppCompatActivity.replaceFragment(layoutResId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().apply {
        replace(layoutResId, fragment)
        addToBackStack(null)
    }.commit()
}