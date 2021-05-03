package com.A306.runnershi.Fragment.Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.A306.runnershi.Activity.LoginActivity
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_social_register.*

class SocialRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_social_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 로그인 버튼 클릭
        toLoginButton.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.overridePendingTransition(0,0)
        }
    }
}