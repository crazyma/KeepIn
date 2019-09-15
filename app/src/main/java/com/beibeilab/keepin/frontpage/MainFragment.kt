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
import com.beibeilab.uikits.alertdialog.AlertDialogFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), MainAdapter.OnItemClickListener, AlertDialogFragment.Callback {

    companion object {

        private const val PERMISSION_REQUEST_CODE_WRITE_PERMISSION = 0x0
        private const val PERMISSION_REQUEST_CODE_READ_PERMISSION = 0x1

        private const val DIALOG_TAG_BACKUP_EMPTY = "DIALOG_TAG_BACKUP_EMPTY"
        private const val DIALOG_TAG_ASK_WRITE_PERMISSION = "DIALOG_TAG_ASK_WRITE_PERMISSION"
        private const val DIALOG_TAG_ASK_READ_PERMISSION = "DIALOG_TAG_ASK_READ_PERMISSION"
        private const val DIALOG_TAG_ASK_TO_SETTING = "DIALOG_TAG_ASK_TO_SETTING"
    }

    private lateinit var viewModel: MainViewModel
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
        (activity as MainActivity).replaceFragment(
            R.id.fragment_content,
            AccountFragment.newInstance(account)
        )
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
                Log.e("badu", "$it")
                Toast.makeText(context!!, "Restore failed", Toast.LENGTH_LONG).show()
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
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:com.beibeilab.keepin")
        }

        startActivity(intent)
    }
}
