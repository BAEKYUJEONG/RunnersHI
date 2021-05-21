package com.A306.runnershi.Fragment.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_running_detail.*

@AndroidEntryPoint
class RunningDetailFragment(receivedRun: Run) : Fragment(R.layout.fragment_running_detail) {
    val userViewModel:UserViewModel by viewModels()
    val thisRun = receivedRun

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userInfo.observe(viewLifecycleOwner, Observer{
            runningDetailTab.text = it.userName
            runningDetailTitle.text = thisRun.title
            detailDistance.text = thisRun.distance.toString()
            runningDetailTime.text = thisRun.time
            runningDetailPace.text = thisRun.pace
            detailRunImage.setImageBitmap(thisRun.img)
        })
    }
}