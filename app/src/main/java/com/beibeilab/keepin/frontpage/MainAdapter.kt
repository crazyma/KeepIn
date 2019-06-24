package com.beibeilab.keepin.frontpage

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.beibeilab.keepin.database.AccountEntity

class MainAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    interface OnItemClickListener {
        fun itemOnClicked(position: Int, account: AccountEntity)
    }

    var items: List<AccountEntity>? = null
        set(value) {
            Log.d("badu","set items")
            val oldItems = field
            field = value
            Log.d("badu","old item size : ${oldItems?.size ?: -1}")
            Log.d("badu","new item size : ${value?.size ?: -1}")
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    (oldItems!![oldItemPosition].uid == value!![newItemPosition].uid).also {
                        Log.i("badu","item same ? $it")
                    }

                override fun getOldListSize() = oldItems?.size ?: 0

                override fun getNewListSize() = value?.size ?: 0

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    (oldItems!![oldItemPosition] == value!![newItemPosition]).also {
                        Log.i("badu","same ? $it")
                    }
            }).dispatchUpdatesTo(this)
        }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder.create(parent)

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items!![position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.itemOnClicked(position, items!![position])
        }
    }

}