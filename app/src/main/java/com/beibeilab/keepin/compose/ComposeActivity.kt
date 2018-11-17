package com.beibeilab.keepin.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.beibeilab.keepin.R
import kotlinx.android.synthetic.main.activity_main.*

class ComposeActivity: AppCompatActivity()  {

    companion object {
        fun getIntent(context: Context) = Intent(context, ComposeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)

        setupFragment()
    }

    private fun setupFragment(){
        supportFragmentManager.beginTransaction().apply {

           add(R.id.contentLayout, ComposeFragment())

        }.commit()
    }


}