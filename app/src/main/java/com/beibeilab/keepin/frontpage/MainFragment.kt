package com.beibeilab.keepin.frontpage

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.account.AccountFragment
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainViewModel
import com.beibeilab.keepin.extension.replaceFragment
import com.beibeilab.uikits.alertdialog.AlertDialogFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), MainAdapter.OnItemClickListener, AlertDialogFragment.Callback {

    companion object {
        private const val FRAGMENT_TAG_BACKUP = "FRAGMENT_TAG_BACKUP"
        private const val FRAGMENT_TAG_BACKUP_EMPTY = "FRAGMENT_TAG_BACKUP_EMPTY"
    }

    private lateinit var viewModel: MainViewModel
    private var mainAdapter: MainAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = obtainViewModel(MainViewModel::class.java).apply {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_backup -> {
            viewModel.handleBackupRequest()

            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onDialogAction(fragment: AlertDialogFragment, action: Int, extras: Bundle?) {
        when (fragment.tag) {
            FRAGMENT_TAG_BACKUP -> {
                when (action) {
                    AlertDialogFragment.ACTION_POSITIVE -> {
//                        viewModel.getBackup()
                    }
                }
            }
        }
    }


    override fun itemOnClicked(position: Int, account: AccountEntity) {
        (activity as MainActivity).replaceFragment(R.id.fragment_content, AccountFragment.newInstance(account))
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

    private fun showBackupDialog() {
        AlertDialogFragment.Builder(context!!)
            .setTitle(R.string.dialog_warning)
            .setPositiveButton(R.string.dialog_confirm)
            .show(childFragmentManager, FRAGMENT_TAG_BACKUP)
    }

    private fun showBackupEmptyDialog() {
        AlertDialogFragment.Builder(context!!)
            .setTitle(R.string.dialog_warning)
            .setMessage(R.string.dialog_backup_empty_message)
            .setPositiveButton(R.string.dialog_confirm)
            .show(childFragmentManager, FRAGMENT_TAG_BACKUP_EMPTY)
    }
}
