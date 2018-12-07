package com.beibeilab.keepin.compose

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.*
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainViewModel
import com.beibeilab.keepin.extension.parseText
import com.beibeilab.keepin.util.Utils
import kotlinx.android.synthetic.main.content_compose.*
import kotlinx.android.synthetic.main.content_edit_attr.*

class ComposeFragment : Fragment(), ComposeNavigator, IComposeView {

    private lateinit var viewModel: ComposeViewModel
    private val dp = Resources.getSystem().displayMetrics.density

    private val serviceButtonClickListener = View.OnClickListener {
        it!!.apply {
            googleImageView.isSelected = googleImageView == this && !googleImageView.isSelected
            facebookImageView.isSelected = facebookImageView == this && !facebookImageView.isSelected
            twitterImageView.isSelected = twitterImageView == this && !twitterImageView.isSelected
            githubImageView.isSelected = githubImageView == this && !githubImageView.isSelected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = obtainViewModel(ComposeViewModel::class.java).apply {
            accountDatabase = AccountDatabase.getInstance(context!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorPickerButton()
        setupServiceButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_compose, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_save_account -> {
                onCommitButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onCommitButtonClicked() {
        viewModel.commitNewAccount(collectAccountInfo())
    }

    override fun collectAccountInfo(): AccountEntity {
        val serviceName = serviceNameEditText.parseText()
        val accountName = accountEditText.parseText()
        val userName = userNameEditText.parseText()
        val password = passwordEditText.parseText()
        val email = emailEditText.parseText()
        val remark = remarkEditText.parseText()
        val oauth = getSelectedService()

        return AccountEntity(serviceName, oauth, accountName, password, 0).apply {
            this.userName = userName
            this.email = email
            this.remark = remark
        }
    }

    private fun setupColorPickerButton() {
        colorPickerImageView.background = Utils.createOvalDrawable(
            ContextCompat.getColor(context!!, R.color.colorPrimary),
            (30 * dp).toInt()
        )
    }

    private fun setupServiceButton() {
        googleImageView.setOnClickListener(serviceButtonClickListener)
        twitterImageView.setOnClickListener(serviceButtonClickListener)
        facebookImageView.setOnClickListener(serviceButtonClickListener)
        githubImageView.setOnClickListener(serviceButtonClickListener)
    }

    private fun getSelectedService() = when {
        googleImageView.isSelected -> "google"
        facebookImageView.isSelected -> "facebook"
        twitterImageView.isSelected -> "twitter"
        githubImageView.isSelected -> "github"
        else -> ""
    }
}