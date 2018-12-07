package com.beibeilab.keepin.extension

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

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