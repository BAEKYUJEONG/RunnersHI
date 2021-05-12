package com.A306.runnershi.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.R
import com.A306.runnershi.Repository.UserRepository
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timber.e(userDAO.toString())
        // 유저 없음
        if (userDAO.getUserInfo().value == null){
            var loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)
            overridePendingTransition(0, 0)
        }else{
            var mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            overridePendingTransition(0, 0)
        }
    }
}