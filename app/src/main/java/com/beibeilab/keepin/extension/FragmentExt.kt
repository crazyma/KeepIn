package com.beibeilab.keepin.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.beibeilab.keepin.util.ViewModelFactory

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>): T =
    ViewModelProviders.of(this).get(viewModelClass)


fun <T : ViewModel> Fragment.obtainActivityViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(activity as FragmentActivity).get(viewModelClass)

fun <T : ViewModel> Fragment.obtainViewModel2(viewModelClass: Class<T>): T =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(activity!!.application)).get(
        viewModelClass
    )