package com.A306.runnershi.Openvidu.Permission

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R

class PermissionsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setTitle("권한 설정이 필요합니다.")
        builder.setMessage("권한 없이 서비스 사용이 불가능합니다.")
            .setPositiveButton(
                "권한 허용"
            ) { dialog, id -> (activity as MainActivity?)?.askForPermissions() }
            .setNegativeButton(
                "권한 거부",
                { dialog, id -> Log.i(TAG, "User cancelled Permissions Dialog") })
        return builder.create()
    }

    companion object {
        private const val TAG = "PermissionsDialog"
    }
}