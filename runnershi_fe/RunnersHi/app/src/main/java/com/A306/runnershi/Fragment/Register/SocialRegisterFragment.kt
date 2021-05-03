package com.A306.runnershi.Fragment.Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.A306.runnershi.Activity.LoginActivity
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_normal_register.*
import kotlinx.android.synthetic.main.fragment_social_register.*
import kotlinx.android.synthetic.main.fragment_social_register.nicknameInput
import kotlinx.android.synthetic.main.fragment_social_register.toLoginButton

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

        // 가입 완료 버튼 클릭
        socialRegisterButton.setOnClickListener {
            var nickname = nicknameInput.text.toString().trim()
            when {
                nickname == "" -> {
                    Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 서버 통신 로직 넣기
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.overridePendingTransition(0,0)
                }
            }
        }
    }
}