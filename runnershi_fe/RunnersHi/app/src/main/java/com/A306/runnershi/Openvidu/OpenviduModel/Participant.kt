package com.A306.runnershi.Openvidu.OpenviduModel

import org.webrtc.*
import timber.log.Timber
import java.util.*

abstract class Participant {
    var connectionId: String? = null
    var participantName: String? = null
    var roomInfo: RoomInfo? = null
    var iceCandidateList: List<IceCandidate> = ArrayList<IceCandidate>()
    var peerConnection: PeerConnection? = null
    var audioTrack: AudioTrack? = null
    var videoTrack: VideoTrack? = null
    var mediaStream: MediaStream? = null

    constructor(participantName: String?, roomInfo: RoomInfo) {
        this.participantName = participantName
        this.roomInfo = roomInfo
    }

    constructor(connectionId: String?, participantName: String?, roomInfo: RoomInfo) {
        this.connectionId = connectionId
        this.participantName = participantName
        this.roomInfo = roomInfo
    }

    open fun dispose() {
        if (peerConnection != null) {
            try {
                peerConnection!!.close()
            } catch (e: IllegalStateException) {
               Timber.e("Dispose PeerConnection ${e.message!!}")
            }
        }
    }
}