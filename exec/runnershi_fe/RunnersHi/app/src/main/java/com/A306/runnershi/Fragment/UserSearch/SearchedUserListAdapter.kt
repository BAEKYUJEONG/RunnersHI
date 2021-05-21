package com.A306.runnershi.Fragment.UserSearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Model.User
import com.A306.runnershi.R

class SearchedUserListAdapter(private val userList: ArrayList<List<Any>>, var link: UserSearchFragment.searchedUserAdapterToList): RecyclerView.Adapter<SearchedUserListAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName:TextView = itemView.findViewById(R.id.userName)
        val friendAddBtn:Button = itemView.findViewById(R.id.friendAddBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_search_user_preview, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        // 해당 유저 정보를 바탕으로,
        var userData = userList[position]
        var user = userData[0] as User
        var isFriend = userData[1] as Boolean

        // 유저 닉네임 출력해주고
        holder.userName.text = user.userName

        // 친구인 경우에는 버튼 세팅
        if (isFriend) {
            holder.friendAddBtn.visibility = View.INVISIBLE
        } else {
            holder.friendAddBtn.setOnClickListener {
                link.getUserId(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.count()
    }
}