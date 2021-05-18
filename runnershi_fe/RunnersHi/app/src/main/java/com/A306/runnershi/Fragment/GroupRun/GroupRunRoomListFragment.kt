package com.A306.runnershi.Fragment.GroupRun

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Openvidu.OpenviduRetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_group_run_room_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class GroupRunRoomListFragment : Fragment() {


    // 데이터를 불러와서 넘겨줄 예정입니다. 지금은 임시로 생성한 데이터를 넘겨주자:
    var tempRoomList: ArrayList<Room> = arrayListOf(
        Room(1, "페이스 조절하며 같이 뛰어요!", 1, 1),
        Room(2, "도란도란 밤산책", 1, 3),
        Room(3, "작심하루 다이어트", 2, 2)
    )

    // 세션 list 불러온 다음
    // 해당 세션 id를 백으로 보내서 거기에 해당되는 room 정보들을 가져온다

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

        var list: ArrayList<Room> = tempRoomList
        var link = roomListAdapterToList()

        OpenviduRetrofitClient.getSessionInstance().getRoomList().enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Timber.e("세션 리스트 제발!!!!!")
                val receivedJson = Gson().fromJson(response.body()?.string(), Map::class.java)
                var receivedContent = receivedJson["content"].toString()
                Timber.e(receivedContent)
                receivedContent = receivedContent.replace("=,", "=\"\",")
                Timber.e(receivedContent)
                val sessionList = Gson().fromJson(receivedContent, List::class.java)
                if (sessionList.isNotEmpty()){
                    for (session in sessionList){
                        Timber.e(session.toString())
                    }
                }

//                Timber.e(response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        roomListAdapter = RoomListAdapter(list, link)
        runningListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        runningListView.adapter = roomListAdapter
    }

    private fun openRoom(room: Room) {
        val mainActivity = activity as MainActivity
        val roomFragment = RoomFragment(room)

        mainActivity.makeCurrentFragment(roomFragment, "hide")
    }

    inner class roomListAdapterToList {

        fun getRoomId(room: Room) {
            openRoom(room)
        }
    }

}