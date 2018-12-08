package com.beibeilab.keepin.frontpage

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.account.AccountFragment
import com.beibeilab.keepin.compose.ComposeViewModel
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainViewModel
import com.beibeilab.keepin.extension.replaceFragment
import com.beibeilab.keepin.model.AccountInfo
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), MainAdapter.OnItemClickListner {

    private lateinit var viewModel: MainViewModel

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

    override fun itemOnClicked(position: Int, account: AccountEntity) {
        (activity as MainActivity).replaceFragment(R.id.fragment_content, AccountFragment.newInstance(account))
    }

    private fun setupViewModel() {
        viewModel.accountList.observe(viewLifecycleOwner, Observer { populateList(it) })
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            adapter = MainAdapter().apply {
                onItemClickListner = this@MainFragment
            }
            layoutManager = LinearLayoutManager(
                context!!,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun populateList(list: List<AccountEntity>) {
        (recyclerView.adapter as MainAdapter).items = list
    }
}
