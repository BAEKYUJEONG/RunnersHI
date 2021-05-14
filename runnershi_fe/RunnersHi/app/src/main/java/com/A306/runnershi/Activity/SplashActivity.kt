package com.A306.runnershi.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.userInfo.observe(this, Observer {
            if(it == null){
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                overridePendingTransition(0, 0)
            } else{
                Timber.e(it.token.toString())
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                overridePendingTransition(0, 0)
            }
        })
    }

}