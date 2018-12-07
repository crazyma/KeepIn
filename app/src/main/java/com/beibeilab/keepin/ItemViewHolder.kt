package com.beibeilab.keepin

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beibeilab.keepin.model.AccountInfo
import kotlinx.android.synthetic.main.item_main.view.*

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
            )
    }

    fun bind(accountInfo: AccountInfo) {
        itemView.accountNameTextView.text = accountInfo.textName
    }

}