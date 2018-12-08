package com.beibeilab.keepin.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>): T {
    return ViewModelProviders.of(this).get(viewModelClass)
}

fun <T : ViewModel> Fragment.obtainActivityViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(activity as FragmentActivity).get(viewModelClass)