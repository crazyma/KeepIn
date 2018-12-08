package com.beibeilab.keepin.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountEntity

class AccountFragment : Fragment() {

    companion object {

        const val ARGS_ACCOUNT = "ARGS_ACCOUNT"

        fun newInstance(accountEntity: AccountEntity) = AccountFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARGS_ACCOUNT, accountEntity)
            }
        }
    }

    lateinit var accountEntity: AccountEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountEntity = arguments!!.getParcelable(ARGS_ACCOUNT)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}