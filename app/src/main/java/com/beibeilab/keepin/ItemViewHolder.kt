package com.beibeilab.keepin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
            )
    }

    fun bind(){

    }

}