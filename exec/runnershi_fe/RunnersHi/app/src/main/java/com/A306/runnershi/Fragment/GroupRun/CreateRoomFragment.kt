package com.A306.runnershi.Fragment.GroupRun

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Openvidu.OpenviduRetrofitClient
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.neovisionaries.ws.client.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_room.*
import okhttp3.ResponseBody
import org.webrtc.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.net.URLDecoder


@AndroidEntryPoint
class CreateRoomFragment : Fragment(R.layout.fragment_create_room) {

    // 우선 방 만들 때 필요한 유저의 토큰을 뷰모델을 통해 받아온다:
    private val viewModel: UserViewModel by viewModels()
    var token = ""

    // 방 생성 후 넘겨줄 파라미터들도 미리 설정하자
    var roomId = 0
    var userId = ""
    var title = ""
    var type = 0
    var members = ""
    var mainActivity:MainActivity? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        // 유저 토큰 꺼내서 정의해보자:
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.e("방만들기 오류", "널값일수가 없는데요...")
            } else {
                token = it.token.toString()
                userId = it.userId.toString()
                members = it.userId.toString()
            }
        })
        viewModel.userInfo.removeObserver(Observer {  })


        // 방 만들기 버튼 클릭
        createRoomButton.setOnClickListener {
            title = createRoomName.text.toString()
            val body = mapOf("title" to title, "type" to type, "members" to members)
            OpenviduRetrofitClient.getInstance().createRoom(token, body).enqueue(
                afterRetrofitConnection
            )
        }
        viewModel.userInfo.removeObserver(Observer { })
    }

    // 레트로핏 통신 이후: 방 만들어졌으므로 방 화면으로 연결해주자
    private var afterRetrofitConnection = object:Callback<ResponseBody>{
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val receivedText = response.body()?.string()
            Timber.e("RECEIVED NUMBER ${receivedText}")
            if (receivedText != null){
                roomId = receivedText.toInt()
//            roomId = 93
                Timber.e(roomId.toString())
                Timber.e("JOIN 시작 $members")
                OpenviduRetrofitClient.getInstance().joinRoom(token, roomId).enqueue(
                        afterRoomNumberReceived
                )
            }else{
                Toast.makeText(requireContext(), "다시 시도해주세요.", Toast.LENGTH_LONG).show()
            }

        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Timber.e(t.cause.toString())
            Timber.e(t.message.toString())
            Timber.e(t.localizedMessage.toString())
        }
    }

    // 방 아이디 받은 이후
    private var afterRoomNumberReceived = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            Timber.e("JOIN 통신!!")
            if (response.body() == null){
                Timber.e("왜 답이 없니")
            } else{
                Timber.e("드디어 방에 들어간다.")
                val receivedURL = response.body()?.string()
                Timber.e("소켓 URL : ${receivedURL}")
                val sessionMap = getSessionIdAndToken(receivedURL)

                Timber.e(sessionMap["sessionId"])
                mainActivity?.setTokenAndSession(receivedURL.toString(), sessionMap["sessionId"].toString())
                val room = Room(roomId, title, type, 1, receivedURL.toString(), sessionMap["sessionId"].toString())
                val roomFragment = RoomFragment(room)
                mainActivity?.makeCurrentFragment(roomFragment)
            }

        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            TODO("Not yet implemented")
        }
    }

    private fun getSessionIdAndToken(receivedText: String?):Map<String, String>{
        if (receivedText != null){
            val sessionMap = HashMap<String, String>()
            val firstIndex = receivedText.indexOf("?")
            val query = receivedText.substring(firstIndex + 1)
            val queryPairs = query.split("&")
            for (pair in queryPairs){
                val idx = pair.indexOf("=")
                sessionMap[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] = URLDecoder.decode(
                    pair.substring(
                        idx + 1
                    ), "UTF-8"
                )
            }
            return sessionMap
        }
        return emptyMap()
    }
}
