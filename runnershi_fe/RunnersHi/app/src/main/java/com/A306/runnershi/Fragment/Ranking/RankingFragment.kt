package com.A306.runnershi.Fragment.Ranking

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ranking.*
import timber.log.Timber

@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private val viewModel: UserViewModel by viewModels()

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

            Timber.e("FRAGMENT 전체 클릭")
        }

        weekRankingRadio.setOnClickListener {
            selectedDate = "주간"
            Timber.e("FRAGMENT 주간 클릭")
        }
    }

    // 랭킹 정보 가져오기
    private fun getRankingInfo(){
        Timber.e("랭킹 통신")
        if (selectedGroup != "" && selectedCategory != "" && selectedDate != ""){
            Timber.e("설마 들어오지도 못하나")
            var type:String = ""
            when (selectedCategory) {
                "거리 순" -> {
                    type = "time"
                }
                "페이스 순" -> {
                    type = "pace"
                }
                "달린 시간 순" -> {
                    type = "distance"
                }
            }
            
            // 비동기 처리
            viewModel.userInfo.observe(viewLifecycleOwner, Observer {
                Timber.e(it.token)
                if (it?.token != null){
                    // 서버 통신 시작
                }
                viewModel.userInfo.removeObserver(Observer {  })
            })

            if (selectedGroup == "전체"){
                if(selectedDate == "전체"){

                } else if (selectedDate == "주간"){

                }

            }else if (selectedGroup == "친구"){

            }
        }
    }

    inner class GroupSpinnerClass:OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedGroup = parent?.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    inner class CategorySpinnerClass:OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedCategory = parent?.getItemAtPosition(position).toString()
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