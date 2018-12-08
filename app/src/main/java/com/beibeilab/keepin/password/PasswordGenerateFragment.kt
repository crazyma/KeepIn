package com.beibeilab.keepin.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beibeilab.keepin.R

class PasswordGenerateFragment: DialogFragment() {

    companion object {
        fun newInstance() = PasswordGenerateFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password_generate, container, false)
    }

}