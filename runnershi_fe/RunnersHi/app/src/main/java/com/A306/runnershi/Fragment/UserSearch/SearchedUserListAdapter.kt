package com.A306.runnershi.Fragment.UserSearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.User
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.user_search_user_preview.view.*

class SearchedUserListAdapter(private val userList: ArrayList<User>, var link: UserSearchFragment.searchedUserAdapterToList): RecyclerView.Adapter<SearchedUserListAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName:TextView = itemView.findViewById(R.id.userName)
        val friendAddBtn:Button = itemView.findViewById(R.id.friendAddBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_search_user_preview, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text = userList[position].userName
        holder.friendAddBtn.setOnClickListener {
            val user = userList[position]
            link.getUserId(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.count()
    }
}