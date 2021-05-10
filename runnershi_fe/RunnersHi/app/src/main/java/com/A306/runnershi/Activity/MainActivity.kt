package com.A306.runnershi.Activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Fragment.HomeFragment
import com.A306.runnershi.Fragment.ProfileFragment
import com.A306.runnershi.Fragment.RankingFragment
import com.A306.runnershi.Fragment.SingleRun.MapFragment
import com.A306.runnershi.Fragment.SingleRun.SingleRunFragment
import com.A306.runnershi.Fragment.UserSearchFragment
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    // 권한 관련 리스트 설정 : 나중에 허가 요청 받아서 연결하기!
//    val permission_list = arrayOf(
//        Manifest.permission.INTERNET,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//    )

    // 데이터 끌어오기
    @Inject
    lateinit var runDao: RunDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 하단 Navigation Bar 설정
        // Fragment 할당
        // 하단 메뉴 Fragments
        val homeFragment = HomeFragment()
        val userSearchFragment = UserSearchFragment()
        val rankingFragment = RankingFragment()
        val profileFragment = ProfileFragment()
        // 혼자 달리기 Fragments
        val singleRunFragment = SingleRunFragment()
        val mapFragment = MapFragment()

        // 첫 시작 Fragment
        makeCurrentFragment(homeFragment)

        navigateToRunningFragment(intent)

        // 하단 메뉴에 따른 Fragment 변경
        bottom_navigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_home -> makeCurrentFragment(homeFragment)
                R.id.navigation_search -> makeCurrentFragment(userSearchFragment)
                R.id.navigation_ranking -> makeCurrentFragment(rankingFragment)
                R.id.navigation_profile -> makeCurrentFragment(profileFragment)
            }
            true
        }



        // 달리기 버튼
        floatingActionButton.setOnClickListener {
            makeCurrentFragment(singleRunFragment, "hide")
            sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
        }

        val settingsActivity = Intent(this, SettingsActivity::class.java)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_alert -> {
                    true
                }
//               R.id.navigation_setting -> {
//                   val intent = Intent(this, SettingsActivity::class.java)
//                   startActivity(intent)
//               }
                R.id.navigation_setting -> startActivity(settingsActivity)
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToRunningFragment(intent)
    }

    fun sendCommandToService(action:String){
        Intent(this, TrackingService::class.java).also{
            it.action = action
            this.startService(it)
        }

//        // 상단 Top App Bar 설정
//        // Fragment 할당
//        val settingsFragment = SettingsFragment()
//
//        // 상단 메뉴 클릭할 경우
//        topAppBar.setOnClickListener{
//            val tran = supportFragmentManager.beginTransaction()
//
//            tran.add(R.id.main_fragment, settingsFragment)
//            tran.commit()
//        }

    }

    // Fragment 변경을 위한 함수
    fun makeCurrentFragment(fragment: Fragment, tag:String = "show"){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.main_fragment, fragment, tag)
            commit()
        }
        if (tag == "hide"){
            floatingActionButton.visibility = View.GONE
        }else{
            floatingActionButton.visibility = View.VISIBLE
        }
    }

    private fun navigateToRunningFragment(intent: Intent?){
        var singleRunFragment = SingleRunFragment()
        if(intent?.action == "ACTION_SHOW_TRACKING_FRAGMENT"){
            makeCurrentFragment(singleRunFragment)
        }
    }
}