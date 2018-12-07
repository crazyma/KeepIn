package com.beibeilab.keepin.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.extension.setupFragment
import kotlinx.android.synthetic.main.activity_main.*

class ComposeActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, ComposeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)

        setupFragment(R.id.contentLayout, ComposeFragment())
    }

}