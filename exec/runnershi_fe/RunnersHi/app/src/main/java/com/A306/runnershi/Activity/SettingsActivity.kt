package com.A306.runnershi.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.privacy_dialog.*

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

//    val userViewModel: UserViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {

        val userViewModel: UserViewModel by viewModels<UserViewModel>()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.temp_preferences, rootKey)

            // temp_preference의 경우 : 팝업으로 preference에 온클릭을 달아두겠습니다 :

            // logout
            val logoutPref = findPreference<Preference>("logout")
            logoutPref?.setOnPreferenceClickListener {
                showLogoutDialog(userViewModel)
            }

            // privacy policy
            val privacyPref = findPreference<Preference>("privacyPolicy")
            privacyPref?.setOnPreferenceClickListener {
                showPrivacyDialog()
            }
        }

        // preference Frag 내에서 다뤄줄 내용들은 Frag class 내에서 선언해준다:
        // logout 먼저
        private fun showLogoutDialog(userViewModel: UserViewModel): Boolean {
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle("로그아웃")
            builder.setMessage("로그아웃하시겠습니까?")
            builder.setPositiveButton("로그아웃") {
                dialog: android.content.DialogInterface?, which: Int ->
                logout(userViewModel)
            }
            builder.setCancelable(true)

            val logoutDialog = builder.create()
            logoutDialog.show()

            return true
        }

        private fun logout(userViewModel: UserViewModel) {

            userViewModel.deleteAllUser()

            val splashIntent = Intent(this.context, SplashActivity::class.java)
            startActivity(splashIntent)
        }


        // privacy Policy 띄워주는 부분
        private fun showPrivacyDialog(): Boolean {
            PrivacyCustomDialog(requireContext()).privacyDialog()
            return true
        }

        class PrivacyCustomDialog(context: Context) {
            private val dialog = Dialog(context)

            fun privacyDialog() {
                dialog.setContentView(R.layout.privacy_dialog)

                dialog.window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()

                val btn = dialog.findViewById<Button>(R.id.confirmBtn)
                btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }



}