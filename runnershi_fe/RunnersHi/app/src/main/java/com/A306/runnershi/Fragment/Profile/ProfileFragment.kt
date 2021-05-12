package com.A306.runnershi.Fragment.Profile

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
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.SingleRunViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.history.view.*

@AndroidEntryPoint
class ProfileFragment : Fragment() { //, View.OnClickListener

    private val viewModel : SingleRunViewModel by viewModels()
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

        val mainActivity = activity as MainActivity
        val achievementFragment = AchievementFragment()

        //업적을 없애면 랭킹을 넣으면 될 듯
        achievement_layout.setOnClickListener{
            mainActivity.makeCurrentFragment(achievementFragment)
        }
        freindClick()
        setupRecyclerView()

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setupRecyclerView() = historyListView.apply {
        runAdapter = RunAdapter()
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

        friend_layout.setOnClickListener{
            mainActivity.makeCurrentFragment(friendFragment)
        }

        friend_text.setOnClickListener{
            mainActivity.makeCurrentFragment(friendFragment)
        }

        friend_num.setOnClickListener{
            mainActivity.makeCurrentFragment(friendFragment)
        }
    }
}