package com.A306.runnershi.Fragment.Home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Detail
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventClick()
        getTimelineInfo()
    }

    private fun eventClick(){

        val mainActivity = activity as MainActivity
        val eventFragment = EventFragment()

        eventCardView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }

        eventImageView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }

        eventTextView.setOnClickListener{
            mainActivity.makeCurrentFragment(eventFragment)
        }
    }

    // 타임라인 정보 가져오기
    private fun getTimelineInfo(){
        Timber.e("타임라인 통신")
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            Timber.e(it.token)
            if(it?.token != null){
                val token:String = it.token!!
                RetrofitClient.getInstance().recordList(token).enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
                        val timelineList = Gson().fromJson<List<Map<String, Any>>>(response.body()?.string(), itemType)
                        val oneTimelineList = ArrayList<Detail>()
                        for(user in timelineList){
                            val userName = user["userName"].toString()

                            // endTime 변환하기
//                            val endTime = user["endTime"].toString().toLong()
                            val timeDate = user["endTime"].toString()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")
                            val formattedDate = LocalDate.parse(timeDate, formatter).toString()
                            val formattedTime = LocalTime.parse(timeDate, formatter).toString()

//                            val endTime = LocalDateTime.parse(timeDate, formatter)

                            val formattedRunningTime = user["runningTime"].toString().toDouble().toInt()
                            Timber.e("러닝타임은 왜 이렇게 찍힐까 $formattedRunningTime")
                            val runningTime = formattedRunningTime

                            val title = user["title"].toString()
                            val distance = user["distance"].toString().toDouble()
                            val timeline = Detail(userName, formattedDate, formattedTime, runningTime, title, distance)
                            oneTimelineList.add(timeline)
                        }
                        setupRecyclerView(oneTimelineList.toTypedArray())
                    }

                })
            }
        })

    }

    private fun setupRecyclerView(timelineList:Array<Detail>) = homeRecyclerView.apply{
        timelineAdapter = TimelineAdapter(timelineList, this@HomeFragment)
        adapter = timelineAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}