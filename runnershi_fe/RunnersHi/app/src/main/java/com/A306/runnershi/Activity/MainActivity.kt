package com.A306.runnershi.Activity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.A306.runnershi.Fragment.*
import com.A306.runnershi.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_activity.*

open class MainActivity : AppCompatActivity() {

    // 권한 관련 리스트 설정 : 나중에 허가 요청 받아서 연결하기!
//    val permission_list = arrayOf(
//        Manifest.permission.INTERNET,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 하단 Navigation Bar 설정
        // Fragment 할당
        val homeFragment = HomeFragment()
        val userSearchFragment = UserSearchFragment()
        val rankingFragment = RankingFragment()
        val profileFragment = ProfileFragment()

        // 첫 시작 Fragment
        makeCurrentFragment(homeFragment)

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

    // Fragment 변경을 위한 함수
    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.main_fragment, fragment)
            commit()
        }
    }
}