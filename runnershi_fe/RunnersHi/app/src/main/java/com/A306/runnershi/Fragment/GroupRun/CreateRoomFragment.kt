package com.A306.runnershi.Fragment.GroupRun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Room
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_room.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class CreateRoomFragment : Fragment(R.layout.fragment_create_room) {

    // 우선 방 만들 때 필요한 유저의 토큰을 뷰모델을 통해 받아온다:
    private val viewModel: UserViewModel by viewModels()
    var token = ""

    // 방 생성 후 넘겨줄 파라미터들도 미리 설정하자
    var roomId = 0
    var title = ""
    var type = 0
    var members = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 유저 토큰 꺼내서 정의해보자:
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.e("방만들기 오류", "널값일수가 없는데요...")
            } else {
                token = it.token.toString()
            }
        })

        // 방 만들기 버튼 클릭
        createRoomButton.setOnClickListener {
            title = createRoomName.text.toString()
            val body = mapOf("title" to title, "type" to type, "members" to members)
            RetrofitClient.getInstanceTemp().roomCreate(token, body).enqueue(afterRetrofitConnection)

            Log.i("크리에이트룸", "온클릭리스너")
        }
        viewModel.userInfo.removeObserver(Observer {  })
    }

    // 레트로핏 통신 이후: 방 만들어졌으므로 방 화면으로 연결해주자
    private var afterRetrofitConnection = object: Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            // response 받은 roomID를 함께 담아서:
            roomId = Gson().fromJson(response.body()?.string(), Map::class.java)["roomId"].toString().toInt()
            val room = Room(roomId, title, type, 1)

            // 우리의 GroupRunRoom Fragment로 보내주면 됩니다!
            val mainActivity = activity as MainActivity
            val roomFragment = RoomFragment(room)

            mainActivity.makeCurrentFragment(roomFragment, "hide")
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("크리에이트룸에서", "통신이 안됐답니다!")
        }
    }

}
