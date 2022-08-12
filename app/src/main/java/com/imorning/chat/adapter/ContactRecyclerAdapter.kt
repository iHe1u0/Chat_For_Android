package com.imorning.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imorning.chat.App
import com.imorning.chat.R
import com.imorning.chat.databinding.LayoutContactItemBinding
import com.imorning.common.database.table.UserInfoEntity
import com.imorning.common.utils.AvatarUtils
import com.imorning.common.utils.FileUtils
import org.jivesoftware.smack.roster.RosterEntry

class ContactRecyclerAdapter(private val onItemClicked: (UserInfoEntity) -> Unit) :
    ListAdapter<UserInfoEntity, ContactRecyclerAdapter.ViewHolder>(DiffCallback) {

    private val rosterEntryList: List<RosterEntry>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder = ViewHolder(
            LayoutContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            if (position < 0) {
                return@setOnClickListener
            }
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private var binding: LayoutContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userInfoEntity: UserInfoEntity) {
            val jid = userInfoEntity.jid
            val username = userInfoEntity.username
            val localAvatarCache = FileUtils.instance.getAvatarCache(jid)

            binding.contactItemUsername.text = username
            binding.contactItemId.text = jid

            if (!localAvatarCache.exists()) {
                AvatarUtils.instance.cacheAvatar(jid)
            }
            Glide.with(App.getContext())
                .load(localAvatarCache)
                .error(
                    ResourcesCompat.getDrawable(
                        App.getContext().resources,
                        R.drawable.ic_default_avatar,
                        null
                    )
                )
                .into(binding.contactItemAvatar)
        }
    }

    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<UserInfoEntity>() {
            override fun areItemsTheSame(
                oldItem: UserInfoEntity,
                newItem: UserInfoEntity
            ): Boolean {
                return oldItem.jid == newItem.jid
            }

            override fun areContentsTheSame(
                oldItem: UserInfoEntity,
                newItem: UserInfoEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}