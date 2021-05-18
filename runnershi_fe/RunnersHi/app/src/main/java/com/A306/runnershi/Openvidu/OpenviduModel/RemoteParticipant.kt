package com.A306.runnershi.Openvidu.OpenviduModel

import android.view.View
import android.widget.TextView
import org.webrtc.SurfaceViewRenderer

class RemoteParticipant : Participant{

    val view: View? = null
    private val videoView: SurfaceViewRenderer? = null
    private val participantNameText: TextView? = null

    constructor(connectionId: String?, participantName: String?, roomInfo: RoomInfo) :super(
        connectionId,
        participantName,
        roomInfo
    ){
        this.roomInfo?.addRemoteParticipant(this)
    }

    fun getVideoView(): SurfaceViewRenderer? {
        return videoView
    }

    override fun dispose() {
        super.dispose()
    }
}