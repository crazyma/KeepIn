package com.beibeilab.keepin.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>): T {
    return ViewModelProviders.of(this).get(viewModelClass)
}