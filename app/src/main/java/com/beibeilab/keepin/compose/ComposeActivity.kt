package com.beibeilab.keepin.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beibeilab.keepin.EditFragment
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.setupFragment
import kotlinx.android.synthetic.main.activity_main.*

class ComposeActivity : AppCompatActivity() {

    companion object {

        const val ARGS_ACCOUNT = "ARGS_ACCOUNT"
        const val ARGS_MODE = "ARGS_MODE"
        const val ARGS_MODE_COMPOSE = 0x01
        const val ARGS_MODE_EDIT = 0x10

        fun getIntent(context: Context) = Intent(context, ComposeActivity::class.java)

        fun getIntent(context: Context, accountEntity: AccountEntity) =
            Intent(context, ComposeActivity::class.java).apply {
                putExtra(ARGS_MODE, ARGS_MODE_EDIT)
                Bundle().apply {
                    putParcelable(ARGS_ACCOUNT, accountEntity)
                }.also {
                    this.putExtras(it)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            val fragment = intent.getIntExtra(ARGS_MODE, ARGS_MODE_COMPOSE).let {
                when (it) {
                    ARGS_MODE_EDIT -> EditFragment.newInstance(intent.extras!!)
                    else -> ComposeFragment()
                }
            }
            setupFragment(R.id.contentLayout, fragment)
        }
    }

}