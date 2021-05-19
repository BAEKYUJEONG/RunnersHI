package com.A306.runnershi.Openvidu.Permission

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.A306.runnershi.R

// 위치정보 권한 설정을 해준다 :
class PermissionInfo(context: Context) {
    val builder = AlertDialog.Builder(context)
    val locationText = context.getString(R.string.permission_info_location)
    val context = context
    fun showLocationPermissionInfo(intent: Intent) {
        builder.setTitle("위치정보 이용 안내")
        builder.setMessage(locationText)
        builder.setPositiveButton("동의하기", DialogInterface.OnClickListener { dialog, which ->
            this.context.startActivity(intent)
        })
        builder.setCancelable(false)

        val locationPermissionDialog = builder.create()
        locationPermissionDialog.show()
    }
}