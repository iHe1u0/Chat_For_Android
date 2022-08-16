package cc.imorning.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cc.imorning.chat.App
import cc.imorning.chat.R
import cc.imorning.chat.databinding.LayoutContactItemBinding
import cc.imorning.common.database.table.UserInfoEntity
import cc.imorning.common.utils.AvatarUtils
import cc.imorning.common.utils.FileUtils

class ContactRecyclerAdapter(private val contacts: List<UserInfoEntity>) :
    ListAdapter<UserInfoEntity, ContactRecyclerAdapter.ViewHolder>(DiffCallback) {

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
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val entity = contacts[position]
        val localAvatarCache = FileUtils.instance.getAvatarCache(entity.jid)

        holder.userName.text = entity.username
        holder.jid.text = entity.jid
        if (!localAvatarCache.exists()) {
            AvatarUtils.instance.cacheAvatar(entity.jid)
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
            .into(holder.avatar)

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ViewHolder(private var binding: LayoutContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val avatar = binding.contactItemAvatar
        val userName = binding.contactItemUsername
        val jid = binding.contactItemId
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