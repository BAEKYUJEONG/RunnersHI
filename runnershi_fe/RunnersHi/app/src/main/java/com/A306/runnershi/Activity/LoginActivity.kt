package com.A306.runnershi.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.A306.runnershi.Helper.HttpConnect
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener(View.OnClickListener {
            Log.e("테스트", "제발 나와라")
            var params = mapOf("postId" to 3)
            var httpConnect = HttpConnect("/posts", params)
            Log.e("테스트", httpConnect.post())
        })
    }
}