package com.beibeilab.keepin.compose

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.*
import androidx.lifecycle.Observer
import com.android.colorpicker.ColorPickerDialog
import com.android.colorpicker.ColorPickerSwatch
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainActivityViewModel
import com.beibeilab.keepin.extension.parseText
import com.beibeilab.keepin.password.PasswordGenerateFragment
import com.beibeilab.keepin.util.Utils
import kotlinx.android.synthetic.main.content_compose.*
import kotlinx.android.synthetic.main.content_edit_attr.*
import java.lang.RuntimeException

open class ComposeFragment : Fragment(), ComposeNavigator, IComposeView, ColorPickerSwatch.OnColorSelectedListener {

    protected lateinit var viewModel: ComposeViewModel
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

        viewModel = obtainActivityViewModel(ComposeViewModel::class.java).apply {
            accountDatabase = AccountDatabase.getInstance(context!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupServiceButton()
        setupPasswordButton()
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

    override fun onColorPickerButtonClicked() {
        val colors = context!!.resources.getIntArray(R.array.androidcolors)
        val selectedColor = viewModel.color.value

        if (selectedColor != null) {
            val colorPickerDialog = ColorPickerDialog()
            colorPickerDialog.initialize(
                R.string.dialog_color_picker_title,
                colors,
                selectedColor,
                4, colors.size
            )

            colorPickerDialog.show(fragmentManager!!, "color_picker")
            colorPickerDialog.setOnColorSelectedListener(this)
        } else {
            throw RuntimeException("The default color is not set")
        }
    }

    override fun onColorSelected(color: Int) {
        viewModel.color.value = color
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

    protected open fun setupViewModel() {
        viewModel.apply {
            color.observe(viewLifecycleOwner, Observer { setupColorPickerButton(it) })
            generatedPassword.observe(viewLifecycleOwner, Observer { passwordEditText.setText(it!!) })
            jobDone.observe(viewLifecycleOwner, Observer { activity!!.finish() })
        }

        viewModel.color.value = ContextCompat.getColor(context!!, R.color.colorDefault)
    }

    private fun setupPasswordButton() {
        passwordButton.setOnClickListener { _ ->
            activity!!.supportFragmentManager.apply {
                val ft = beginTransaction()
                findFragmentByTag("dialog")?.let {
                    ft.remove(it)
                }
                ft.addToBackStack(null)

                PasswordGenerateFragment.newInstance().apply {
                    setTargetFragment(this@ComposeFragment, 0)
                    show(ft, "dialog")
                }
            }
        }
    }

    protected fun setupColorPickerButton(color: Int?) {
        colorPickerImageView.apply {
            background = Utils.createOvalDrawable(
                color ?: ContextCompat.getColor(context!!, R.color.colorDefault),
                (30 * dp).toInt()
            )
            setOnClickListener {
                onColorPickerButtonClicked()
            }
        }
    }

    private fun setupServiceButton() {
        googleImageView.setOnClickListener(serviceButtonClickListener)
        twitterImageView.setOnClickListener(serviceButtonClickListener)
        facebookImageView.setOnClickListener(serviceButtonClickListener)
        githubImageView.setOnClickListener(serviceButtonClickListener)
    }

    protected fun getSelectedService() = when {
        googleImageView.isSelected -> "google"
        facebookImageView.isSelected -> "facebook"
        twitterImageView.isSelected -> "twitter"
        githubImageView.isSelected -> "github"
        else -> ""
    }
}