package com.imorning.chat.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imorning.chat.databinding.FragmentContactBinding
import org.jivesoftware.smack.roster.RosterEntry

class ContactRecyclerAdapter(private val contactList: HashMap<String, List<Any>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val rosterEntryList: List<RosterEntry>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO()
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    class ViewHolder(private var binding: FragmentContactBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(){

            }
        }
}