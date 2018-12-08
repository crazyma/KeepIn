package com.beibeilab.keepin.frontpage

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.model.AccountInfo

class MainAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    interface OnItemClickListner {
        fun itemOnClicked(position: Int, account: AccountEntity)
    }

    var items: List<AccountEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListner: OnItemClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder.create(parent)

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items!![position])
        holder.itemView.setOnClickListener {
            onItemClickListner?.itemOnClicked(position, items!![position])
        }
    }

}