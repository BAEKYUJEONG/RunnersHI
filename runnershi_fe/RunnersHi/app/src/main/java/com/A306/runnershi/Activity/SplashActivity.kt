package com.A306.runnershi.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.A306.runnershi.DB.RunningDB
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Model.User
import com.A306.runnershi.R
import com.A306.runnershi.Repository.UserRepository
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val db = Room.databaseBuilder(
            this,
            RunningDB::class.java, "runnershi_db"
        ).allowMainThreadQueries().build()

        val checkUser = db.getUserDao().getUserInfo()

        if (checkUser != null){
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            overridePendingTransition(0, 0)
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            overridePendingTransition(0, 0)
        }

    }

}