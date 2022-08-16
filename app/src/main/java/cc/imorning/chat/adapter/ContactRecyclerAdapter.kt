package cc.imorning.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cc.imorning.chat.databinding.LayoutContactItemBinding
import cc.imorning.common.database.table.UserInfoEntity
import cc.imorning.common.utils.AvatarUtils
import cc.imorning.common.utils.FileUtils
import com.bumptech.glide.Glide

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
        val localAvatarCache = FileUtils.instance.getAvatarCachePath(entity.jid)
        val defaultAvatar = AvatarUtils.instance.getDefaultAvatar()

        holder.userName.text = entity.username
        holder.jid.text = entity.jid
        if (!localAvatarCache.exists()) {
            if (AvatarUtils.instance.cacheAvatar(entity.jid) == null) {
                Glide.with(holder.avatar.context)
                    .load(defaultAvatar)
                    .into(holder.avatar)
                return
            }
        }
        Glide.with(holder.avatar.context)
            .load(localAvatarCache)
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

        private const val TAG = "ContactRecyclerAdapter"

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