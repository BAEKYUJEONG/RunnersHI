package com.A306.runnershi.Fragment.GroupRun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import com.A306.runnershi.Fragment.GroupRun.Room
import kotlinx.android.synthetic.main.fragment_group_run_room_list.*
import kotlinx.android.synthetic.main.group_run_room_preview.*


class GroupRunRoomListFragment : Fragment() {


//    val roomFragment = RoomFragment()



    // 데이터를 불러와서 넘겨줄 예정입니다. 지금은 임시로 생성한 데이터를 넘겨주자:
    var tempRoomList : ArrayList<Room> = arrayListOf(
        Room(1, "페이스 조절하며 같이 뛰어요!", 1, 1),
        Room(2, "도란도란 밤산책", 1, 3),
        Room(3, "작심하루 다이어트", 2, 2)
    )

    private lateinit var roomListAdapter: RoomListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_run_room_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity

        var list : ArrayList<Room> = tempRoomList
        var test = testClass()

        roomListAdapter = RoomListAdapter(list, test)
        runningListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        runningListView.adapter = roomListAdapter
    }

    class testClass{
        fun testFun(message:Int?){
            Log.d("TAG", "밖에서!! $message")
        }
    }
}