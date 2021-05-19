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
import com.A306.runnershi.Openvidu.OpenviduSessionRetrofitClient
import com.A306.runnershi.Openvidu.constant.JsonConstants.OPENVIDU_SECRET
import com.A306.runnershi.Openvidu.constant.JsonConstants.OPENVIDU_URL
import com.A306.runnershi.Openvidu.fragment.PermissionsDialogFragment
import com.A306.runnershi.Openvidu.model.LocalParticipant
import com.A306.runnershi.Openvidu.model.Participant
import com.A306.runnershi.Openvidu.model.RemoteParticipant
import com.A306.runnershi.Openvidu.model.Session
import com.A306.runnershi.Openvidu.utils.CustomHttpClient
import com.A306.runnershi.Openvidu.websocket.CustomWebSocket
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_group_run_room_list.*
import kotlinx.android.synthetic.main.fragment_room.*
import kotlinx.android.synthetic.main.grouprun_mate.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class RoomFragment(private val room: Room) : Fragment(R.layout.fragment_room), EasyPermissions.PermissionCallbacks {
    val userViewModel:UserViewModel by viewModels()

    var mainActivity:MainActivity? = null
    var httpClient:CustomHttpClient? = null
    var currentUser: User? = null
    lateinit var session: Session
    var participantList = ArrayList<Participant>()
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

        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            currentUser = it
            if (mainActivity != null) {
                Timber.e("MAIN NULL 아님")
                if (mainActivity!!.arePermissionGranted()) {
                    Timber.e("HTTP CLIENT 실행")
                    httpClient = CustomHttpClient(
                        OPENVIDU_URL,
                        "Basic " + Base64.encodeToString(
                            "OPENVIDUAPP:${OPENVIDU_SECRET}".toByteArray(), Base64.DEFAULT
                        ).trim()
                    )
                    Timber.e("ROOM SESSION : ${room.roomSession}, ROOM TOKEN : ${room.roomToken}")
                    getTokenSuccess(room.roomToken, room.roomSession)
                } else {
                    val permissionsFragment: DialogFragment = PermissionsDialogFragment()
                    permissionsFragment.show(
                        mainActivity!!.supportFragmentManager,
                        "Permissions Fragment"
                    )
                }
            }
        })
        // 함께 뛰는 메이트들 불러오기
//        var list: ArrayList<User> = tempUserList
//
        mateListAdapter = MateListAdapter(participantList)
        mateListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mateListView.adapter = mateListAdapter
    }

    private fun getTokenSuccess(token: String, sessionId: String){
        // Initialize our session
        if (room.room_id > 0){
            session = Session(room.room_id, room.title, sessionId, token, grouprun_item, mainActivity)
            val localParticipant = LocalParticipant(currentUser?.userName, session, requireContext())
            localParticipant.startAudio()
            // Initialize and connect the websocket to OpenVidu Server
            startWebSocket()
        } else {
            mainActivity?.makeCurrentFragment(GroupRunRoomListFragment())
        }
    }

    private fun startWebSocket(){
        val webSocket = CustomWebSocket(session, OPENVIDU_URL, mainActivity, this)
        webSocket.execute()
        session.setWebSocket(webSocket)
    }

    fun createRemoteParticipantVideo(remoteParticipant: RemoteParticipant){

    }

    fun leaveSession() {
        session.leaveSession()
        httpClient!!.dispose()
    }

    fun addParticipant(participant: Participant){
        participantList.add(participant)
    }

    fun removeParticipant(participant: Participant){
        participantList.remove(participant)

    }

    fun updateParticipantsList(){
        mateListAdapter.notifyDataSetChanged()
    }

    fun setParticipantsList(receivedInfo:String){
        Timber.e("함수 소환!!")
        Timber.e(receivedInfo)
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
        if(TrackingUtility.hasLocationPermissions(requireContext()) && TrackingUtility.hasLocationPermissions(
                requireContext()
            )){
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

}