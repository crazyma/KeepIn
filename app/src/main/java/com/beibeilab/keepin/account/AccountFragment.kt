package com.beibeilab.keepin.account

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountEntity
import kotlinx.android.synthetic.main.content_account.*

class AccountFragment : Fragment(), AccountNavigator {

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
        setHasOptionsMenu(true)

        accountEntity = arguments!!.getParcelable(ARGS_ACCOUNT)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccountInfo()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_account, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_edit_account -> {
                jump2Edit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onCopyButtonClicked() {
        copyToClipboard(passwordTextView.text.toString())
    }

    private fun setupAccountInfo() {

        accountEntity.apply {

            if (isLessInfo()) {
                addMoreButton.visibility = View.VISIBLE
            } else {
                addMoreButton.visibility = View.GONE
            }

            if (account.isEmpty()) {
                accountTitleTextView.visibility = View.GONE
                accountImageView.visibility = View.GONE
                accountTextView.visibility = View.GONE
            } else {
                accountTitleTextView.visibility = View.VISIBLE
                accountImageView.visibility = View.VISIBLE
                accountTextView.visibility = View.VISIBLE
                accountTextView.text = account
            }

            if (userName.isNullOrEmpty()) {
                userNameTextView.visibility = View.GONE
                userNameTitleTextView.visibility = View.GONE
                userNameImageView.visibility = View.GONE
            } else {
                userNameTextView.visibility = View.VISIBLE
                userNameTitleTextView.visibility = View.VISIBLE
                userNameImageView.visibility = View.VISIBLE
                userNameTextView.text = userName
            }

            if (pwd1.isEmpty()) {
                passwordTextView.visibility = View.GONE
                passwordTitleTextView.visibility = View.GONE
                passwordImageView.visibility = View.GONE
                passwordButton.visibility = View.GONE
            } else {
                passwordTextView.visibility = View.VISIBLE
                passwordTitleTextView.visibility = View.VISIBLE
                passwordImageView.visibility = View.VISIBLE
                passwordButton.visibility = View.VISIBLE
                passwordTextView.text = userName
                passwordButton.setText(R.string.btn_copy_password)
                passwordButton.setOnClickListener { onCopyButtonClicked() }
            }

            if (email.isNullOrEmpty()) {
                emailTextView.visibility = View.GONE
                emailTitleTextView.visibility = View.GONE
                emailImageView.visibility = View.GONE
            } else {
                emailTextView.visibility = View.VISIBLE
                emailTitleTextView.visibility = View.VISIBLE
                emailImageView.visibility = View.VISIBLE
                emailTextView.text = email
            }

            if (remark.isNullOrEmpty()) {
                remarkTextView.visibility = View.GONE
                remarkTitleTextView.visibility = View.GONE
                remarkImageView.visibility = View.GONE
            } else {
                remarkTextView.visibility = View.VISIBLE
                remarkTitleTextView.visibility = View.VISIBLE
                remarkImageView.visibility = View.VISIBLE
                remarkTextView.text = remark
            }

            serviceNameTextView.text = serviceName
            userNameTextView.text = userName
            passwordTextView.text = pwd1
            emailTextView.text = email
            remarkTextView.text = remark

            oauthImageView.setImageResource(
                when (oauth) {
                    "google" -> R.drawable.icon_google
                    "facebook" -> R.drawable.icon_facebook
                    "github" -> R.drawable.icon_github
                    "twitter" -> R.drawable.icon_twitter
                    else -> 0
                }
            )
        }
    }

    private fun copyToClipboard(str: String){
        val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("text label", str)
        clipboard.primaryClip = clip

        Toast.makeText(context!!, "Password Copied!", Toast.LENGTH_SHORT).show()
    }

    private fun jump2Edit() {

    }

}