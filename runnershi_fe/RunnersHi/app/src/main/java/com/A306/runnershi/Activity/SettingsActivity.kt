package com.A306.runnershi.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        private fun showPrivacyDialog(): Boolean {
            Toast.makeText(context, "개인정보를보여줄게요", Toast.LENGTH_LONG).show()
            return true
        }
    }



}