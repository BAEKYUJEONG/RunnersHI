package com.A306.runnershi.Fragment.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Friend
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_friend.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_friend.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class FriendFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var friendAdapter: FriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFriendInfo()
    }

    // 친구 정보 가져오기
    private fun getFriendInfo(){
        Timber.e("친구 통신")
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            Timber.e(it.token)
            if (it?.token != null){
                val token:String = it.token!!
                RetrofitClient.getInstance().getFriendList(token).enqueue(object : Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val itemType = object : TypeToken<Map<String, Any>>() {}.type
                        val friendMap = Gson().fromJson<Map<String, Any>>(response.body()?.string(), itemType)
                        val friendList = friendMap["friendList"]
                        Timber.e(friendList.toString())

                        val friendViewList = ArrayList<Friend>()

                        val listItemType = object  : TypeToken<List<Map<String, Any>>>(){}.type
                        val friend_list = Gson().fromJson<List<Map<String, Any>>>(friendList.toString(), listItemType)

                        for(friend in friend_list){
                            Timber.e(friend["userName"].toString())
                            val friendName = friend["userName"].toString()
                            val friendRank = friend["rank"].toString().replace(".0","")
                            val friendItem = Friend(friendName, friendRank)
                            friendViewList.add(friendItem)
                        }
                        setupRecyclerView(friendViewList.toTypedArray())
                    }

                })
            }
        })
    }

    private fun setupRecyclerView(friendList:Array<Friend>) = friendRecyclerView.apply {
        friendAdapter = FriendAdapter(friendList, this@FriendFragment)
        adapter = friendAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}