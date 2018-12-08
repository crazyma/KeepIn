package com.beibeilab.keepin.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.beibeilab.keepin.R
import com.beibeilab.keepin.compose.ComposeViewModel
import com.beibeilab.keepin.extension.obtainActivityViewModel
import com.beibeilab.keepin.extension.obtainViewModel
import kotlinx.android.synthetic.main.fragment_password_generate.*

class PasswordGenerateFragment : DialogFragment() {

    companion object {
        fun newInstance() = PasswordGenerateFragment()
    }

    private lateinit var viewModel: PasswordViewModel
    private lateinit var activityViewModel: ComposeViewModel

    private val checkedTextViewClickListener = View.OnClickListener {
        (it as CheckedTextView).apply {
            toggle()
            when (id) {
                R.id.upperCaseCheckedText -> viewModel.setRules(0, isChecked)
                R.id.lowerCaseCheckedText -> viewModel.setRules(1, isChecked)
                R.id.numberCheckedText -> viewModel.setRules(2, isChecked)
                R.id.otherCheckedText -> viewModel.setRules(3, isChecked)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel(PasswordViewModel::class.java)
        activityViewModel = obtainActivityViewModel(ComposeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password_generate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupButton()
        setupCheckedTextView()
    }

    private fun setupViewModel() {
        viewModel.apply {
            isNextStep.observe(viewLifecycleOwner, Observer { modifyLayout(it!!) })
            password.observe(viewLifecycleOwner, Observer { generatedPasswordTextView.text = it!! })
        }
    }

    private fun setupCheckedTextView() {
        upperCaseCheckedText.setOnClickListener(checkedTextViewClickListener)
        lowerCaseCheckedText.setOnClickListener(checkedTextViewClickListener)
        numberCheckedText.setOnClickListener(checkedTextViewClickListener)
        otherCheckedText.setOnClickListener(checkedTextViewClickListener)
    }

    private fun setupButton() {
        confirmButton.setOnClickListener {
            viewModel.apply {
                when (isNextStep.value) {
                    true -> {
                        activityViewModel.generatedPassword.value = generatedPasswordTextView.text.toString()
                        dismiss()
                    }
                    else -> {
                        if (checkRulesExist()) {
                            generatePassword()
                            isNextStep.value = true
                        }
                    }
                }
            }
        }

        cancelButton.setOnClickListener { dismiss() }

        refreshImageView.setOnClickListener {
            viewModel.generatePassword()
        }
    }

    private fun modifyLayout(isNextStep: Boolean) {
        val firstStepVisibility = if (isNextStep) View.INVISIBLE else View.VISIBLE
        val secondStepVisibility = if (isNextStep) View.VISIBLE else View.INVISIBLE

        upperCaseCheckedText.visibility = firstStepVisibility
        lowerCaseCheckedText.visibility = firstStepVisibility
        numberCheckedText.visibility = firstStepVisibility
        otherCheckedText.visibility = firstStepVisibility
        lengthTitleTextView.visibility = firstStepVisibility
        lengthEditText.visibility = firstStepVisibility

        generatedPasswordTextView.visibility = secondStepVisibility
        refreshImageView.visibility = secondStepVisibility

        if (isNextStep) changeLayoutSize()
    }

    private fun changeLayoutSize() {
        val constraintLayout = confirmButton.parent as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(confirmButton.id, ConstraintSet.TOP, R.id.refreshImageView, ConstraintSet.BOTTOM, 16)
        constraintSet.applyTo(constraintLayout)
    }
}