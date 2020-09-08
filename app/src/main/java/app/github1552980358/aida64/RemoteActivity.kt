package app.github1552980358.aida64

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.github1552980358.aida64.GuardianService.Companion.SERVICE_CALL
import app.github1552980358.aida64.GuardianService.Companion.START_FOREGROUND
import app.github1552980358.aida64.GuardianService.Companion.STOP_FOREGROUND
import kotlinx.android.synthetic.main.activity_remote.textView_ip
import kotlinx.android.synthetic.main.activity_remote.webView
import lib.github1552980358.ktExtension.android.content.toast
import lib.github1552980358.ktExtension.android.util.logE
import java.io.Serializable

class RemoteActivity: AppCompatActivity(), Serializable {
    
    companion object {
        
        private const val TAG = "RemoteActivity"
        
        const val INTENT_SETTINGS = "SETTINGS"
        
        const val ACTION_CONNECTED_TO_SERVER = "ACTION_CONNECTED_TO_SERVER"
        const val ACTION_DISCONNECTED_TO_SERVER = "ACTION_DISCONNECTED_TO_SERVER"
        
    }
    
    private val broadcastReceiver = object: BroadcastReceiver() {
        
        override fun onReceive(context: Context?, intent: Intent?) {
            logE(TAG, "BroadcastReceiver: onReceive")
            intent ?: return
            
            when (intent.action) {
                ACTION_CONNECTED_TO_SERVER -> {
                    logE(TAG, "Action: ACTION_CONNECTED_TO_SERVER")
                    if (isConnected) {
                        return
                    }
        
                    webView.loadUrl(settings!!.link)
                    isConnected = true
                }
                ACTION_DISCONNECTED_TO_SERVER -> {
                    logE(TAG, "Action: ACTION_DISCONNECTED_TO_SERVER")
                    if (!isConnected) {
                        return
                    }
                    webView.visibility = View.GONE
                    isConnected = false
                    lowBrightness()
                }
            }
        }
        
    }
    
    private val intentFilter = IntentFilter().apply {
        addAction(ACTION_CONNECTED_TO_SERVER)
        addAction(ACTION_DISCONNECTED_TO_SERVER)
    }
    
    private var settings: Settings? = null
    
    private var isConnected = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        logE(TAG, "Activity: onCreate")
        
        super.onCreate(savedInstanceState)
        
        if (intent == null) {
            finish()
            // Restart activity after long time of lock screen
            return
        }
        
        settings = intent.getSerializableExtra(INTENT_SETTINGS) as Settings
        
        if (settings == null) {
            finish()
            // Restart activity after long time of lock screen
            return
        }
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        setContentView(R.layout.activity_remote)
        
        textView_ip.text = settings?.link
        
        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.visibility = View.VISIBLE
                highBrightness()
            }
        }
        
        lowBrightness()
    }
    
    private fun lowBrightness() {
        if (!settings!!.brightness) {
            return
        }
        
        try {
            android.provider.Settings.System.putInt(
                contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                0
            )
        } catch (e: Exception) {
            toast(R.string.remoteActivity_need_permission)
        }
    }
    
    private fun highBrightness() {
        if (!settings!!.brightness) {
            return
        }
        
        try {
            android.provider.Settings.System.putInt(
                contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                255
            )
        } catch (e: Exception) {
            toast(R.string.remoteActivity_need_permission)
        }
    }
    
    override fun onResume() {
        logE(TAG, "Activity: onResume")
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
        
        ContextCompat.startForegroundService(
            this,
            Intent(this, GuardianService::class.java)
                .putExtra(SERVICE_CALL, START_FOREGROUND)
                .putExtra(INTENT_SETTINGS, settings)
        )
        
        if (isConnected) {
            highBrightness()
        }
    }
    
    override fun onPause() {
        logE(TAG, "Activity: onPause")
        super.onPause()
        unregisterReceiver(broadcastReceiver)
        
        startService(
            Intent(this, GuardianService::class.java)
                .putExtra(SERVICE_CALL, STOP_FOREGROUND)
        )
        
        highBrightness()
    }
    
    override fun onBackPressed() = finish()
    
    override fun finish() {
        logE(TAG, "Activity: finish")
        
        startService(Intent(this, GuardianService::class.java).putExtra(SERVICE_CALL, STOP_FOREGROUND))
        
        highBrightness()
        super.finish()
    }
    
}