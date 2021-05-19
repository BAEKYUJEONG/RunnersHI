package com.A306.runnershi.Fragment.Profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.Friend
import com.A306.runnershi.R

class FriendAdapter(private val friendList:Array<Friend>, private val friendFragment: FriendFragment): RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val friend_nick: TextView = itemView.findViewById(R.id.friend_nick)
        val friend_rank : TextView = itemView.findViewById(R.id.friend_rank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)

        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.friend_nick.text = friendList[position].userName.toString()
        holder.friend_rank.text = friendList[position].rank.toString()
    }

    override fun getItemCount(): Int = friendList.size
}