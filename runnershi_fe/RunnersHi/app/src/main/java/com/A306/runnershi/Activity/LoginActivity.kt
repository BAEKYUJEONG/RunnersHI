package com.A306.runnershi.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.A306.runnershi.Helper.HttpConnect
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 버튼 클릭
        // 회원가입 페이지로 이동
        socialRegisterButton.setOnClickListener {
            val registerIntent:Intent = Intent(this,RegisterActivity::class.java).apply {
                putExtra("loginType", "Normal")
            }
            startActivity(registerIntent)
            overridePendingTransition(0,0)
        }


        // 로그인 버튼 클릭
        // 서버 통신으로 로그인
        toLoginButton.setOnClickListener {
            val userEmail = emailInput.text.toString()
            val userPwd = pwdInput.text.toString()
            val loginParams = mapOf("email" to userEmail, "pwd" to userPwd)
            // 서버 통신
            val loginConnect = HttpConnect("/login", loginParams)
        }

        // 카카오 로그인 버튼 클릭
        kakaoLogin.setOnClickListener {
            val registerIntent:Intent = Intent(this,RegisterActivity::class.java).apply {
                putExtra("loginType", "Social")
            }
            startActivity(registerIntent)
            overridePendingTransition(0,0)
        }

        // 네이버 로그인 클릭
        naverLogin.setOnClickListener {

        }
    }
}