package com.beibeilab.keepin.frontpage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.account.AccountFragment
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainViewModel2
import com.beibeilab.keepin.extension.replaceFragment
import com.beibeilab.keepin.util.pref.PrefUtils
import com.beibeilab.keepin.util.pref.PreferenceConstants
import com.beibeilab.uikits.alertdialog.AlertDialogFragment
import com.beibeilab.uikits.bottomsheet.EditTextBottomSheet
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.concurrent.Executors

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(),
    MainAdapter.OnItemClickListener,
    AlertDialogFragment.Callback,
    EditTextBottomSheet.Callback {

    companion object {

        private const val PERMISSION_REQUEST_CODE_WRITE_PERMISSION = 0x0
        private const val PERMISSION_REQUEST_CODE_READ_PERMISSION = 0x1

        private const val DIALOG_TAG_BACKUP_EMPTY = "DIALOG_TAG_BACKUP_EMPTY"
        private const val DIALOG_TAG_ASK_WRITE_PERMISSION = "DIALOG_TAG_ASK_WRITE_PERMISSION"
        private const val DIALOG_TAG_ASK_READ_PERMISSION = "DIALOG_TAG_ASK_READ_PERMISSION"
        private const val DIALOG_TAG_ASK_TO_SETTING = "DIALOG_TAG_ASK_TO_SETTING"

        private const val BOTTOM_SHEET_PIN_CODE = "BOTTOM_SHEET_PIN_CODE"
        private const val BOTTOM_SHEET_PIN_CODE_SETTING = "BOTTOM_SHEET_PIN_CODE_SETTING"
    }

    private lateinit var viewModel: MainViewModel

    private var selectedAccount: AccountEntity? = null

    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = obtainViewModel2(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
        viewModel.loadAccountList()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_backup -> {
            askPermissionToBackup()
            true
        }

        R.id.action_restore -> {
            askPermissionToRestore()
            true
        }

        R.id.action_pin_code -> {
            showPinCodeSettingBottomSheet()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_WRITE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backup()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showRequestWritePermissionRationale()
                    } else {
                        handleWritePermissionDenied()
                    }
                }
            }
            PERMISSION_REQUEST_CODE_READ_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restore()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showRequestReadPermissionRationale()
                    } else {
                        handleReadPermissionDenied()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDialogAction(fragment: AlertDialogFragment, action: Int, extras: Bundle?) {
        when (fragment.tag) {
            DIALOG_TAG_ASK_WRITE_PERMISSION -> {
                if (action == AlertDialogFragment.ACTION_POSITIVE) {
                    // ActivityCompat.requestPermissions()
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE_WRITE_PERMISSION
                    )
                }
            }

            DIALOG_TAG_ASK_READ_PERMISSION -> {
                if (action == AlertDialogFragment.ACTION_POSITIVE) {
                    // ActivityCompat.requestPermissions()
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE_READ_PERMISSION
                    )
                }
            }

            DIALOG_TAG_ASK_TO_SETTING -> {
                if (action == AlertDialogFragment.ACTION_POSITIVE) {
                    jumpToSetting()
                }
            }
        }
    }

    override fun itemOnClicked(position: Int, account: AccountEntity) {
        selectedAccount = account

        showBiometricPromt()
    }

    override fun onBottomSheetCommitMessage(tag: String?, message: String) {
        when (tag) {
            BOTTOM_SHEET_PIN_CODE -> {
                viewModel.checkPinCode(message)
            }

            BOTTOM_SHEET_PIN_CODE_SETTING -> {
                viewModel.savePinCode(message)
            }
        }
    }

    private fun setupViewModel() {
        viewModel.apply {
            accountList.observe(viewLifecycleOwner, Observer { populateList(it) })

            noDataEvent.observe(viewLifecycleOwner, Observer { showBackupEmptyDialog() })

            isBackupDone.observe(viewLifecycleOwner, Observer { isBackupDone ->
                if (isBackupDone) {
                    Toast.makeText(context!!, "Backup done", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context!!, "Backup failed", Toast.LENGTH_LONG).show()
                }
            })

            readBackupFailed.observe(viewLifecycleOwner, Observer {
                Toast.makeText(context!!, "Restore failed", Toast.LENGTH_LONG).show()
            })

            pinCodeMatched.observe(viewLifecycleOwner, Observer { matched ->
                if (matched) jumpt2AccountFragment()
            })
        }
    }

    private fun setupRecyclerView() {

        val mainAdapter = this.mainAdapter ?: MainAdapter().apply {
            onItemClickListener = this@MainFragment
        }.also {
            this.mainAdapter = it
        }

        recyclerView.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        }
    }

    private fun populateList(list: List<AccountEntity>) {
        (recyclerView.adapter as MainAdapter).items = list
    }

    private fun showBiometricPromt() {
        val executor = Executors.newSingleThreadExecutor()

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.biometric_title))
            .setSubtitle(resources.getString(R.string.biometric_subtitle))
            .setNegativeButtonText(resources.getString(R.string.action_use_pin_code))
            .build()

        val biometricPrompt = BiometricPrompt(activity!!, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    jumpt2AccountFragment()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            val pinCodeExist =
                                PrefUtils.getDefaultPrefs(context!!)
                                    .contains(PreferenceConstants.PIN_CODE)

                            activity!!.runOnUiThread {
                                if (pinCodeExist) {
                                    showPinCodeBottomSheet()
                                } else {
                                    Toast.makeText(
                                        context!!,
                                        "you have no pic code set",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            activity!!.runOnUiThread {
                                Toast.makeText(context!!, "no biometrics", Toast.LENGTH_LONG).show()
                            }
                        }
                        else -> {
                            Log.e("badu", "error: $errorCode")
                        }
                    }
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun jumpt2AccountFragment() {
        (activity as MainActivity).replaceFragment(
            R.id.fragment_content,
            AccountFragment.newInstance(selectedAccount!!)
        )
    }

    private fun askPermissionToBackup() {
        val context = context!!

        val checkWritePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        when (checkWritePermission) {
            PackageManager.PERMISSION_GRANTED -> {
                backup()
            }
            else -> {

                //  ActivityCompat.shouldShowRequestPermissionRationale()
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showRequestWritePermissionRationale()
                } else {
                    handleWritePermissionDenied()
                }
            }
        }
    }

    private fun askPermissionToRestore() {
        val context = context!!

        val checkReadPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        when (checkReadPermission) {
            PackageManager.PERMISSION_GRANTED -> {
                restore()
            }
            else -> {
                //  ActivityCompat.shouldShowRequestPermissionRationale()
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showRequestReadPermissionRationale()
                } else {
                    handleReadPermissionDenied()
                }
            }
        }
    }

    private fun backup() {
        viewModel.handleBackupRequest()
    }

    private fun restore() {
        viewModel.handleRestoreRequest()
    }

    private fun showBackupEmptyDialog() {
        AlertDialogFragment.Builder(context!!)
            .setTitle(R.string.dialog_warning)
            .setMessage(R.string.dialog_backup_empty_message)
            .setPositiveButton(R.string.dialog_confirm)
            .show(childFragmentManager, DIALOG_TAG_BACKUP_EMPTY)
    }

    private fun showRequestWritePermissionRationale() {
        AlertDialogFragment.Builder(context!!)
            .setTitle("Excuse Me")
            .setMessage("We need storage writing permission")
            .setPositiveButton("OK")
            .setNegativeButton("No")
            .show(childFragmentManager, DIALOG_TAG_ASK_WRITE_PERMISSION)
    }

    private fun showRequestReadPermissionRationale() {
        AlertDialogFragment.Builder(context!!)
            .setTitle("Excuse Me")
            .setMessage("We need storage reading permission")
            .setPositiveButton("OK")
            .setNegativeButton("No")
            .show(childFragmentManager, DIALOG_TAG_ASK_READ_PERMISSION)
    }

    private fun handleWritePermissionDenied() {
        AlertDialogFragment.Builder(context!!)
            .setTitle("No Permission")
            .setMessage("You have to approve write permission in setting")
            .setPositiveButton("OK")
            .setNegativeButton("No")
            .show(childFragmentManager, DIALOG_TAG_ASK_TO_SETTING)
    }

    private fun handleReadPermissionDenied() {
        AlertDialogFragment.Builder(context!!)
            .setTitle("No Permission")
            .setMessage("You have to approve read permission in setting")
            .setPositiveButton("OK")
            .setNegativeButton("No")
            .show(childFragmentManager, DIALOG_TAG_ASK_TO_SETTING)
    }

    private fun jumpToSetting() {
        val packageName = context!!.applicationContext.packageName
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }

        startActivity(intent)
    }

    private fun showPinCodeSettingBottomSheet() {
        val res = context!!.resources
        EditTextBottomSheet
            .newInstance(
                res.getString(R.string.bottom_sheet_pin_setting_code_title),
                res.getString(R.string.bottom_sheet_pin_setting_code_message)
            )
            .show(childFragmentManager, BOTTOM_SHEET_PIN_CODE_SETTING)
    }

    private fun showPinCodeBottomSheet() {
        val res = context!!.resources
        EditTextBottomSheet
            .newInstance(
                res.getString(R.string.bottom_sheet_pin_code_title),
                res.getString(R.string.bottom_sheet_pin_code_message)
            )
            .show(childFragmentManager, BOTTOM_SHEET_PIN_CODE)
    }
}
