package com.A306.runnershi.Openvidu.OpenviduWebSocket

import android.app.Activity
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.A306.runnershi.Openvidu.OpenviduConstant.JsonConstants
import com.A306.runnershi.Openvidu.OpenviduModel.LocalParticipant
import com.A306.runnershi.Openvidu.OpenviduModel.Participant
import com.A306.runnershi.Openvidu.OpenviduModel.RemoteParticipant
import com.A306.runnershi.Openvidu.OpenviduModel.RoomInfo
import com.A306.runnershi.Openvidu.OpenviduObserver.CustomSdpObserver
import com.neovisionaries.ws.client.*
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.SignalingState
import timber.log.Timber
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CustomWebSocket : WebSocketListener, AsyncTask<Activity, Void, Void> {
    private val TAG = "CustomWebSocketListener"
    private val PING_MESSAGE_INTERVAL = 5
    private val trustManagers = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            Timber.e(": authType: $authType")
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            Timber.e(": authType: $authType")
        }
    })

    private val RPC_ID = AtomicInteger(0)
    private val ID_PING = AtomicInteger(-1)
    private val ID_JOINROOM = AtomicInteger(-1)
    private val ID_LEAVEROOM = AtomicInteger(-1)
    private val ID_PUBLISHVIDEO = AtomicInteger(-1)
    private val IDS_RECEIVEVIDEO: Map<Int, String> = ConcurrentHashMap()
    private val IDS_ONICECANDIDATE = Collections.newSetFromMap(ConcurrentHashMap<Int, Boolean>())
    private var roomInfo: RoomInfo? = null
    private var openviduUrl: String? = null
    private var activity: Activity? = null
    private var websocket: WebSocket? = null
    private var websocketCancelled = false

    constructor(roomInfo: RoomInfo, openviduUrl: String?, activity: Activity?) {
        this.roomInfo = roomInfo
        this.openviduUrl = openviduUrl
        this.activity = activity
    }

    override fun onTextMessage(websocket: WebSocket?, text: String?) {
        val json = JSONObject(text)
        if (json.has(JsonConstants.RESULT)) {
            handleServerResponse(json)
        } else {
            handleServerEvent(json)
        }
    }

    @Throws(JSONException::class)
    private fun handleServerResponse(json: JSONObject) {
        val rpcId = json.getInt(JsonConstants.ID)
        val result = JSONObject(json.getString(JsonConstants.RESULT))
        if (result.has("value") && result.getString("value") == "pong") {
            // Response to ping
            Log.i(TAG, "pong")
        } else if (rpcId == ID_JOINROOM.get()) {
            // Response to joinRoom
//            activity.viewToConnectedState()
            val localParticipant: LocalParticipant? = this.roomInfo?.localParticipant
            val localConnectionId = result.getString(JsonConstants.ID)
            if (localParticipant != null) {
                localParticipant.connectionId = localConnectionId
            }
            val localPeerConnection: PeerConnection = roomInfo?.createLocalPeerConnection()!!
            if (localParticipant != null) {
                localPeerConnection.addTrack(localParticipant.audioTrack)
                localPeerConnection.addTrack(localParticipant.videoTrack)
                localParticipant.peerConnection = localPeerConnection
            }
            for (transceiver in localPeerConnection.transceivers) {
                transceiver.direction = RtpTransceiver.RtpTransceiverDirection.SEND_ONLY
            }
            val sdpConstraints = MediaConstraints()
            sdpConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "offerToReceiveAudio",
                    "true"
                )
            )
            sdpConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "offerToReceiveVideo",
                    "true"
                )
            )
            roomInfo!!.createLocalOffer(sdpConstraints)
            if (result.getJSONArray(JsonConstants.VALUE).length() > 0) {
                // There were users already connected to the session
                addRemoteParticipantsAlreadyInRoom(result)
            }
        } else if (rpcId == ID_LEAVEROOM.get()) {
            // Response to leaveRoom
            if (websocket!!.isOpen) {
                websocket!!.disconnect()
            }
        } else if (rpcId == ID_PUBLISHVIDEO.get()) {
            // Response to publishVideo
            val sessionDescription =
                SessionDescription(SessionDescription.Type.ANSWER, result.getString("sdpAnswer"))
            this.roomInfo?.localParticipant?.peerConnection
                ?.setRemoteDescription(CustomSdpObserver("localSetRemoteDesc"), sessionDescription)
        } else if (IDS_RECEIVEVIDEO.containsKey(rpcId)) {
            // Response to receiveVideoFrom
            val sessionDescription =
                SessionDescription(SessionDescription.Type.ANSWER, result.getString("sdpAnswer"))
            IDS_RECEIVEVIDEO.minus(rpcId)
            roomInfo?.getRemoteParticipant(rpcId.toString())?.peerConnection
                ?.setRemoteDescription(CustomSdpObserver("remoteSetRemoteDesc"), sessionDescription)
        } else if (IDS_ONICECANDIDATE.contains(rpcId)) {
            // Response to onIceCandidate
            IDS_ONICECANDIDATE.remove(rpcId)
        } else {
            Timber.e("Unrecognized server response: $result")
        }
    }

    fun joinRoom() {
        val joinRoomParams: MutableMap<String, String?> = HashMap()
        joinRoomParams[JsonConstants.METADATA] =
            "{\"clientData\": \"" + this.roomInfo?.localParticipant?.participantName
                .toString() + "\"}"
        joinRoomParams["secret"] = ""
        joinRoomParams["session"] = this.roomInfo?.id
        joinRoomParams["platform"] = "Android " + Build.VERSION.SDK_INT
        joinRoomParams["token"] = this.roomInfo?.token
        ID_JOINROOM.set(this.sendJson(JsonConstants.JOINROOM_METHOD, joinRoomParams))
    }

    fun leaveRoom() {
        ID_LEAVEROOM.set(this.sendJson(JsonConstants.LEAVEROOM_METHOD))
    }

    fun publishVideo(sessionDescription: SessionDescription) {
        val publishVideoParams: MutableMap<String, String?> = HashMap()
        publishVideoParams["audioActive"] = "true"
        publishVideoParams["videoActive"] = "true"
        publishVideoParams["doLoopback"] = "false"
        publishVideoParams["frameRate"] = "30"
        publishVideoParams["hasAudio"] = "true"
        publishVideoParams["hasVideo"] = "true"
        publishVideoParams["typeOfVideo"] = "CAMERA"
        publishVideoParams["videoDimensions"] = "{\"width\":320, \"height\":240}"
        publishVideoParams["sdpOffer"] = sessionDescription.description
        ID_PUBLISHVIDEO.set(this.sendJson(JsonConstants.PUBLISHVIDEO_METHOD, publishVideoParams))
    }

    fun receiveVideoFrom(
        sessionDescription: SessionDescription,
        remoteParticipant: RemoteParticipant,
        streamId: String
    ) {
        val receiveVideoFromParams: MutableMap<String, String?> = HashMap()
        receiveVideoFromParams["sdpOffer"] = sessionDescription.description
        receiveVideoFromParams["sender"] = streamId
        IDS_RECEIVEVIDEO.plus(
            mapOf(
                this.sendJson(
                    JsonConstants.RECEIVEVIDEO_METHOD,
                    receiveVideoFromParams
                ) to remoteParticipant.connectionId
            )
        )
    }

    fun onIceCandidate(iceCandidate: IceCandidate, endpointName: String?) {
        val onIceCandidateParams: MutableMap<String, String?> = HashMap()
        if (endpointName != null) {
            onIceCandidateParams["endpointName"] = endpointName
        }
        onIceCandidateParams["candidate"] = iceCandidate.sdp
        onIceCandidateParams["sdpMid"] = iceCandidate.sdpMid
        onIceCandidateParams["sdpMLineIndex"] = Integer.toString(iceCandidate.sdpMLineIndex)
        IDS_ONICECANDIDATE.add(
            this.sendJson(
                JsonConstants.ONICECANDIDATE_METHOD,
                onIceCandidateParams
            )
        )
    }

    @Throws(JSONException::class)
    private fun handleServerEvent(json: JSONObject) {
        if (!json.has(JsonConstants.PARAMS)) {
            Log.e(TAG, "No params $json")
        } else {
            val params = JSONObject(json.getString(JsonConstants.PARAMS))
            val method = json.getString(JsonConstants.METHOD)
            when (method) {
                JsonConstants.ICE_CANDIDATE -> iceCandidateEvent(params)
                JsonConstants.PARTICIPANT_JOINED -> participantJoinedEvent(params)
                JsonConstants.PARTICIPANT_PUBLISHED -> participantPublishedEvent(params)
                JsonConstants.PARTICIPANT_LEFT -> participantLeftEvent(params)
                else -> throw JSONException("Unknown method: $method")
            }
        }
    }

    fun sendJson(method: String?): Int {
        return sendJson(method, emptyMap<String, String?>() as MutableMap<String, String?>)
    }

    @Synchronized
    fun sendJson(method: String?, params: MutableMap<String, String?>): Int {
        val id = RPC_ID.get()
        val jsonObject = JSONObject()
        try {
            val paramsJson = JSONObject()
            for ((key, value) in params) {
                paramsJson.put(key, value)
            }
            jsonObject.put("jsonrpc", JsonConstants.JSON_RPCVERSION)
            jsonObject.put("method", method)
            jsonObject.put("id", id)
            jsonObject.put("params", paramsJson)
        } catch (e: JSONException) {
            Timber.e("JSONException raised on sendJson ${e}")
            return -1
        }
        websocket!!.sendText(jsonObject.toString())
        RPC_ID.incrementAndGet()
        return id
    }

    @Throws(JSONException::class)
    private fun addRemoteParticipantsAlreadyInRoom(result: JSONObject) {
        for (i in 0 until result.getJSONArray(JsonConstants.VALUE).length()) {
            val participantJson = result.getJSONArray(JsonConstants.VALUE).getJSONObject(i)
            val remoteParticipant: RemoteParticipant? = this.newRemoteParticipantAux(participantJson)
            try {
                val streams = participantJson.getJSONArray("streams")
                for (j in 0 until streams.length()) {
                    val stream = streams.getJSONObject(0)
                    val streamId = stream.getString("id")
                    if (remoteParticipant != null) {
                        this.subscribeAux(remoteParticipant, streamId)
                    }
                }
            } catch (e: Exception) {
                //Sometimes when we enter in room the other participants have no stream
                //We catch that in this way the iteration of participants doesn't stop
                Log.e(TAG, "Error in addRemoteParticipantsAlreadyInRoom: " + e.localizedMessage)
            }
        }
    }

    @Throws(JSONException::class)
    private fun iceCandidateEvent(params: JSONObject) {
        val iceCandidate = IceCandidate(
            params.getString("sdpMid"),
            params.getInt("sdpMLineIndex"),
            params.getString("candidate")
        )
        val connectionId = params.getString("senderConnectionId")
        val isRemote: Boolean =
            !roomInfo?.localParticipant?.connectionId.equals(connectionId)
        val participant: Participant =
            if (isRemote) roomInfo?.getRemoteParticipant(connectionId) as Participant else roomInfo?.localParticipant as Participant
        val pc: PeerConnection? = participant.peerConnection
        when (pc?.signalingState()) {
            SignalingState.CLOSED -> Log.e(
                "saveIceCandidate error",
                "PeerConnection object is closed"
            )
            SignalingState.STABLE -> if (pc.remoteDescription != null) {
                participant.peerConnection!!.addIceCandidate(iceCandidate)
            } else {
                participant.iceCandidateList.plus(iceCandidate)
            }
            else -> participant.iceCandidateList.plus(iceCandidate)
        }
    }

    @Throws(JSONException::class)
    private fun participantJoinedEvent(params: JSONObject) {
        this.newRemoteParticipantAux(params)
    }

    @Throws(JSONException::class)
    private fun participantPublishedEvent(params: JSONObject) {
        val remoteParticipantId = params.getString(JsonConstants.ID)
        val remoteParticipant: RemoteParticipant? =
            this.roomInfo?.getRemoteParticipant(remoteParticipantId)
        val streamId = params.getJSONArray("streams").getJSONObject(0).getString("id")
        if (remoteParticipant != null) {
            this.subscribeAux(remoteParticipant, streamId)
        }
    }

    @Throws(JSONException::class)
    private fun participantLeftEvent(params: JSONObject) {
        val remoteParticipant: RemoteParticipant? =
            this.roomInfo?.removeRemoteParticipant(params.getString("connectionId"))
        if (remoteParticipant != null) {
            remoteParticipant.dispose()
        }
        val mainHandler = Handler(activity!!.mainLooper)
        val myRunnable = Runnable {
            if (remoteParticipant != null) {
                roomInfo?.removeView(remoteParticipant.view)
            }
        }
        mainHandler.post(myRunnable)
    }

    @Throws(JSONException::class)
    private fun newRemoteParticipantAux(participantJson: JSONObject): RemoteParticipant? {
        val connectionId = participantJson.getString(JsonConstants.ID)
        var participantName: String? = ""
        if (participantJson.getString(JsonConstants.METADATA) != null) {
            val jsonStringified = participantJson.getString(JsonConstants.METADATA)
            try {
                val json = JSONObject(jsonStringified)
                val clientData = json.getString("clientData")
                if (clientData != null) {
                    participantName = clientData
                }
            } catch (e: JSONException) {
                participantName = jsonStringified
            }
        }
        val remoteParticipant =
            this.roomInfo?.let { RemoteParticipant(connectionId, participantName, it) }
        activity.createRemoteParticipantVideo(remoteParticipant)
        if (remoteParticipant != null) {
            this.roomInfo?.createRemotePeerConnection(remoteParticipant.connectionId)
        }
        return remoteParticipant
    }

    private fun subscribeAux(remoteParticipant: RemoteParticipant, streamId: String) {
        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))
        remoteParticipant.peerConnection
            ?.createOffer(object : CustomSdpObserver("remote offer sdp") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    super.onCreateSuccess(sessionDescription)
                    remoteParticipant.peerConnection!!.setLocalDescription(
                        CustomSdpObserver("remoteSetLocalDesc"),
                        sessionDescription
                    )
                    receiveVideoFrom(sessionDescription!!, remoteParticipant, streamId)
                }

                override fun onCreateFailure(s: String) {
                    Log.e("createOffer error", s!!)
                }
            }, sdpConstraints)
    }

    fun setWebsocketCancelled(websocketCancelled: Boolean) {
        this.websocketCancelled = websocketCancelled
    }

    fun disconnect() {
        websocket!!.disconnect()
    }

    private fun pingMessageHandler() {
        val initialDelay = 0L
        val executor = ScheduledThreadPoolExecutor(1)
        executor.scheduleWithFixedDelay({
            val pingParams: MutableMap<String, String?> =
                HashMap()
            if (ID_PING.get() == -1) {
                // First ping call
                pingParams["interval"] = "5000"
            }
            ID_PING.set(sendJson(JsonConstants.PING_METHOD, pingParams))
        }, initialDelay, PING_MESSAGE_INTERVAL.toLong(), TimeUnit.SECONDS)
    }

    private fun getWebSocketAddress(openviduUrl: String): String? {
        return try {
            val url = URL(openviduUrl)
            if (url.port > -1) "wss://" + url.host + ":" + url.port + "/openvidu" else "wss://" + url.host + "/openvidu"
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Wrong URL", e)
            e.printStackTrace()
            ""
        }
    }

    override fun doInBackground(vararg params: Activity?): Void? {
        try {
            val factory = WebSocketFactory()
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustManagers, SecureRandom())
            factory.sslContext = sslContext
            factory.verifyHostname = false
            websocket = factory.createSocket(openviduUrl?.let { getWebSocketAddress(it) })
            websocket!!.addListener(this)
            websocket!!.connect()
        } catch (e: KeyManagementException) {
            Log.e("WebSocket error", e.message!!)
            val mainHandler = Handler(activity!!.mainLooper)
            val myRunnable = Runnable {
                val toast = Toast.makeText(activity, e.message, Toast.LENGTH_LONG)
                toast.show()
                activity.leaveSession()
            }
            mainHandler.post(myRunnable)
            websocketCancelled = true
        } catch (e: NoSuchAlgorithmException) {
            Log.e("WebSocket error", e.message!!)
            val mainHandler = Handler(activity!!.mainLooper)
            val myRunnable = Runnable {
                val toast = Toast.makeText(activity, e.message, Toast.LENGTH_LONG)
                toast.show()
                activity.leaveSession()
            }
            mainHandler.post(myRunnable)
            websocketCancelled = true
        } catch (e: IOException) {
            Log.e("WebSocket error", e.message!!)
            val mainHandler = Handler(activity!!.mainLooper)
            val myRunnable = Runnable {
                val toast = Toast.makeText(activity, e.message, Toast.LENGTH_LONG)
                toast.show()
                activity.leaveSession()
            }
            mainHandler.post(myRunnable)
            websocketCancelled = true
        } catch (e: WebSocketException) {
            Log.e("WebSocket error", e.message!!)
            val mainHandler = Handler(activity!!.mainLooper)
            val myRunnable = Runnable {
                val toast = Toast.makeText(activity, e.message, Toast.LENGTH_LONG)
                toast.show()
                activity.leaveSession()
            }
            mainHandler.post(myRunnable)
            websocketCancelled = true
        }
        return null
    }

    override fun onProgressUpdate(vararg progress: Void?) {
        Log.i(TAG, "PROGRESS " + Arrays.toString(progress))
    }

    override fun onStateChanged(websocket: WebSocket?, newState: WebSocketState?) {
        TODO("Not yet implemented")
    }

    override fun onConnected(
        websocket: WebSocket?,
        headers: MutableMap<String, MutableList<String>>?
    ) {
        TODO("Not yet implemented")
    }

    override fun onConnectError(websocket: WebSocket?, cause: WebSocketException?) {
        TODO("Not yet implemented")
    }

    override fun onDisconnected(
        websocket: WebSocket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onContinuationFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onTextFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onBinaryFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onCloseFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onPongFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onTextMessage(websocket: WebSocket?, data: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onBinaryMessage(websocket: WebSocket?, binary: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onSendingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onFrameSent(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onFrameUnsent(websocket: WebSocket?, frame: WebSocketFrame?) {
        TODO("Not yet implemented")
    }

    override fun onThreadCreated(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {
        TODO("Not yet implemented")
    }

    override fun onThreadStarted(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {
        TODO("Not yet implemented")
    }

    override fun onThreadStopping(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {
        TODO("Not yet implemented")
    }

    override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
        TODO("Not yet implemented")
    }

    override fun onFrameError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frame: WebSocketFrame?
    ) {
        TODO("Not yet implemented")
    }

    override fun onMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frames: MutableList<WebSocketFrame>?
    ) {
        TODO("Not yet implemented")
    }

    override fun onMessageDecompressionError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        compressed: ByteArray?
    ) {
        TODO("Not yet implemented")
    }

    override fun onTextMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        data: ByteArray?
    ) {
        TODO("Not yet implemented")
    }

    override fun onSendError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frame: WebSocketFrame?
    ) {
        TODO("Not yet implemented")
    }

    override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
        TODO("Not yet implemented")
    }

    override fun handleCallbackError(websocket: WebSocket?, cause: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun onSendingHandshake(
        websocket: WebSocket?,
        requestLine: String?,
        headers: MutableList<Array<String>>?
    ) {
        TODO("Not yet implemented")
    }


}