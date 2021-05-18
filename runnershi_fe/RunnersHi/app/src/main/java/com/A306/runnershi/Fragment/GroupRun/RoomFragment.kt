package com.A306.runnershi.Fragment.GroupRun

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Model.User
import com.A306.runnershi.Openvidu.OpenviduModel.LocalParticipant
import com.A306.runnershi.Openvidu.OpenviduModel.RemoteParticipant
import com.A306.runnershi.Openvidu.OpenviduModel.RoomInfo
import com.A306.runnershi.Openvidu.OpenviduUtil.CustomHttpClient
import com.A306.runnershi.Openvidu.OpenviduWebSocket.CustomWebSocket
import com.A306.runnershi.Openvidu.Permission.PermissionsDialogFragment
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_room.*
import kotlinx.android.synthetic.main.grouprun_mate.*
import org.webrtc.AudioTrack
import org.webrtc.MediaStream
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RoomFragment(private val room: Room?) : Fragment(R.layout.fragment_room), EasyPermissions.PermissionCallbacks {
    val userViewModel:UserViewModel by viewModels()


    var mainActivity:MainActivity? = null
    var httpClient:CustomHttpClient? = null
    lateinit var roomInfo:RoomInfo
    var currentUser:User? = null


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

        val mainActivity = activity as MainActivity

        mainActivity.sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity

        // 방 이름 설정해주기
        roomTitle.text = room?.title

        requestPermissions()
        Timber.e(room?.title)
        subscribeToObservers()

        if(mainActivity != null){
            Timber.e("MAIN NULL 아님")
            if (mainActivity!!.arePermissionGranted()){
                Timber.e("HTTP CLIENT 실행")
                httpClient = CustomHttpClient(
                    "https://k4a3061.p.ssafy.io/",
                    "Basic " + Base64.encodeToString(
                        "OPENVIDUAPP:MY_SECRET".toByteArray(), android.util.Base64.DEFAULT
                    ).trim()
                )

                getTokenSuccess(mainActivity!!.roomToken, mainActivity!!.roomSession)
            }else{
                val permissionsFragment: DialogFragment = PermissionsDialogFragment()
                permissionsFragment.show(mainActivity!!.supportFragmentManager, "Permissions Fragment")
            }
        }

        // 함께 뛰는 메이트들 불러오기
        var list: ArrayList<User> = tempUserList

        mateListAdapter = MateListAdapter(list)
        mateListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mateListView.adapter = mateListAdapter
    }

    private fun getTokenSuccess(token:String, sessionId:String){
        roomInfo = RoomInfo(sessionId, token, groupRunLayout, mainActivity)
        Timber.e("ROOMINFO : ${roomInfo}")
        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            currentUser = it
            var localParticipant = LocalParticipant(currentUser?.userName, roomInfo, requireContext())
            localParticipant.startAudio()

            startWebSocket()
        })

    }

    private fun startWebSocket(){
        var webSocket = CustomWebSocket(roomInfo, "https://k4a3061.p.ssafy.io", mainActivity)
        webSocket.execute()
        roomInfo.setWebSocket(webSocket)
    }

    fun setRemoteMediaStream(stream:MediaStream){
        var audioTrack:AudioTrack = stream.audioTracks.get(0)
        audioTrack.setVolume(100.0)
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
        if(TrackingUtility.hasLocationPermissions(requireContext()) && TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
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

    fun leaveSession(){
        this.roomInfo.leaveSession()
        this.httpClient?.dispose()
    }

}