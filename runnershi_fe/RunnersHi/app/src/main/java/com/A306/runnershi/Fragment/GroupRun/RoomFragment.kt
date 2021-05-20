package com.A306.runnershi.Fragment.GroupRun

import android.Manifest
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Model.Room
import com.A306.runnershi.Model.User
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
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.fragment_room.*
import kotlinx.android.synthetic.main.grouprun_mate.*
import org.webrtc.EglBase
import org.webrtc.MediaStream
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RoomFragment(private val room: Room) : Fragment(R.layout.fragment_room), EasyPermissions.PermissionCallbacks {
    val userViewModel:UserViewModel by viewModels()

    val HTML_STRING = "<html>\n" +
            "\n" +
            "<head>\n" +
            "\t<title>openvidu-insecure-js</title>\n" +
            "\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" charset=\"utf-8\">\n" +
            "\n" +
            "\t<!-- Bootstrap -->\n" +
            "\t<script src=\"https://code.jquery.com/jquery-3.3.1.min.js\" integrity=\"sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=\" crossorigin=\"anonymous\"></script>\n" +
            "\t<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
            "\t<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>\n" +
            "\t<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
            "\t<!-- Bootstrap -->\n" +
            "\n" +
            "\t<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" media=\"screen\">\n" +
            "\t<script src=\"openvidu-browser-2.17.0.js\"></script>\n" +
            "\t<script src=\"app.js\"></script>\n" +
            "\t<script src=\"jquery.js\"></script>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "\n" +
            "\t<div id=\"main-container\" class=\"container\">\n" +
            "\t\t<div id=\"join\">\n" +
            "\t\t\t<div id=\"join-dialog\" class=\"jumbotron vertical-center\">\n" +
            "\t\t\t\t<form class=\"form-group\" onsubmit=\"joinSession(); return false\">\n" +
            "\t\t\t\t\t<p>\n" +
            "\t\t\t\t\t\t<input class=\"form-control\" type=\"text\" id=\"userName\" required>\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t\t<p>\n" +
            "\t\t\t\t\t\t<input class=\"form-control\" type=\"text\" id=\"sessionId\" required>\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t\t<p class=\"text-center\">\n" +
            "\t\t\t\t\t\t<input class=\"btn btn-lg btn-success\" type=\"submit\" name=\"commit\" value=\"Join!\">\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t</form>\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\n" +
            "\t\t<div id=\"session\" style=\"display: none;\">\n" +
            "\t\t\t<div id=\"session-header\">\n" +
            "\t\t\t\t<h1 id=\"session-title\"></h1>\n" +
            "\t\t\t\t<input class=\"btn btn-large btn-danger\" type=\"button\" id=\"buttonLeaveSession\" onmouseup=\"leaveSession()\" value=\"방 나가기\">\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<div id=\"main-video\" class=\"col-md-6\"><p></p><video autoplay playsinline=\"true\"></video></div>\n" +
            "\t\t\t<div id=\"video-container\" class=\"col-md-6\"></div>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "\n" +
            "</body>\n" +
            "\n" +
            "</html>"

    var mainActivity:MainActivity? = null
    var httpClient:CustomHttpClient? = null
    var currentUser: User? = null
    lateinit var session: Session

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

        val sessionWebView:AdvancedWebView = view.findViewById(R.id.sessionWebView)
        sessionWebView.settings.useWideViewPort = true
        sessionWebView.settings.javaScriptEnabled = true
        sessionWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        sessionWebView.settings.allowContentAccess = true
        sessionWebView.settings.domStorageEnabled = true
        sessionWebView.settings.mediaPlaybackRequiresUserGesture = false
        // 캐쉬 사용 방법을 정의
        sessionWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;
        sessionWebView.webViewClient = object: WebViewClient(){
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // DO NOT CALL SUPER METHOD
//                super.onReceivedSslError(view, handler, error)
            }
        }
        sessionWebView.webChromeClient = object : WebChromeClient() {
            // Grant permissions for cam
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        sessionWebView.setGeolocationEnabled(true)
        sessionWebView.setMixedContentAllowed(false)


        sessionWebView.loadDataWithBaseURL("file:///android_asset/www", HTML_STRING, "text/html", "utf-8", null)


        // 방 이름 설정해주기
        roomTitle.text = room?.title

        requestPermissions()
        Timber.e(room?.title)
        subscribeToObservers()

        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            currentUser = it
//            if (mainActivity != null) {
//                Timber.e("MAIN NULL 아님")
//                if (mainActivity!!.arePermissionGranted()) {
//                    initViews()
//                    Timber.e("HTTP CLIENT 실행")
//                    httpClient = CustomHttpClient(
//                            OPENVIDU_URL,
//                            "Basic " + Base64.encodeToString(
//                                    "OPENVIDUAPP:${OPENVIDU_SECRET}".toByteArray(), Base64.DEFAULT
//                            ).trim()
//                    )
//                    Timber.e("ROOM SESSION : ${room.roomSession}, ROOM TOKEN : ${room.roomToken}")
//                    getTokenSuccess(room.roomToken, room.roomSession)
//                } else {
//                    val permissionsFragment: DialogFragment = PermissionsDialogFragment()
//                    permissionsFragment.show(
//                            mainActivity!!.supportFragmentManager,
//                            "Permissions Fragment"
//                    )
//                }
//            }
        })
        // 함께 뛰는 메이트들 불러오기
//        var list: ArrayList<User> = tempUserList
//
//        mateListAdapter = MateListAdapter(participantList)
//        mateListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
//        mateListView.adapter = mateListAdapter
    }

//    private fun getTokenSuccess(token: String, sessionId: String){
//        // Initialize our session
//        if (room.room_id > 0){
//            session = Session(room.room_id, room.title, sessionId, token, mateListView, mainActivity, this)
//            val localParticipant = LocalParticipant(currentUser?.userName, session, requireContext(), localVideo)
//            localName.text = localParticipant.participantName
//            localParticipant.startCamera()
//            // Initialize and connect the websocket to OpenVidu Server
//            startWebSocket()
//        } else {
//            mainActivity?.makeCurrentFragment(GroupRunRoomListFragment())
//        }
//    }
//
//    private fun initViews() {
//        val rootEglBase = EglBase.create()
//        localVideo.init(rootEglBase.eglBaseContext, null)
//        localVideo.setMirror(true)
//        localVideo.setEnableHardwareScaler(true)
//        localVideo.setZOrderMediaOverlay(true)
//    }

    private fun startWebSocket(){
        val webSocket = CustomWebSocket(session, OPENVIDU_URL, mainActivity, this)
        webSocket.execute()
        session.setWebSocket(webSocket)
    }
//
//    fun createRemoteParticipantVideo(remoteParticipant: RemoteParticipant) {
//        val mainHandler = Handler(mainActivity!!.mainLooper)
//        val myRunnable = Runnable {
//            val rowView: View = this.layoutInflater.inflate(R.layout.grouprun_mate, null)
//            val rowId = View.generateViewId()
//            rowView.id = rowId
//            mateListView.addView(rowView)
//            remoteParticipant.videoView = mateVideo
//
//            val rootEglBase = EglBase.create()
//            remoteParticipant.videoView.init(rootEglBase.eglBaseContext, null)
//            remoteParticipant.videoView.setMirror(false)
//            remoteParticipant.videoView.setEnableHardwareScaler(true)
//            remoteParticipant.videoView.setZOrderMediaOverlay(true)
//            remoteParticipant.participantNameText = mateName
//            remoteParticipant.view = rowView
//            remoteParticipant.participantNameText.text = remoteParticipant.participantName
//        }
//        mainHandler.post(myRunnable)
//    }

    fun setRemoteMediaStream(stream: MediaStream, remoteParticipant: RemoteParticipant) {
        val videoTrack = stream.videoTracks[0]
        videoTrack.addSink(remoteParticipant.videoView)
//        Runnable { remoteParticipant.videoView.visibility = View.VISIBLE }
        remoteParticipant.videoView.visibility = View.VISIBLE
    }

    fun leaveSession() {
        session.leaveSession()
        httpClient!!.dispose()
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

    override fun onDestroy() {
        leaveSession()
        super.onDestroy()
    }

    override fun onStop() {
        leaveSession()
        super.onStop()
    }

}