package com.example.modibot.Admin.Manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.modibot.database.UserSign
import com.example.modibot.databinding.RecyclerRowBinding

class UserAdapter(val userList: ArrayList<String>,val userInfoList: ArrayList<UserSign>) : RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    class UserAdapterViewHolder(val binding: RecyclerRowBinding, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserAdapterViewHolder(binding, mListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
        holder.binding.textViewRecyclerView.text = userList[position]
    }
}
