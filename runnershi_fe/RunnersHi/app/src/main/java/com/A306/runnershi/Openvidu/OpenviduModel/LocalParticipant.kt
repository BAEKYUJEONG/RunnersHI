package com.A306.runnershi.Openvidu.OpenviduModel

import android.content.Context
import android.os.Build
import org.webrtc.*
import java.util.*

class LocalParticipant : Participant {
    var context: Context? = null
    var localVideoView: SurfaceViewRenderer? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    var videoCapturer: VideoCapturer? = null

    var localIceCandidates: Collection<IceCandidate>? = null
    var localSessionDescription: SessionDescription? = null

    constructor(
        participantName: String?,
        roomInfo: RoomInfo,
        context: Context?,
        localVideoView: SurfaceViewRenderer?
    ) : super(participantName, roomInfo) {
        this.localVideoView = localVideoView
        this.context = context
        localIceCandidates = ArrayList()
        roomInfo.localParticipant = this
    }

    fun startCamera() {
        val eglBaseContext = EglBase.create().eglBaseContext
        val peerConnectionFactory: PeerConnectionFactory? = this.roomInfo?.peerConnectionFactory

        // create AudioSource
        val audioSource = peerConnectionFactory?.createAudioSource(MediaConstraints())
        audioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        // create VideoCapturer
        val videoCapturer: VideoCapturer? = createCameraCapturer()
        val videoSource = videoCapturer?.let { peerConnectionFactory?.createVideoSource(it.isScreencast) }
        if (videoCapturer != null) {
            if (videoSource != null) {
                videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
                videoCapturer.startCapture(480, 640, 30)
            }
        }

        // create VideoTrack
        videoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)
        // display in localView
        videoTrack?.addSink(localVideoView)
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator: CameraEnumerator = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Camera2Enumerator(context)
        } else {
            Camera1Enumerator(false)
        }
        val deviceNames = enumerator.deviceNames

        // Try to find front facing camera
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        // Front facing camera not found, try something else
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    fun storeIceCandidate(iceCandidate: IceCandidate?) {
        localIceCandidates?.plus(iceCandidate)
    }

    fun storeLocalSessionDescription(sessionDescription: SessionDescription) {
        localSessionDescription = sessionDescription
    }

    override fun dispose() {
        super.dispose()
        if (videoTrack != null) {
            videoTrack!!.removeSink(localVideoView)
            videoCapturer!!.dispose()
            videoCapturer = null
        }
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper!!.dispose()
            surfaceTextureHelper = null
        }
    }

}