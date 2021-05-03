package com.A306.runnershi.Fragment.Register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.A306.runnershi.Activity.LoginActivity
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.fragment_normal_register.*

class NormalRegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_normal_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 로그인 버튼 클릭
        toLoginButton.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.overridePendingTransition(0,0)
        }

        normalRegisterButton.setOnClickListener {
            var email = emailInput.text.toString().trim()
            var pwd = pwdInput.text.toString().trim()
            var nickname = nicknameInput.text.toString().trim()
            var emailCheck = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            when {
                email == "" -> {
                    Toast.makeText(activity, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                !emailCheck -> {
                    Toast.makeText(activity, "이메일형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
                nickname == "" -> {
                    Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                pwd == "" -> {
                    Toast.makeText(activity, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
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