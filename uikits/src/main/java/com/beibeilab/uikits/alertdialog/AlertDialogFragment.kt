package com.beibeilab.uikits.alertdialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Wraps simple [AlertDialog] into a [DialogFragment].
 */
class AlertDialogFragment : DialogFragment() {

    class Builder(val context: Context) {

        private var title: CharSequence? = null
        private var message: CharSequence? = null
        private var extras: Bundle? = null
        private var positiveButton: CharSequence? = null
        private var neutralButton: CharSequence? = null
        private var negativeButton: CharSequence? = null
        private var isCancelable = true
        private var cancelOnTouchOutside = true

        fun create() = AlertDialogFragment().apply {
            val builder = this@Builder
            title = builder.title
            message = builder.message
            extras = builder.extras
            positiveButton = builder.positiveButton
            neutralButton = builder.neutralButton
            negativeButton = builder.negativeButton
            isCancelable = builder.isCancelable
            cancelOnTouchOutside = builder.cancelOnTouchOutside
        }

        fun show(manager: FragmentManager, tag: String?) {
            create().show(manager, tag)
        }

        fun setTitle(title: CharSequence?) = run {
            this.title = title
            this
        }

        fun setTitle(@StringRes title: Int) = run {
            this.title = context.getText(title)
            this
        }

        fun setMessage(message: CharSequence?) = run {
            this.message = message
            this
        }

        fun setMessage(@StringRes message: Int) = run {
            this.message = context.getText(message)
            this
        }

        fun setExtras(extras: Bundle?) = run {
            this.extras = extras
            this
        }

        fun setPositiveButton(text: CharSequence?) = run {
            this.positiveButton = text
            this
        }

        fun setPositiveButton(@StringRes text: Int) = run {
            this.positiveButton = context.getText(text)
            this
        }

        fun setNeutralButton(text: CharSequence?) = run {
            this.neutralButton = text
            this
        }

        fun setNeutralButton(@StringRes text: Int) = run {
            this.neutralButton = context.getText(text)
            this
        }

        fun setNegativeButton(text: CharSequence?) = run {
            this.negativeButton = text
            this
        }

        fun setNegativeButton(@StringRes text: Int) = run {
            this.negativeButton = context.getText(text)
            this
        }

        fun setCancellable(isCancellable: Boolean) = run {
            this.isCancelable = isCancellable
            this
        }

        fun setCancelOnTouchOutside(cancel: Boolean) = run {
            this.cancelOnTouchOutside = cancel
            this
        }
    }

    interface Callback {
        fun onDialogAction(fragment: AlertDialogFragment, @Action action: Int, extras: Bundle?)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        ACTION_DISMISS,
        ACTION_POSITIVE,
        ACTION_NEUTRAL,
        ACTION_NEGATIVE
    )
    annotation class Action

    companion object {

        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_EXTRAS = "ARG_EXTRAS"
        private const val ARG_POSITIVE_BUTTON = "ARG_POSITIVE_BUTTON"
        private const val ARG_NEUTRAL_BUTTON = "ARG_NEUTRAL_BUTTON"
        private const val ARG_NEGATIVE_BUTTON = "ARG_NEGATIVE_BUTTON"
        private const val ARG_CANCELED_ON_TOUCH_OUTSIDE = "ARG_CANCELED_ON_TOUCH_OUTSIDE"

        const val ACTION_DISMISS = 0
        const val ACTION_CANCEL = 1
        const val ACTION_POSITIVE = 10
        const val ACTION_NEUTRAL = 11
        const val ACTION_NEGATIVE = 12
    }

    var title
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putCharSequence(ARG_TITLE, value) } }
        get() = arguments?.getCharSequence(ARG_TITLE)

    var message
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putCharSequence(ARG_MESSAGE, value) } }
        get() = arguments?.getCharSequence(ARG_MESSAGE)

    /**
     * These extras will be passed to [Callback.onDialogAction].
     */
    var extras
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putBundle(ARG_EXTRAS, value) } }
        get() = arguments?.getBundle(ARG_EXTRAS)

    var positiveButton
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putCharSequence(ARG_POSITIVE_BUTTON, value) } }
        get() = arguments?.getCharSequence(ARG_POSITIVE_BUTTON)

    var neutralButton
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putCharSequence(ARG_NEUTRAL_BUTTON, value) } }
        get() = arguments?.getCharSequence(ARG_NEUTRAL_BUTTON)

    var negativeButton
        set(value) = run { arguments = (arguments ?: Bundle()).apply { putCharSequence(ARG_NEGATIVE_BUTTON, value) } }
        get() = arguments?.getCharSequence(ARG_NEGATIVE_BUTTON)

    var cancelOnTouchOutside
        set(value) = run {
            arguments = (arguments ?: Bundle()).apply { putBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE, value) }
        }
        get() = arguments?.getBoolean(ARG_CANCELED_ON_TOUCH_OUTSIDE, true) ?: true

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = parentFragment as? Callback ?: context as? Callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = run {
        val fragment = this@AlertDialogFragment
        AlertDialog.Builder(context!!).apply {
            setTitle(title)
            setMessage(message)
            positiveButton?.let {
                setPositiveButton(it) { _, _ ->
                    callback?.onDialogAction(fragment, ACTION_POSITIVE, extras)
                }
            }
            neutralButton?.let {
                setNeutralButton(it) { _, _ ->
                    callback?.onDialogAction(fragment, ACTION_NEUTRAL, extras)
                }
            }
            negativeButton?.let {
                setNegativeButton(it) { _, _ ->
                    callback?.onDialogAction(fragment, ACTION_NEGATIVE, extras)
                }
            }
        }.create().apply {
            if (isCancelable) {
                setCanceledOnTouchOutside(cancelOnTouchOutside)
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        callback?.onDialogAction(this, ACTION_CANCEL, extras)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback?.onDialogAction(this, ACTION_DISMISS, extras)
    }
}