package com.A306.runnershi.Fragment.Profile

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Fragment.Ranking.RankingFragment
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.SingleRunViewModel
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.history.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : Fragment() { //, View.OnClickListener

    private val userViewModel: UserViewModel by viewModels()
    private val singleRunViewModel : SingleRunViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter
    var place = arrayOf(
            "노원구" , "군포", "대야동", "송파구", "강남구"
    )
    var time = arrayOf(
            "55", "48", "23", "12", "37"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //achievement_layout.setOnClickListener(this)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rankingClick()
        freindClick()
        setupProfile()
        setFontColor()
        setupRecyclerView()

        singleRunViewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setupProfile(){
        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            val token = it.token

            if (token != null) {
                RetrofitClient.getInstance().userProfile(token).enqueue(object:Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val user = Gson().fromJson(response.body()?.string(), Map::class.java)
                        //val userId = user["userId"].toString()
                        profileTab.text = user["userName"].toString()
                        ranking_num.text = user["totalRank"].toString().replace(".0","")
                        distance.text = user["total_distance"].toString()
                        pace.text = user["best_pace"].toString()
                    }
                })

                RetrofitClient.getInstance().getFriendList(token).enqueue(object:Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val friend = Gson().fromJson(response.body()?.string(), Map::class.java)
                        friend_num.text = friend["friendNum"].toString().replace(".0","")
                    }

                })
            }
        })
    }

    fun setFontColor() {
        profileTab.setTextColor(Color.rgb(82,40,136))
        ranking_text.setTextColor(Color.rgb(82,40,136))
        friend_text.setTextColor(Color.rgb(82,40,136))
        ranking_num.setTextColor(Color.rgb(82,40,136))
        friend_num.setTextColor(Color.rgb(82,40,136))
        profiledistance.setTextColor(Color.rgb(82,40,136))
        profilepace.setTextColor(Color.rgb(82,40,136))
        recent.setTextColor(Color.rgb(82,40,136))
    }

    private fun setupRecyclerView() = historyListView.apply {
        var link = runAdapterToList()

        runAdapter = RunAdapter(link)
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    // RecyclerView의 Adapter 클래스
    inner class RecyclerAdapter :RecyclerView.Adapter<RecyclerAdapter.ViewHolderClass>(){

        // 항목 구성을 위해 사용할 ViewHolder 객체가 필요할 때 호출되는 메서드
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            // 항목으로 사용할 View 객체를 생성한다.
            val itemView = layoutInflater.inflate(R.layout.history, null)
            val holder = ViewHolderClass(itemView)

            return holder
        }

        // ViewHolder를 통해 항목을 구성할 때 항목 내의 View 객체에 데이터를 셋팅한다.
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.placeTextView.text = place[position]
            holder.timeTextView.text = time[position]
        }

        // RecyclerView의 항목 개수를 반환한다.
        override fun getItemCount(): Int {
            return place.size
        }

        // ViewHolder 클래스
        inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView){
            // 항목 View 내부의 View 객체의 주소값을 담는다.
            val placeTextView = itemView.placeTextView
            val timeTextView = itemView.timeTextView
        }
    }

//    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.achievement_layout -> if(achievement_layout.visibility == View.VISIBLE){
//                achievement_layout.visibility = View.GONE
//            }else {
//                achievement_layout.visibility = View.VISIBLE
//            }
//        }
//    }

    private fun freindClick(){
        val mainActivity = activity as MainActivity
        val friendFragment = FriendFragment()
        friend_text.setOnClickListener{
            mainActivity.makeCurrentFragment(friendFragment)
        }

        friend_num.setOnClickListener{
            mainActivity.makeCurrentFragment(friendFragment)
        }
    }

    private fun rankingClick(){
        val mainActivity = activity as MainActivity
        val rankingFragment = RankingFragment()

        ranking_text.setOnClickListener{
            mainActivity.makeCurrentFragment(rankingFragment)
        }

        ranking_num.setOnClickListener{
            mainActivity.makeCurrentFragment(rankingFragment)
        }
    }

    inner class runAdapterToList {

        fun getRunningDetailId() {
            openRunningDetail()
        }
    }

    private fun openRunningDetail() {
        val mainActivity = activity as MainActivity
        val runningDetailFragment = RunningDetailFragment()

        mainActivity.makeCurrentFragment(runningDetailFragment)
    }
}