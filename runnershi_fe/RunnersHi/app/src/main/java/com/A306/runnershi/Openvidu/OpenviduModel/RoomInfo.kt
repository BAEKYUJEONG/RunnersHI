package com.A306.runnershi.Openvidu.OpenviduModel

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Openvidu.OpenviduObserver.CustomPeerConnectionObserver
import com.A306.runnershi.Openvidu.OpenviduObserver.CustomSdpObserver
import com.A306.runnershi.Openvidu.OpenviduWebSocket.CustomWebSocket
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.PeerConnectionFactory.InitializationOptions
import timber.log.Timber
import java.util.*

class RoomInfo {
    var localParticipant: LocalParticipant? = null
    var remoteParticipants: Map<String, RemoteParticipant> =
        HashMap<String, RemoteParticipant>()
    var id: String? = null
    var token: String? = null
    var views_container: LinearLayout? = null
    var peerConnectionFactory: PeerConnectionFactory? = null
    var websocket: CustomWebSocket? = null
    var activity: MainActivity? = null

    constructor(id: String?, token: String?, views_container: LinearLayout?, activity: MainActivity?) {
        this.id = id
        this.token = token
        this.views_container = views_container
        this.activity = activity

        val optionsBuilder = InitializationOptions.builder(activity!!.applicationContext)
        optionsBuilder.setEnableInternalTracer(true)
        val opt = optionsBuilder.createInitializationOptions()
        PeerConnectionFactory.initialize(opt)
        val options = PeerConnectionFactory.Options()

        val encoderFactory: VideoEncoderFactory
        val decoderFactory: VideoDecoderFactory
        encoderFactory = SoftwareVideoEncoderFactory()
        decoderFactory = SoftwareVideoDecoderFactory()

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .setOptions(options)
                .createPeerConnectionFactory()
    }

    fun setWebSocket(websocket: CustomWebSocket?) {
        this.websocket = websocket
    }

    fun createLocalPeerConnection(): PeerConnection? {
        val iceServers: MutableList<IceServer> = ArrayList()
        val iceServer = IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        iceServers.add(iceServer)
        val rtcConfig = RTCConfiguration(iceServers)
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        rtcConfig.enableDtlsSrtp = true
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return peerConnectionFactory?.createPeerConnection(
            rtcConfig,
            object : CustomPeerConnectionObserver(
                "local"
            ) {
                override fun onIceCandidate(iceCandidate: IceCandidate?) {
                    super.onIceCandidate(iceCandidate)
                    if (iceCandidate != null) {
                        websocket?.onIceCandidate(iceCandidate, localParticipant?.connectionId)
                    }
                }
            })
    }

    fun createRemotePeerConnection(connectionId: String?) {
        val iceServers: MutableList<IceServer> = ArrayList()
        val iceServer = IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        iceServers.add(iceServer)
        val rtcConfig = RTCConfiguration(iceServers)
        rtcConfig.tcpCandidatePolicy = TcpCandidatePolicy.ENABLED
        rtcConfig.bundlePolicy = BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = RtcpMuxPolicy.NEGOTIATE
        rtcConfig.continualGatheringPolicy = ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = KeyType.ECDSA
        rtcConfig.enableDtlsSrtp = true
        rtcConfig.sdpSemantics = SdpSemantics.UNIFIED_PLAN
        val peerConnection = peerConnectionFactory!!.createPeerConnection(
            rtcConfig,
            object : CustomPeerConnectionObserver("remotePeerCreation") {
                override fun onIceCandidate(iceCandidate: IceCandidate?) {
                    super.onIceCandidate(iceCandidate)
                    if (iceCandidate != null) {
                        websocket?.onIceCandidate(iceCandidate, connectionId)
                    }
                }

                override fun onAddTrack(
                    rtpReceiver: RtpReceiver?,
                    mediaStreams: Array<out MediaStream>?
                ) {
                    super.onAddTrack(rtpReceiver, mediaStreams)
                    mediaStreams?.get(0)?.let {
                        activity?.setRemoteMediaStream(
                            it
                        )
                    }
                }

                override fun onSignalingChange(signalingState: SignalingState?) {
                    if (SignalingState.STABLE == signalingState) {
                        val remoteParticipant: RemoteParticipant? =
                            remoteParticipants.get(connectionId)
                        val it: MutableIterator<IceCandidate> =
                            remoteParticipant?.iceCandidateList?.iterator() as MutableIterator<IceCandidate>
                        while (it.hasNext()) {
                            val candidate = it.next()
                            if (remoteParticipant != null) {
                                remoteParticipant.peerConnection?.addIceCandidate(candidate)
                            }
                            it.remove()
                        }
                    }
                }
            })
        peerConnection!!.addTrack(localParticipant?.audioTrack) //Add audio track to create transReceiver
        peerConnection.addTrack(localParticipant?.videoTrack) //Add video track to create transReceiver
        for (transceiver in peerConnection.transceivers) {
            //We set both audio and video in receive only mode
            transceiver.direction = RtpTransceiver.RtpTransceiverDirection.RECV_ONLY
        }
        remoteParticipants[connectionId]?.peerConnection = peerConnection
    }

    fun createLocalOffer(constraints: MediaConstraints?) {
        localParticipant?.peerConnection
            ?.createOffer(object : CustomSdpObserver("local offer sdp") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    super.onCreateSuccess(sessionDescription)
                    Timber.e("createOffer SUCCESS ${sessionDescription}")
                    localParticipant?.peerConnection?.setLocalDescription(
                        CustomSdpObserver("local set local"),
                        sessionDescription
                    )
                    websocket?.publishVideo(sessionDescription)
                }

                override fun onCreateFailure(s: String) {
                    Log.e("createOffer ERROR", s!!)
                }
            }, constraints)
    }

    fun getRemoteParticipant(id: String?): RemoteParticipant? {
        return remoteParticipants[id]
    }

    fun removeView(view: View?) {
        views_container!!.removeView(view)
    }

    fun removeRemoteParticipant(id: String?): RemoteParticipant? {
        val thisParticipant = remoteParticipants[id]
        remoteParticipants.minus(id)
        return thisParticipant
    }

    fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        remoteParticipants.plus(mapOf(remoteParticipant.connectionId to remoteParticipant))
    }

    fun leaveSession() {
        AsyncTask.execute {
            websocket?.setWebsocketCancelled(true)
            if (websocket != null) {
                websocket!!.leaveRoom()
                websocket!!.disconnect()
            }
            localParticipant!!.dispose()
        }
        activity!!.runOnUiThread {
            for (remoteParticipant in remoteParticipants.values) {
                if (remoteParticipant.peerConnection != null) {
                    remoteParticipant.peerConnection!!.close()
                }
                views_container!!.removeView(remoteParticipant.view)
            }
        }
        AsyncTask.execute {
            if (peerConnectionFactory != null) {
                peerConnectionFactory!!.dispose()
                peerConnectionFactory = null
            }
        }
    }
}