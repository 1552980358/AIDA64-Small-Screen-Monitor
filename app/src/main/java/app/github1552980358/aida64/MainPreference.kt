package app.github1552980358.aida64

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import lib.github1552980358.ktExtension.android.content.toast
import lib.github1552980358.ktExtension.android.util.logE

class MainPreference: PreferenceFragmentCompat() {
    
    companion object {
        
        const val TAG = "MainPreference"
        
        private const val KEY_IP = "ip"
        private const val KEY_PORT = "port"
        private const val KEY_BRIGHTNESS = "brightness"
        private const val KEY_AMOLED = "amoled"
        private const val KEY_HEART_BEAT = "heartbeat"
        private const val KEY_CONNECT_TIMEOUT = "connect"
        private const val KEY_READ_TIMEOUT = "read"
        private const val KEY_PERMISSION = "permission"
        
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        logE(TAG, "PreferenceFragment: onCreatePreferences")
        
        addPreferencesFromResource(R.xml.activity_main)
        
        findPreference<EditTextPreference>(KEY_IP)?.apply {
            setOnBindEditTextListener { it.setSingleLine() }
            summary = text
            setOnPreferenceChangeListener { _, newValue ->
                summary = newValue as String
                return@setOnPreferenceChangeListener true
            }
        }
        
        findPreference<EditTextPreference>(KEY_PORT)?.apply {
            setOnBindEditTextListener { it.setSingleLine() }
            summary = text
            setOnPreferenceChangeListener { _, newValue ->
                summary = newValue as String
                return@setOnPreferenceChangeListener true
            }
        }
        
        findPreference<EditTextPreference>(KEY_HEART_BEAT)?.apply {
            setOnBindEditTextListener { it.setSingleLine() }
            summary = "$text ms"
            setOnPreferenceChangeListener { _, newValue ->
                summary = "${newValue as String} ms"
                return@setOnPreferenceChangeListener true
            }
        }
        
        findPreference<EditTextPreference>(KEY_CONNECT_TIMEOUT)?.apply {
            setOnBindEditTextListener { it.setSingleLine() }
            summary = "$text ms"
            setOnPreferenceChangeListener { _, newValue ->
                summary = "${newValue as String} ms"
                return@setOnPreferenceChangeListener true
            }
        }
        
        findPreference<EditTextPreference>(KEY_READ_TIMEOUT)?.apply {
            setOnBindEditTextListener { it.setSingleLine() }
            summary = "$text ms"
            setOnPreferenceChangeListener { _, newValue ->
                summary = "${newValue as String} ms"
                return@setOnPreferenceChangeListener true
            }
        }
        
        findPreference<Preference>(KEY_PERMISSION)?.apply {
            setOnPreferenceClickListener {
                if (checkPermission()) {
                    requireActivity().toast(R.string.mainPreference_client_brightness_toast)
                    return@setOnPreferenceClickListener true
                }
                
                gainPermission()
                return@setOnPreferenceClickListener true
            }
        }
        
    }
    
    fun getSettings() = Settings(
        findPreference<EditTextPreference>(KEY_IP)!!.text!!,
        findPreference<EditTextPreference>(KEY_PORT)!!.text!!,
        findPreference<SwitchPreferenceCompat>(KEY_BRIGHTNESS)!!.sharedPreferences.getBoolean(KEY_BRIGHTNESS, true),
        findPreference<SwitchPreferenceCompat>(KEY_AMOLED)!!.sharedPreferences.getBoolean(KEY_AMOLED, true),
        findPreference<EditTextPreference>(KEY_HEART_BEAT)!!.text!!.toLong(),
        findPreference<EditTextPreference>(KEY_CONNECT_TIMEOUT)!!.text!!.toInt(),
        findPreference<EditTextPreference>(KEY_READ_TIMEOUT)!!.text!!.toInt()
    )
    
    private fun checkPermission() =
        android.provider.Settings.System.canWrite(requireActivity()) ||
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    
    private fun gainPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().startActivityForResult(
                Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS).setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID)),
                0
            )
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_SETTINGS), 0)
        }
    
}