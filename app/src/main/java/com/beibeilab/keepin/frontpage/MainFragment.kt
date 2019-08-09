package com.beibeilab.keepin.frontpage

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_backup -> {
                AlertDialogFragment.Builder(context!!)
                    .setTitle("title")
                    .setMessage("message")
                    .setPositiveButton("ok")
                    .show(childFragmentManager, "backup")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogAction(fragment: AlertDialogFragment, action: Int, extras: Bundle?) {
        when (fragment.tag) {
            "backup" -> {
                when (action) {
                    AlertDialogFragment.ACTION_POSITIVE -> {
                        Log.d("badu", "XDD")
                    }
                }
            }
        }
    }

    override fun itemOnClicked(position: Int, account: AccountEntity) {
        (activity as MainActivity).replaceFragment(R.id.fragment_content, AccountFragment.newInstance(account))
    }

    private fun setupViewModel() {
        viewModel.accountList.observe(viewLifecycleOwner, Observer { populateList(it) })
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
}
