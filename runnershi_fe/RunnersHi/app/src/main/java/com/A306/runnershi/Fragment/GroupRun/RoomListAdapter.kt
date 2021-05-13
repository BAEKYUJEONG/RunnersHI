package com.A306.runnershi.Fragment.GroupRun

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.group_run_room_preview.view.*

//class RoomListAdapter (private var list: MutableList<Room>, private val listener:(Room) -> Unit): RecyclerView.Adapter<RoomListAdapter.ListItemViewHolder> () {
//class RoomListAdapter (private var list: MutableList<Room>, private val callbackInterface:CallbackInterface): RecyclerView.Adapter<RoomListAdapter.ListItemViewHolder> () {

class RoomListAdapter (private var list: MutableList<Room>, var test:GroupRunRoomListFragment.testClass): RecyclerView.Adapter<RoomListAdapter.ListItemViewHolder> () {

    inner class ListItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        var roomTitle: TextView = itemView!!.findViewById(R.id.roomTitle)
        var roomType: TextView = itemView!!.findViewById(R.id.roomType)
        var roomCount: TextView = itemView!!.findViewById(R.id.roomCount)

        fun bind(data: Room, position: Int) {
            roomTitle.text = data.title
            // type에 따라 변환해주기
            when (data.type) {
                0 -> roomType.text = "공개방"
                1 -> roomType.text = "친구공개방"
                2 -> roomType.text = "비공개방"
            }
            var countToString: String = data.count.toString()

            roomCount.text = "$countToString /5"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_run_room_preview, parent, false)
        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: RoomListAdapter.ListItemViewHolder, position: Int) {

//        val callbackInterface = CallbackInterface()

        holder.bind(list[position], position)
        holder.itemView.setOnClickListener {
            val please= list[position].room_id

            test.testFun(please)

            Log.i("이 아이템의", "$please")
//            if (context is MainActivity) {
//                val mainActivity = activity as MainActivity
//                val roomFragment = RoomFragment()
//
//                mainActivity.makeCurrentFragment(roomFragment)
//            }
        }
    }

}