package com.A306.runnershi.Fragment.GroupRun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Openvidu.OpenviduRetrofitClient
import com.A306.runnershi.Openvidu.OpenviduSessionRetrofitClient
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_group_run_room_list.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class GroupRunRoomListFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()

    // 데이터를 불러와서 넘겨줄 예정입니다. 지금은 임시로 생성한 데이터를 넘겨주자:
    var tempRoomList: ArrayList<Room> = arrayListOf(
            Room(1, "페이스 조절하며 같이 뛰어요!", 1, 1),
            Room(2, "도란도란 밤산책", 1, 3),
            Room(3, "작심하루 다이어트", 2, 2)
    )

    var token = ""

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
        getRoomList()
        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it != null){
                token = it.token.toString()
            }
        })


    }

    override fun onResume() {
        super.onResume()
        Timber.e("RESUME")
//        getRoomList()
    }

    private fun getRoomList(){
        OpenviduSessionRetrofitClient.getInstance().getRoomList().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Timber.e("세션 리스트 제발!!!!!")
                Timber.e(response.headers().toString())
                Timber.e(response.errorBody().toString())
                Timber.e(response.code().toString())
                val body = response.body()
                if (body != null){
                    Timber.e("BODY 왜 안되지")
                    var roomList = ArrayList<Room>()
                    val receivedJson = JSONObject(body.string())
                    val sessionList = JSONArray(receivedJson.get("content").toString())
                    Timber.e(sessionList.length().toString())
                    if (sessionList.length() > 0){
                        for (index in 0 until sessionList.length()){
                            val session = JSONObject(sessionList[index].toString())
                            Timber.e(sessionList[index].toString())
                            val roomSession = session["sessionId"].toString()
                            Timber.e(session["id"].toString())
                            Timber.e(session["sessionId"].toString())
                            val connectionInfo = JSONObject(session["connections"].toString())
                            val count = connectionInfo["numberOfElements"].toString().toInt()
                            val connectionContent = JSONArray(connectionInfo["content"].toString())
                            val type = 0
                            if (connectionContent.length() > 0){
                                val firstConnectionInfo = JSONObject(connectionContent[0].toString())
                                val roomToken = firstConnectionInfo["token"].toString()
                                val clientData = JSONObject(firstConnectionInfo["clientData"].toString())
                                Timber.e(clientData.toString())
                                val roomId = clientData["roomId"].toString().toInt()
                                val title = clientData["roomTitle"].toString()
                                val roomItem = Room(roomId, title, type, count, roomToken, roomSession)
                                roomList.add(roomItem)
                            }
                        }
                    }
                    var link = roomListAdapterToList()
                    roomListAdapter = RoomListAdapter(roomList, link)
                    runningListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    runningListView.adapter = roomListAdapter
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun openRoom(room: Room) {
        val mainActivity = activity as MainActivity
        val roomFragment = RoomFragment(room)

        mainActivity.makeCurrentFragment(roomFragment, "hide")
    }

    inner class roomListAdapterToList {
        fun getRoomId(room: Room) {
            OpenviduRetrofitClient.getInstance().joinRoom(token, room.room_id).enqueue(object :Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val receivedBody = response.body()?.string()
                    Timber.e("TOKEN : ${token}, ROOM ID: ${room.room_id}")
                    if(receivedBody != null){
                        val newRoom = Room(room.room_id, room.title, room.type, room.count, receivedBody, room.roomSession)
                        openRoom(newRoom)
                    }else{
                        Toast.makeText(requireContext(), "모종의 이유로 입장이 불가능합니다.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Timber.e("연결 실패")
                }

            })

        }
    }

}