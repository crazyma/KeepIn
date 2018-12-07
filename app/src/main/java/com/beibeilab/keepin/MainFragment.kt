package com.beibeilab.keepin

import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beibeilab.keepin.model.AccountInfo
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        val list = listOf(
            AccountInfo("aaa"),
            AccountInfo("bbb"),
            AccountInfo("ccc"),
            AccountInfo("ddd")
        )

        (recyclerView.adapter as MainAdapter).items = list
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            adapter = MainAdapter()
            layoutManager = LinearLayoutManager(
                context!!,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

    }
}
