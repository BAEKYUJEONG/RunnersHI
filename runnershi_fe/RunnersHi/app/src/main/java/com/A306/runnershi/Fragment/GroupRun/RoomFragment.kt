package com.A306.runnershi.Fragment.GroupRun

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Model.User
import com.A306.runnershi.Openvidu.OpenviduUtil.CustomHttpClient
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_room.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RoomFragment(private val room: Room) : Fragment(R.layout.fragment_room), EasyPermissions.PermissionCallbacks {

    var mainActivity:MainActivity? = null
    var httpClient:CustomHttpClient? = null

    // 임시로 넘겨줄 UserList:
    var tempUserList: ArrayList<User> = arrayListOf(
        User(null, null, "티캔", null),
        User(null, null, "디니", null),
        User(null, null, "바비", null),
        User(null, null, "에리얼", null),
        User(null, null, "클로이", null)
    )

    private lateinit var mateListAdapter: MateListAdapter


    private var curTimeMillis = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("입장하였습니다", "${room.title}")

        val mainActivity = activity as MainActivity
        mainActivity.sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity

        // 방 이름 설정해주기
        roomTitle.text = room.title

        requestPermissions()
        Timber.e(room.title)
        subscribeToObservers()

        if(mainActivity != null){
            if (mainActivity!!.arePermissionGranted()){
                httpClient = CustomHttpClient("https://k4a3061.p.ssafy.io/",
                    "Basic " + Base64.encodeToString(
                        "OPENVIDUAPP:MY_SECRET".toByteArray(), android.util.Base64.DEFAULT).trim()
                    )
            }
        }

        // 함께 뛰는 메이트들 불러오기
        var list: ArrayList<User> = tempUserList

        mateListAdapter = MateListAdapter(list)
        mateListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mateListView.adapter = mateListAdapter
    }

    private fun subscribeToObservers(){
        TrackingService.totalDistance.observe(viewLifecycleOwner, Observer {
            distanceText.text = String.format("%.2f", it / 1000f)
        })

        TrackingService.totalPace.observe(viewLifecycleOwner, Observer {
            if (it > 0) {
                val formattedPace = TrackingUtility.getPaceWithMilliAndDistance(it)
                paceText.text = formattedPace
            } else {
                paceText.text = "0' 00''"
            }
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeMillis, false)
            timeText.text = formattedTime
        })
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
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