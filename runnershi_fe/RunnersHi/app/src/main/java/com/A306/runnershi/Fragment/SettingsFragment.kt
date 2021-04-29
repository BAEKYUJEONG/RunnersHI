package com.A306.runnershi.Fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.A306.runnershi.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}