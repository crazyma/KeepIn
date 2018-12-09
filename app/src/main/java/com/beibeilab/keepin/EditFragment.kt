package com.beibeilab.keepin

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.beibeilab.keepin.compose.ComposeFragment
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainActivityViewModel
import com.beibeilab.keepin.extension.parseText
import kotlinx.android.synthetic.main.content_compose.*
import kotlinx.android.synthetic.main.content_edit_attr.*

class EditFragment : ComposeFragment() {

    companion object {
        const val ARGS_ACCOUNT = "ARGS_ACCOUNT"

        fun newInstance(bundle: Bundle) = EditFragment().apply {
            arguments = bundle
        }
    }

    private lateinit var givenAccountEntity: AccountEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        givenAccountEntity = arguments?.getParcelable(ARGS_ACCOUNT)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGivenInfo()
    }

    override fun setupViewModel() {
        super.setupViewModel()

        viewModel.jobDone.observe(viewLifecycleOwner, Observer {
            activity!!.apply{
                setResult(RESULT_OK)
                finish()
            }
        })
    }

    override fun collectAccountInfo(): AccountEntity {
        val serviceName = serviceNameEditText.parseText()
        val accountName = accountEditText.parseText()
        val userName = userNameEditText.parseText()
        val password = passwordEditText.parseText()
        val email = emailEditText.parseText()
        val remark = remarkEditText.parseText()
        val oauth = getSelectedService()

        return givenAccountEntity.apply {
            this.serviceName = serviceName
            this.account = accountName
            this.userName = userName
            this.pwd1 = password
            this.email = email
            this.remark = remark
            this.oauth = oauth
        }
    }

    override fun onCommitButtonClicked() {
        viewModel.updateAccount(collectAccountInfo())
    }

    private fun setupGivenInfo() {
        serviceNameEditText.setText(givenAccountEntity.serviceName)
        accountEditText.setText(givenAccountEntity.account)
        userNameEditText.setText(givenAccountEntity.userName)
        passwordEditText.setText(givenAccountEntity.pwd1)
        emailEditText.setText(givenAccountEntity.email)
        remarkEditText.setText(givenAccountEntity.remark)

        when (givenAccountEntity.oauth) {
            "google" -> googleImageView.isSelected = true
            "facebook" -> facebookImageView.isSelected = true
            "github" -> githubImageView.isSelected = true
            "twitter" -> twitterImageView.isSelected = true
        }

        setupColorPickerButton(givenAccountEntity.color)
    }

}