package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_network.domain.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.models.DBSetting
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DBSettingsFragment : PreferenceFragmentCompat() {

    companion object{
        const val TITLE = "app_title"
        const val EMAIL = "contactEmail"
        const val DISCLAIMER_FRAG = "legalDisclaimer"
        const val PRIVACY_FRAG = "legalPrivacy"
        const val FACEBOOK = "facebook"
        const val TWITTER = "twitter"
        const val INSTAGRAM = "instagram"
        const val YOUTUBE = "youtube"
    }
    private var settings: DBSetting? = null
        set(value) {
            field = value
            updateUI()
        }

    private lateinit var dbSettingsViewModel: DBSettingsViewModel

    private lateinit var dbSettingsViewModelFactory: DBSettingsViewModelFactory

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        dbSettingsViewModelFactory = DBSettingsViewModelFactory(DBSettingsRepository())

        dbSettingsViewModel = ViewModelProvider(this, dbSettingsViewModelFactory).get(DBSettingsViewModel::class.java)

        GlobalScope.launch(Dispatchers.IO) {

            settings = dbSettingsViewModel.getSettings(requireContext())
        }
    }

    private fun updateUI() {

        activity?.runOnUiThread {

            val appTitle: Preference? = findPreference(TITLE)
            if (appTitle != null) {
                appTitle.title = settings?.title ?: "Desh Bidesh"
                appTitle.summary = settings?.subtitle ?: "Unity in Diversity"
            }

            val appEmail: Preference? = findPreference(EMAIL)
            if (appEmail != null) {
                appEmail.summary = settings?.email ?: "informationdeshbidesh@gmail.com"
            }

            val disclaimerFrag: Preference? = findPreference(DISCLAIMER_FRAG)
            disclaimerFrag?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                    val action =
                        DBSettingsFragmentDirections.actionDBSettingsFragmentToDisclaimerViewFragment(
                            settings?.legal_disclosure ?: ""
                        )

                    view?.findNavController()?.navigate(action)

                true
            }

            val privacyFrag: Preference? = findPreference(PRIVACY_FRAG)
            privacyFrag?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                    val action =
                        DBSettingsFragmentDirections.actionDBSettingsFragmentToPrivacyViewFragment(
                            settings?.privacy_policy ?: ""
                        )

                    view?.findNavController()?.navigate(action)

                true
            }

            val facebook: Preference? = findPreference(FACEBOOK)

            if (facebook != null) {

                facebook.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            settings?.facebook
                                ?: "https://www.facebook.com/Desh-Bidesh-103355211335288"
                        )
                    )
                    startActivity(browserIntent)

                    true
                }
            }

            val twitter: Preference? = findPreference(TWITTER)

            if (twitter != null) {

                twitter.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            settings?.twitter ?: "https://twitter.com/DeshBidesh4"
                        )
                    )
                    startActivity(browserIntent)

                    true
                }
            }

            val instagram: Preference? = findPreference(INSTAGRAM)

            if (instagram != null) {

                instagram.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            settings?.instagram ?: "https://www.instagram.com/deshbideshapp"
                        )
                    )
                    startActivity(browserIntent)

                    true
                }
            }

            val youtube: Preference? = findPreference(YOUTUBE)

            if (youtube != null) {

                youtube.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            settings?.youtube
                                ?: "https://www.youtube.com/channel/UC22Tp-7tetG-CwbnNPPkeWQ"
                        )
                    )
                    startActivity(browserIntent)

                    true
                }
            }
        }
    }

}