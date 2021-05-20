package com.A306.runnershi.Fragment.Ranking

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Ranking
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ranking.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var rankingAdapter: RankingAdapter

    // 전체, 친구
    var selectedGroup: String = "전체"
    // 전체, 주간
    var selectedDate:String = "전체"
    // 거리 순, 페이스 순, 달린 시간 순
    var selectedCategory:String = "거리 순"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 상단 스피너
        addAdapter(R.array.group_spinner, group_spinner)
        // 중간 스피너
        addAdapter(R.array.category_spinner, category_spinner)

        group_spinner.onItemSelectedListener = GroupSpinnerClass()
        category_spinner.onItemSelectedListener = CategorySpinnerClass()

        allRankingRadio.setOnClickListener {
            if(selectedDate != "전체"){
                selectedDate = "전체"
                getRankingInfo()
            }
        }

        weekRankingRadio.setOnClickListener {
            if (selectedDate != "주간"){
                selectedDate = "주간"
                getRankingInfo()
            }
        }
    }

    // 랭킹 정보 가져오기
    private fun getRankingInfo(){
        Timber.e("랭킹 통신")
        if (selectedGroup != "" && selectedCategory != "" && selectedDate != ""){
            Timber.e("설마 들어오지도 못하나")
            // 비동기 처리
            viewModel.userInfo.observe(viewLifecycleOwner, Observer {
                Timber.e(it.token)
                if (it?.token != null){
                    val token:String = it.token!!
                    var type = ""
                    when (selectedCategory) {
                        "거리 순" -> {
                            type = "distance"
                        }
                        "페이스 순" -> {
                            type = "pace"
                        }
                        "달린 시간 순" -> {
                            type = "time"
                        }
                    }

                    val body = mapOf("type" to type, "offset" to 0 )
                    // 서버 통신 시작
                    if (selectedGroup == "전체"){
                        if (selectedDate == "전체") {
                            RetrofitClient.getInstance().totalAllRanking(token, body).enqueue(afterRetrofitConnection)
                        } else if(selectedDate == "주간"){
                            RetrofitClient.getInstance().weeklyAllRanking(token, body).enqueue(afterRetrofitConnection)
                        }
                    }else if(selectedGroup == "친구"){
                        if (selectedDate == "전체") {
                            RetrofitClient.getInstance().totalFriendRanking(token, body).enqueue(afterRetrofitConnection)
                        } else if(selectedDate == "주간"){
                            RetrofitClient.getInstance().weeklyFriendRanking(token, body).enqueue(afterRetrofitConnection)
                        }
                    }
                }
                viewModel.userInfo.removeObserver(Observer {  })
            })
        }
    }
    
    // 레트로핏 통신 이후 실행
    private var afterRetrofitConnection = object:Callback<ResponseBody>{
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val itemType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val userList = Gson().fromJson<List<Map<String, Any>>>(response.body()?.string(), itemType)
            val userRankingList = ArrayList<Ranking>()
            for (user in userList){
                val userId = user["userId"].toString()
                val userName = user["userName"].toString()
                var userData = 0.0
                Timber.e(user.toString())
                when (selectedCategory) {
                    "거리 순" -> {
                        userData = user["distance"].toString().toDouble()
                    }
                    "페이스 순" -> {
                        userData = user["pace"].toString().toDouble()
                    }
                    "달린 시간 순" -> {
                        userData = user["time"].toString().toDouble()
                    }
                }
                val ranking = Ranking(userId, userName, userData)
                Timber.e("유저 아이디 : $userId, 유저 이름 : $userName, 유저 데이터 : $userData")
                userRankingList.add(ranking)
            }
            setupRecyclerView(userRankingList.toTypedArray())
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            TODO("Not yet implemented")
        }
    }

    private fun setupRecyclerView(rankingList:Array<Ranking>) = rankingListView.apply {
        rankingAdapter = RankingAdapter(rankingList, selectedCategory, this@RankingFragment)
        adapter = rankingAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    fun itemClick(receivedData:String){
        Timber.e("CLICK!! : $receivedData")
    }

    inner class GroupSpinnerClass:OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedGroup = parent?.getItemAtPosition(position).toString()
            getRankingInfo()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    inner class CategorySpinnerClass:OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedCategory = parent?.getItemAtPosition(position).toString()
            getRankingInfo()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    // 스피너 어댑터 추가
    private fun addAdapter(stringArray:Int, whichSpinner: Spinner){
        ArrayAdapter.createFromResource(
                requireContext(),
                stringArray,
                android.R.layout.simple_spinner_item
        ).also{
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            whichSpinner.adapter = adapter
        }
    }
}