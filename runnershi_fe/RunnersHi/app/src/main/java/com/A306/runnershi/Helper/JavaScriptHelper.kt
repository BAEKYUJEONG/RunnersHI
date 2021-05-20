package com.A306.runnershi.Helper

import android.content.Context
import android.webkit.JavascriptInterface
import com.A306.runnershi.Model.Room

class JavaScriptHelper(private val mContext: Context) {
    @JavascriptInterface
    fun setRoomItem(room:Room):Room{
        return room
    }
}