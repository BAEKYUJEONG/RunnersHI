package com.A306.runnershi.Fragment.GroupRun

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_create_room.*

class CreateRoomFragment : Fragment(R.layout.fragment_create_room) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 방 만들기 버튼 클릭
        createRoomButton.setOnClickListener {

        }

    }
}