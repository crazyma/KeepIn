package com.beibeilab.keepin.frontpage

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
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
import java.util.concurrent.Executors

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), MainAdapter.OnItemClickListener {

    private lateinit var viewModel: MainViewModel

    private var selectedAccount: AccountEntity? = null

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

    override fun itemOnClicked(position: Int, account: AccountEntity) {
        selectedAccount = account

        showBiometricPromt()
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

    private fun showBiometricPromt(){
        val executor = Executors.newSingleThreadExecutor()

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.fingerprint_title))
            .setSubtitle(resources.getString(R.string.fingerprint_subtitle))
            .setNegativeButtonText(resources.getString(R.string.cancel))
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

                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when(errorCode){
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            Log.d("badu","cancel clicked")
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            Toast.makeText(context!!, "no biometrics", Toast.LENGTH_LONG).show()
                        }
                        else-> {
                            Log.e("badu","error: $errorCode")
                        }
                    }
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun jumpt2AccountFragment(){
        (activity as MainActivity).replaceFragment(R.id.fragment_content, AccountFragment.newInstance(selectedAccount!!))
    }
}
