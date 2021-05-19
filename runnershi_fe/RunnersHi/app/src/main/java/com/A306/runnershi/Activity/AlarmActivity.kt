package com.A306.runnershi.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Alarm
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alarm.*
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
                                val friendName = user["fromUserName"].toString()
                                val content = user["content"].toString()
                                val userItem = Alarm(friendName, content)
                                userAlarmList.add(userItem)
                            }
                            setupRecyclerView(userAlarmList.toTypedArray())
                        }

                    })
                }
            })
        }

        private fun setupRecyclerView(alarmList:Array<Alarm>) = alarmRecyclerView.apply {
            alarmAdapter = AlarmAdapter(alarmList, this@AlarmFragment)
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}