package com.beibeilab.keepin.frontpage

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.beibeilab.keepin.R
import com.beibeilab.keepin.database.AccountEntity
import com.beibeilab.keepin.model.AccountInfo
import com.beibeilab.keepin.util.Utils
import kotlinx.android.synthetic.main.item_main.view.*

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
            )
    }

    fun bind(account: AccountEntity) {
        itemView.apply {
            accountNameTextView.text = account.serviceName
            imageAvatar.background = when (account.color) {
                0 -> Utils.createOvalDrawable(
                    ContextCompat.getColor(imageAvatar.context, R.color.colorPrimary),
                    imageAvatar.resources.getDimensionPixelSize(R.dimen.main_list_avatar_width)
                )

                else -> Utils.createOvalDrawable(
                    account.color,
                    imageAvatar.resources.getDimensionPixelSize(R.dimen.main_list_avatar_width)
                )
            }

            when (account.oauth) {
                "google" -> {
                    imageOauthIcon.visibility = View.VISIBLE
                    imageOauthIcon.setImageResource(R.drawable.icon_google_small)
                }

                "facebook" -> {
                    imageOauthIcon.visibility = View.VISIBLE
                    imageOauthIcon.setImageResource(R.drawable.icon_facebook_small)
                }

                "twitter" -> {
                    imageOauthIcon.visibility = View.VISIBLE
                    imageOauthIcon.setImageResource(R.drawable.icon_twitter_small)
                }

                "github" -> {
                    imageOauthIcon.visibility = View.VISIBLE
                    imageOauthIcon.setImageResource(R.drawable.icon_github_small)
                }

                else -> {
                    imageOauthIcon.visibility = View.GONE
                }
            }
        }
    }
}