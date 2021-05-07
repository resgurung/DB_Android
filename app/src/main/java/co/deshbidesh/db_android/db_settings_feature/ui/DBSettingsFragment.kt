package co.deshbidesh.db_android.db_settings_feature.ui

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.deshbidesh.db_android.R

class DBSettingsFragment : PreferenceFragmentCompat() {

    companion object{
        const val DISCLAIMER_FRAG = "legalDisclaimer"
        const val PRIVACY_FRAG = "legalPrivacy"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val disclaimerFrag: Preference? = findPreference(DISCLAIMER_FRAG)
        disclaimerFrag?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            view?.findNavController()?.navigate(R.id.action_DBSettingsFragment_to_disclaimerViewFragment)
            true
        }

        val privacyFrag: Preference? = findPreference(PRIVACY_FRAG)
        privacyFrag?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            view?.findNavController()?.navigate(R.id.action_DBSettingsFragment_to_privacyViewFragment)
            true
        }
    }

}