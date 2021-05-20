package com.A306.runnershi.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Alarm
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.android.play.core.splitinstall.p.d
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alarm.*
import kotlinx.android.synthetic.main.item_alarm.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.alarms, AlarmActivity.AlarmFragment())
                .commit()
        }
    }

    @AndroidEntryPoint
    class AlarmFragment : Fragment() {

        private val viewModel: UserViewModel by viewModels()
        private lateinit var alarmAdapter: AlarmAdapter

        var link = acceptFriendAdapterToList()

        inner class acceptFriendAdapterToList {
            fun getFriendId(friend: Alarm) {
                //고유 값이라서 let이 들어감
                friend.userId?.let { friend.alarmId?.let { it1 -> acceptFriend(it, it1) } }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_alarm, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            getAlarmFriendInfo()
        }

        // 친구 신청 알람 정보 가져오기
        private fun getAlarmFriendInfo(){
            Timber.e("알림 통신")
            viewModel.userInfo.observe(viewLifecycleOwner, Observer {
                Timber.e(it.token)
                if (it?.token != null){
                    val token:String = it.token!!
                    RetrofitClient.getInstance().getAlarmFriendList(token).enqueue(object :
                        Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                            val userList = Gson().fromJson<List<Map<String, Any>>>(response.body()?.string(), itemType)
                            val userAlarmList = ArrayList<Alarm>()
                            for(user in userList){
                                val friendUserId = user["friendUserId"].toString()
                                val alarmId = user["alarmId"].toString().replace(".0","").toLong()
                                val friendName = user["fromUserName"].toString()
                                val content = user["content"].toString()
                                val userItem = Alarm(alarmId, friendUserId, friendName, content)
                                userAlarmList.add(userItem)
                            }

                            // 리사이클러뷰 셋업
                            setupRecyclerView(userAlarmList.toTypedArray())
                        }

                    })
                }
            })
        }

        // 친구 수락
        private fun acceptFriend(friendUserId: String, alarmId: Long){
            Timber.e("친구 ACCEPT 통신")

            viewModel.userInfo.observe(viewLifecycleOwner, Observer {
                Timber.e(it.token)
                if (it?.token != null){
                    val token:String = it.token!!
                    val body = mapOf("friendUserId" to friendUserId, "alarmId" to alarmId)

                    // alertDialog 선택
                    acceptButton.setOnClickListener {
                        // Dialog
                        val builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.Theme_AppCompat_Light_Dialog))
                        builder.setTitle("친구 수락")
                        builder.setMessage("친구를 수락하시겠습니까?")
                        builder.setPositiveButton("확인") { _, _ ->
                            d("alert ok")
                            // 여기서 레트로핏 통신
                            RetrofitClient.getInstance().acceptFriend(token, body as Map<String, String>).enqueue(afterAccpetFriend)
                        }
                        builder.setNegativeButton("취소") { _, _ ->
                            d("alert cancel")
                        }

                        builder.show()
                    }

                }
            })
        }

        private var afterAccpetFriend = object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "친구를 수락을 실패했습니다", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(context, "친구를 수락했습니다", Toast.LENGTH_LONG).show()
            }

        }

        private fun setupRecyclerView(alarmList:Array<Alarm>) = alarmRecyclerView.apply {
            alarmAdapter = AlarmAdapter(alarmList, link, this@AlarmFragment)
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}