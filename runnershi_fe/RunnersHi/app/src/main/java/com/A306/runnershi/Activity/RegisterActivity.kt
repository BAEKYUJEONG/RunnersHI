package com.A306.runnershi.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.A306.runnershi.Fragment.NormalRegisterFragment
import com.A306.runnershi.Fragment.SocialRegisterFragment
import com.A306.runnershi.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Fragment 할당
        val normalRegisterFragment = NormalRegisterFragment()
        val socialRegisterFragment = SocialRegisterFragment()

        // 로그인 페이지에서 넘어온 경로 확인
        when (intent.getStringExtra("loginType")) {
            "Normal" -> {
                makeCurrentFragment(normalRegisterFragment)
            }
            "Social" -> {
                makeCurrentFragment(socialRegisterFragment)
            }
            else -> {
                Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        //



    }

    // Fragment 변경을 위한 함수
    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.registerFragment, fragment)
            commit()
        }
    }
}