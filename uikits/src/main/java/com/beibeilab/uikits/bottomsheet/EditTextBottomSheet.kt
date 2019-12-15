package com.beibeilab.uikits.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.beibeilab.uikits.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_edittext.*

class EditTextBottomSheet : BottomSheetDialogFragment() {

    companion object {

        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_EDITTEXT_HINT = "ARG_EDITTEXT_HINT"

        @JvmStatic
        fun newInstance(title: String, hint: String?) =
            EditTextBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    hint?.let { putString(ARG_EDITTEXT_HINT, it) }
                }
            }
    }

    interface Callback {
        fun onBottomSheetCommitMessage(message: String)
    }

    private val title: String
        get() = arguments!!.getString(ARG_TITLE)!!

    private val hint: String?
        get() = arguments?.getString(ARG_EDITTEXT_HINT)

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as? Callback)?.run {
            callback = this
        }

        (parentFragment as? Callback)?.run {
            callback = this
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.bottom_sheet_edittext, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        titleTextView.text = title
        editText.hint = hint

        closeButton.setOnClickListener {
            dismiss()
        }

        submitButton.setOnClickListener {
            editText.text?.toString()?.let{
                callback?.onBottomSheetCommitMessage(it)
            }
        }
    }

}