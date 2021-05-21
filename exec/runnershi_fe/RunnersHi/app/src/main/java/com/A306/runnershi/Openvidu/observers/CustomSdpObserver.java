package com.A306.runnershi.Openvidu.observers;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import timber.log.Timber;

public class CustomSdpObserver implements SdpObserver {

    private String tag;

    public CustomSdpObserver(String tag) {
        this.tag = "SdpObserver-" + tag;
    }

    private void log(String s) {
        Timber.d(s);
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        log("onCreateSuccess " + sessionDescription);
    }

    @Override
    public void onSetSuccess() {
        log("onSetSuccess ");
    }

    @Override
    public void onCreateFailure(String s) {
        log("onCreateFailure " + s);
    }

    @Override
    public void onSetFailure(String s) {
        log("onSetFailure " + s);
    }
}
