package com.beibeilab.keepin

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.beibeilab.keepin.model.AccountInfo

class MainAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    var items: List<AccountInfo>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder.create(parent)

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items!![position])
    }

}