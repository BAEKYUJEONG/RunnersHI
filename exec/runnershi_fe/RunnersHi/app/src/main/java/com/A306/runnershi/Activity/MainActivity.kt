package com.A306.runnershi.Activity


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Dao.RunDAO
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Fragment.GroupRun.CreateRoomFragment
import com.A306.runnershi.Fragment.GroupRun.GroupRunRoomListFragment
import com.A306.runnershi.Fragment.GroupRun.RoomFragment
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Fragment.Profile.AchievementFragment
import com.A306.runnershi.Fragment.Profile.ProfileFragment
import com.A306.runnershi.Fragment.Ranking.RankingFragment
import com.A306.runnershi.Fragment.SingleRun.SingleRunFragment
import com.A306.runnershi.Fragment.UserSearch.UserSearchFragment
import com.A306.runnershi.Model.Run
import com.A306.runnershi.Openvidu.model.RemoteParticipant
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlinx.android.synthetic.main.fragment_run_result.*
import kotlinx.android.synthetic.main.fragment_single_run.*
import org.webrtc.MediaStream
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
open class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val MY_PERMISSIONS_REQUEST_CAMERA = 100
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
    private val MY_PERMISSIONS_REQUEST = 102

    // ?????? ?????? ????????? ?????? : ????????? ?????? ?????? ????????? ????????????!
//    val permission_list = arrayOf(
//        Manifest.permission.INTERNET,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//    )

    // ????????? ????????????
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

        Timber.e("????????? ???????? ${arePermissionGranted()}")
        requestPermissions()


        // ?????? Navigation Bar ??????
        // Fragment ??????
        // ?????? ?????? Fragments
        val homeFragment = HomeFragment()
        val userSearchFragment = UserSearchFragment()
        val rankingFragment = RankingFragment()
        val profileFragment = ProfileFragment()
        val achievementFragment = AchievementFragment()

        // ?????? ????????? Fragments
//        val singleRunFragment = SingleRunFragment()
//        val mapFragment = MapFragment()
//
//        // ?????? ????????? Fragments
//        val groupRunFragment = GroupRunRoomListFragment()


        // ??? ?????? Fragment
        makeCurrentFragment(homeFragment)

        navigateToRunningFragment(intent)

        // ?????? ????????? ?????? Fragment ??????
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


        // ?????? ?????? ??????
        val settingsActivity = Intent(this, SettingsActivity::class.java)
        val alarmActivity = Intent(this, AlarmActivity::class.java)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            val speedDialView = findViewById<SpeedDialView>(R.id.speedDial)
            when(menuItem.itemId) {
                R.id.navigation_alert -> {
                    startActivity(alarmActivity)
                    speedDialView.close()
                }
                R.id.navigation_setting -> {
                    startActivity(settingsActivity)
                }
            }
            true
        }
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

    // Fragment ????????? ?????? ??????
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

//    //AchievementFragment??? ???????????? ??????
//    fun showAchievemnt(fragment: Fragment){
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.ranking_layout, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }

    // ????????? ??????
    fun radioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.allRankingRadio ->
                    if (checked) {
                        Timber.e("?????? ??????")

                        allRankingRadio.setTextColor(Color.parseColor("#ffffff"))
                        weekRankingRadio.setTextColor(Color.parseColor("#000000"))
                    }
                R.id.weekRankingRadio ->
                    if (checked) {
                        Timber.e("?????? ??????")
                        weekRankingRadio.setTextColor(Color.parseColor("#ffffff"))
                        allRankingRadio.setTextColor(Color.parseColor("#000000"))
                    }
            }
        }
    }

    // ?????????????????? ?????? ??????
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
                    .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.purple_4, theme))
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.white,
                            theme
                        )
                    )
                    .setLabel("???????????????")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.purple_4,
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
                    .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.purple_3, theme))
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.white,
                            theme
                        )
                    )
                    .setLabel("???????????????")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.purple_3,
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
                    .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.purple_2, theme))
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.white,
                            theme
                        )
                    )
                    .setLabel("??? ?????????")
                    .setLabelColor(Color.WHITE)
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources, R.color.purple_2,
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

    // RTC ?????? ??????
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
        Timber.e("?????? ????????")
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

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(this) && TrackingUtility.hasVoicePermissions(this)){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                    this,
                    "??? ????????? ?????? ?????? ?????? ?????? ????????? ???????????????.",
                    0,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            )
        } else{
            EasyPermissions.requestPermissions(
                    this,
                    "??? ????????? ?????? ?????? ?????? ?????? ????????? ???????????????.",
                    0,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>){}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        Log.e("PERMISSION RESULT", requestCode.toString())
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}