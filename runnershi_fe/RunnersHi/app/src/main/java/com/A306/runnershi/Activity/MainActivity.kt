package com.A306.runnershi.Activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Fragment.GroupRun.CreateRoomFragment
import com.A306.runnershi.Fragment.GroupRun.GroupRunRoomListFragment
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Fragment.Profile.AchievementFragment
import com.A306.runnershi.Fragment.Profile.ProfileFragment
import com.A306.runnershi.Fragment.Ranking.RankingFragment
import com.A306.runnershi.Fragment.SingleRun.SingleRunFragment
import com.A306.runnershi.Fragment.UserSearch.UserSearchFragment
import com.A306.runnershi.Openvidu.OpenviduModel.RemoteParticipant
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.webrtc.MediaStream
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CAMERA = 100
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
    private val MY_PERMISSIONS_REQUEST = 102

    // 권한 관련 리스트 설정 : 나중에 허가 요청 받아서 연결하기!
//    val permission_list = arrayOf(
//        Manifest.permission.INTERNET,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//    )

    // 데이터 끌어오기
    @Inject
    lateinit var runDao: RunDAO
    @Inject
    lateinit var userDao: UserDAO

    var roomToken = ""
    var roomSession = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSpeedDial(savedInstanceState == null)

        Timber.e("권한이 있나?? ${arePermissionGranted()}")
        askForPermissions()


        // 하단 Navigation Bar 설정
        // Fragment 할당
        // 하단 메뉴 Fragments
        val homeFragment = HomeFragment()
        val userSearchFragment = UserSearchFragment()
        val rankingFragment = RankingFragment()
        val profileFragment = ProfileFragment()
        val achievementFragment = AchievementFragment()

        // 혼자 달리기 Fragments
//        val singleRunFragment = SingleRunFragment()
//        val mapFragment = MapFragment()
//
//        // 같이 달리기 Fragments
//        val groupRunFragment = GroupRunRoomListFragment()


        // 첫 시작 Fragment
        makeCurrentFragment(homeFragment)

        navigateToRunningFragment(intent)

        // 하단 메뉴에 따른 Fragment 변경
        bottom_navigation.setOnNavigationItemSelectedListener{
            val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
            when(it.itemId){
                R.id.navigation_home -> {
                    makeCurrentFragment(homeFragment)
                    speedDialView.close()
                }
                R.id.navigation_search -> {
                    makeCurrentFragment(userSearchFragment)
                    speedDialView.close()
                }
                R.id.navigation_ranking -> {
                    makeCurrentFragment(rankingFragment)
                    speedDialView.close()
                }
                R.id.navigation_profile -> {
                    makeCurrentFragment(profileFragment)
                    speedDialView.close()
                }
            }
            true
        }


        // 상단 앱바 관련
        val settingsActivity = Intent(this, SettingsActivity::class.java)

//        topAppBar.setOnMenuItemClickListener { menuItem ->
//            val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
//            when(menuItem.itemId) {
//                R.id.navigation_alert -> {
//                    // 여기 alert dialog 띄워주자
//                    speedDialView.close()
//                }
//                R.id.navigation_setting -> {
//                    startActivity(settingsActivity)
//                }
//            }
//            true
//        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToRunningFragment(intent)
    }

    fun sendCommandToService(action: String){
        Intent(this, TrackingService::class.java).also{
            it.action = action
            this.startService(it)
        }
    }

    // Fragment 변경을 위한 함수
    fun makeCurrentFragment(fragment: Fragment, tag: String = "show"){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.main_fragment, fragment, tag)
            commit()
        }

        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
        if (tag == "hide"){
            speedDialView.visibility = View.GONE
        }else{
            speedDialView.visibility = View.VISIBLE
        }
    }

    private fun navigateToRunningFragment(intent: Intent?){
        var singleRunFragment = SingleRunFragment()
        if(intent?.action == "ACTION_SHOW_TRACKING_FRAGMENT"){
            makeCurrentFragment(singleRunFragment)
        }
    }

//    //AchievementFragment로 이동하는 함수
//    fun showAchievemnt(fragment: Fragment){
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.ranking_layout, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }

    // 라디오 클릭
    fun radioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.allRankingRadio ->
                    if (checked) {
                        Timber.e("전체 클릭")

                        allRankingRadio.setTextColor(Color.parseColor("#ffffff"))
                        weekRankingRadio.setTextColor(Color.parseColor("#000000"))
                    }
                R.id.weekRankingRadio ->
                    if (checked) {
                        Timber.e("주간 클릭")
                        weekRankingRadio.setTextColor(Color.parseColor("#ffffff"))
                        allRankingRadio.setTextColor(Color.parseColor("#000000"))
                    }
            }
        }
    }

    // 스피드다이얼 버튼 설정
    private fun initSpeedDial(addActionItems: Boolean) {

        val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)

        val singleRunFragment = SingleRunFragment()
        val groupRunFragment = GroupRunRoomListFragment()
        val createRoomFragment = CreateRoomFragment()

        if (addActionItems) {

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id
                        .fab_SingleRun, R.drawable.ic_baseline_directions_run_24
                )
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.purple_200,
                            theme
                        )
                    )
                    .setLabel("혼자달리기")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.teal_700,
                            theme
                        )
                    )
                    .create()
            )

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id
                        .fab_GroupRun, R.drawable.ic_baseline_directions_run_24
                )
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.purple_200,
                            theme
                        )
                    )
                    .setLabel("같이달리기")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.teal_700,
                            theme
                        )
                    )
                    .create()
            )

            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                    R.id
                        .fab_CreateRoom, R.drawable.ic_baseline_directions_run_24
                )
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.purple_200,
                            theme
                        )
                    )
                    .setLabel("방 만들기")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.teal_700,
                            theme
                        )
                    )
                    .create()
            )
        }

        // Set main action clicklistener.
//        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
//            override fun onMainActionSelected(): Boolean {
//                showToast("Main action clicked!")
//                return false // True to keep the Speed Dial open
//            }
//
//            override fun onToggleChanged(isOpen: Boolean) {
//                Log.d(TAG, "Speed dial toggle state changed. Open = $isOpen")
//            }
//        })

        // Set option fabs clicklisteners.
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_SingleRun -> {
                    makeCurrentFragment(singleRunFragment, "hide")
                    sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
                    speedDialView.close()
                }
                R.id.fab_GroupRun -> {
                    makeCurrentFragment(groupRunFragment)
//                    sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
                    speedDialView.close()
                }
                R.id.fab_CreateRoom -> {
                    makeCurrentFragment(createRoomFragment)
                    speedDialView.close()
                }
            }
            true // To keep the Speed Dial open
        })
    }

    // RTC 관련 함수
    open fun setRemoteMediaStream(stream: MediaStream, remoteParticipant: RemoteParticipant) {
        val videoTrack = stream.videoTracks[0]
        videoTrack.addSink(remoteParticipant.getVideoView())
        runOnUiThread {
            remoteParticipant.getVideoView()?.setVisibility(View.VISIBLE)
        }
    }

    fun setTokenAndSession(token: String, sessionId: String){
        roomToken = token
        roomSession = sessionId
    }

    fun arePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_DENIED
    }

    fun askForPermissions() {
        Timber.e("권한 설정??")
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        }
    }

    fun createRemoteParticipantVideo(remoteParticipant: RemoteParticipant?){}
    fun leaveSession(){}
}