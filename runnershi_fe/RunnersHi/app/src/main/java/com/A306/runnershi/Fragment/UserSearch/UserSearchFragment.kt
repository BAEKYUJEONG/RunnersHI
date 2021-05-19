package com.A306.runnershi.Fragment.UserSearch

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.KeyframesSpec
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.User
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_search.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class UserSearchFragment : Fragment() {

    // 임시로 넘겨줄 UserList:
    var tempUserList: ArrayList<User> = arrayListOf(
        User(null, null, "티캔", null),
        User(null, null, "디니", null),
        User(null, null, "바비", null),
        User(null, null, "에리얼", null),
        User(null, null, "클로이", null)
    )

    // SearchedUserList 만들어줄 어댑터
    private lateinit var searchedUserListAdapter: SearchedUserListAdapter

    var userList: ArrayList<User> = ArrayList()

    // 친구 요청 보내줄 때 필요한 사항들
    private val viewModel: UserViewModel by viewModels()
    var token = ""
    var myId = ""

    var friendUserName = ""

    inner class searchedUserAdapterToList {
        fun getUserId(user: User) {
            sendFriendRequest(user.userName)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 친구 요청을 대비해서 유저 토큰도 미리 꺼내서 준비해보자:
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.e("유저서치탭에서 유저 정보 가져오는데", "오류가 날수가 없는데...")
            } else {
                token = it.token.toString()
                myId = it.userId.toString()
            }
        })
        viewModel.userInfo.removeObserver(Observer {  })

        // 키업될때마다 유저 서치
        userName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            // TODO("백키 누를때도 마찬가지로 키이벤트 적용하기")
            if (event.action == KeyEvent.ACTION_UP) {
                val userName = userName.text.toString()
                userSearchRequest(userName)
                return@OnKeyListener true
            }
            false
        })


        // 서치해서 받은 유저 리스트 보여주기
//        var userList: ArrayList<User> = userList
//        var link = searchedUserAdapterToList()
//
//        searchedUserListAdapter = SearchedUserListAdapter(userList, link)
//        searchedUserListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
//        searchedUserListView.adapter = searchedUserListAdapter
    }


    // 여기부터는 유저 서치 부분

    private fun userSearchRequest(userName: String) {
        Log.i("userSearchRequest", "$userName")
        RetrofitClient.getInstance().searchUserName(token, userName).enqueue(afterUserSearchRequest)
    }

    private var afterUserSearchRequest = object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("유저서치리퀘스트에서", "통신이 안됐답니다!")
        }
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            Log.e("유저서치리퀘스트에서", "통신은 하나?")
            val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val searchedUserList =
                Gson().fromJson<List<Map<String, Any>>>(response.body()?.string(), itemType)

            if (searchedUserList != null) {

                //매번 클리어해줘야하므로
                userList = ArrayList()
                for (user in searchedUserList) {
                    val userId = user["userId"].toString()
                    val userName = user["userName"].toString()

                    // TODO("이미 친구인 경우 구분해서 표기해주기")
                    // 이때 userList는 User class의 List가 아니고 User, Boolean(친구여부) 쌍의 리스트여야 한다!
                    if (userId != myId) {
                        userList.add(User(null, userId, userName, null))
                    }
                }

                // 이때 어댑터로 넘겨서 리사이클러뷰 불러와주자
                var link = searchedUserAdapterToList()

                searchedUserListAdapter = SearchedUserListAdapter(userList, link)
                searchedUserListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                searchedUserListView.adapter = searchedUserListAdapter
            } else {
                Log.i("afterUserSearchRequest", "널인경우인가?")
            }
        }
    }

    // 여기부터는 친구 요청 부분
    fun sendFriendRequest(userName: String?) {
        friendUserName = userName.toString()
        val body = mapOf("friendUserName" to friendUserName)
        RetrofitClient.getInstance().addFriend(token, body).enqueue(afterSendFriendRequest)
    }

    // 레트로핏 통신 이후: 리스폰스 확인해서 다이얼로그 띄워주자
    private var afterSendFriendRequest = object: Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//            val result = Gson().fromJson<String>(response.body()?.string())
            val result = response.body()?.string().toString()
            Log.i("리스폰스", "$result")
            if (result == "SUCCESS") {
                Toast.makeText(context, "친구요청을 보냈습니다", Toast.LENGTH_LONG).show()
                Log.i("친구요청", "성공")
            } else {
                Toast.makeText(context, "친구요청을 보내지 못했습니다", Toast.LENGTH_LONG).show()
                Log.i("친구요청", "실패")
            }
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("센드프렌드리퀘스트에서", "통신이 안됐답니다!")
        }
    }
}