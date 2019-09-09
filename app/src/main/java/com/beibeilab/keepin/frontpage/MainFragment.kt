package com.beibeilab.keepin.frontpage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.account.AccountFragment
import com.beibeilab.keepin.database.AccountDatabase
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

        private const val DIALOG_TAG_BACKUP = "DIALOG_TAG_BACKUP"
        private const val DIALOG_TAG_BACKUP_EMPTY = "DIALOG_TAG_BACKUP_EMPTY"
        private const val DIALOG_TAG_ASK_PERMISSION = "DIALOG_TAG_ASK_PERMISSION"
        private const val DIALOG_TAG_ASK_TO_SETTING = "DIALOG_TAG_ASK_TO_SETTING"
    }

    private lateinit var viewModel: MainViewModel
    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = obtainViewModel2(MainViewModel::class.java).apply {
            accountDatabase = AccountDatabase.getInstance(context!!)
        }
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
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showFAB()
        test()
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
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDialogAction(fragment: AlertDialogFragment, action: Int, extras: Bundle?) {
        when (fragment.tag) {
            DIALOG_TAG_ASK_PERMISSION -> {
                if (action == AlertDialogFragment.ACTION_POSITIVE) {
                    // ActivityCompat.requestPermissions()
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE_WRITE_PERMISSION
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

            showDialog.observe(viewLifecycleOwner, Observer { isDataEmpty ->
                if (isDataEmpty) {
                    showBackupEmptyDialog()
                } else {
                    showBackupDialog()
                }
            })

            loadAccountList()
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

    private fun backup() {
        viewModel.handleBackupRequest()
    }

    private fun showBackupDialog() {
        AlertDialogFragment.Builder(context!!)
            .setTitle(R.string.dialog_warning)
            .setPositiveButton(R.string.dialog_confirm)
            .show(childFragmentManager, DIALOG_TAG_BACKUP)
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
            .show(childFragmentManager, DIALOG_TAG_ASK_PERMISSION)
    }

    private fun handleWritePermissionDenied() {
        AlertDialogFragment.Builder(context!!)
            .setTitle("No Permission")
            .setMessage("You have to approve wirte permission in setting")
            .setPositiveButton("OK")
            .setNegativeButton("No")
            .show(childFragmentManager, DIALOG_TAG_ASK_TO_SETTING)

    }

    private fun jumpToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            setData(Uri.parse("package:com.beibeilab.keepin"))
        }

        startActivity(intent)
    }

    private fun test() {

        Handler().postDelayed({

            val size = resources.displayMetrics.density * 100
            viewModel.testSingleEvent.observe(viewLifecycleOwner, Observer {
                imageView.setImageBitmap(it)
            })
            viewModel.test(size)


        }, 1000)
    }
}
