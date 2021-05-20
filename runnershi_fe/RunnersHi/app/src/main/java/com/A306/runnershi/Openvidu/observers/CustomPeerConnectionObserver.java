package com.A306.runnershi.Openvidu.observers;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

import java.util.Arrays;

import timber.log.Timber;

public class CustomPeerConnectionObserver implements PeerConnection.Observer {

    private String TAG = "PeerConnection";

    public CustomPeerConnectionObserver(String id) {
        this.TAG = this.TAG + "-" + id;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Timber.d("onSignalingChange() called with: signalingState = [" + signalingState + "]");
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Timber.d("onIceConnectionChange() called with: iceConnectionState = [" + iceConnectionState + "]");
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Timber.d("onIceConnectionReceivingChange() called with: b = [" + b + "]");
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Timber.d("onIceGatheringChange() called with: iceGatheringState = [" + iceGatheringState + "]");
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Timber.d("onIceCandidate() called with: iceCandidate = [" + iceCandidate + "]");
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Timber.d("onIceCandidatesRemoved() called with: iceCandidates = [" + Arrays.toString(iceCandidates) + "]");
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Timber.d("onAddStream() called with: mediaStream = [" + mediaStream + "]");
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Timber.d("onRemoveStream() called with: mediaStream = [" + mediaStream + "]");
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Timber.d("onDataChannel() called with: dataChannel = [" + dataChannel + "]");
    }

    @Override
    public void onRenegotiationNeeded() {
        Timber.d("onRenegotiationNeeded() called");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Timber.d("onAddTrack() called with: mediaStreams = [" + Arrays.toString(mediaStreams) + "]");
    }
}
