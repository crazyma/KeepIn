package com.beibeilab.keepin.frontpage

import android.app.AlertDialog
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.CancellationSignal
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.beibeilab.keepin.MainActivity
import com.beibeilab.keepin.R
import com.beibeilab.keepin.account.AccountFragment
import com.beibeilab.keepin.database.AccountDatabase
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.extension.obtainViewModel
import com.beibeilab.keepin.extension.replaceFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), MainAdapter.OnItemClickListner {

    private lateinit var viewModel: MainViewModel
    private var fingerprintDialog: AlertDialog? = null
    private var selectedAccount: AccountEntity? = null

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

    override fun itemOnClicked(position: Int, account: AccountEntity) {
        selectedAccount = account

        showBiometricPromt()
    }

    private fun setupViewModel() {
        viewModel.accountList.observe(viewLifecycleOwner, Observer { populateList(it) })
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            adapter = MainAdapter().apply {
                onItemClickListner = this@MainFragment
            }
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        }
    }

    private fun populateList(list: List<AccountEntity>) {
        (recyclerView.adapter as MainAdapter).items = list
    }

    private fun showBiometricPromt(){
        val executor = context!!.mainExecutor
        val cancelListener = DialogInterface.OnClickListener { _, _ ->

        }

        resources.getString(R.string.fingerprint_title)

        val biometricPrompt = BiometricPrompt.Builder(context!!)
            .setTitle(resources.getString(R.string.fingerprint_title))
            .setSubtitle(resources.getString(R.string.fingerprint_subtitle))
            .setNegativeButton(resources.getString(R.string.cancel), executor, cancelListener)
            .build()

        biometricPrompt.authenticate(CancellationSignal(), executor, object:BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                jumpt2AccountFragment()
            }
        })
    }

    private fun jumpt2AccountFragment(){
        (activity as MainActivity).replaceFragment(R.id.fragment_content, AccountFragment.newInstance(selectedAccount!!))
    }
}
