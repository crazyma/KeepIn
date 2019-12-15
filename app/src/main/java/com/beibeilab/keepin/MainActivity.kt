package com.beibeilab.keepin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beibeilab.keepin.compose.ComposeActivity
import com.beibeilab.keepin.extension.setupFragment
import com.beibeilab.keepin.frontpage.MainFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(ComposeActivity.getIntent(this))
        }

        if (savedInstanceState == null) {
            setupFragment(R.id.fragment_content, MainFragment())
        }
    }


    fun showFAB() {
        fab.show()
    }

    fun hideFAB() {
        fab.hide()
    }
}
