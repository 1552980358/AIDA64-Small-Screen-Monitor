package app.github1552980358.aida64

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.github1552980358.aida64.RemoteActivity.Companion.ACTION_CONNECTED_TO_SERVER
import app.github1552980358.aida64.RemoteActivity.Companion.ACTION_DISCONNECTED_TO_SERVER
import app.github1552980358.aida64.RemoteActivity.Companion.INTENT_SETTINGS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lib.github1552980358.ktExtension.android.util.logE
import lib.github1552980358.ktExtension.jvm.keyword.tryCatch
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import java.net.HttpURLConnection
import java.net.URL

class GuardianService: Service() {
    
    companion object {
        
        private const val TAG = "GuardianService"
        
        const val SERVICE_CALL = "SERVICE_CALL"
        const val UNKNOWN = -1
        const val START_FOREGROUND = 0
        const val STOP_FOREGROUND = 1
        
        const val WAKE_LOCK_TAG = "app.github1552980358.aida64:GuardianService"
        
        const val ChannelName = TAG
        
        const val ChannelId = "app.github1552980358.aida64"
        
        const val ServiceId = 2333
        
    }
    
    private var wakeLock: PowerManager.WakeLock? = null
    
    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManagerCompat
    
    private var isForeground = false
    
    private var settings: Settings? = null
    
    private var coroutineJob: Job? = null
    
    override fun onCreate() {
        logE(TAG, "Service: onCreate")
        
        wakeLock = tryRun { (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG) }
        notificationManager = getNotificationManager()
        notification = getNotification()
    }
    
    @SuppressLint("WakelockTimeout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logE(TAG, "Service: onStartCommand")
        
        intent ?: return super.onStartCommand(intent, flags, startId)
        
        when (intent.getIntExtra(SERVICE_CALL, UNKNOWN)) {
    
            START_FOREGROUND -> {
                logE(TAG, "START_FOREGROUND")
        
                settings = intent.getSerializableExtra(INTENT_SETTINGS) as Settings
        
                if (wakeLock == null) {
                    wakeLock =
                        tryRun { (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG) }
                }
                wakeLock?.acquire()
                if (!isForeground) {
                    startForeground(ServiceId, notification)
                    isForeground = true
                }
                launchCheckingConnection()
            }
    
            STOP_FOREGROUND, UNKNOWN -> {
                if (wakeLock?.isHeld == true) {
                    wakeLock?.release()
                }
                stopForeground(true)
                isForeground = false
                coroutineJob?.cancel()
                coroutineJob = null
            }
            
            else -> {
                if (wakeLock?.isHeld == true) {
                    wakeLock?.release()
                }
                stopForeground(true)
                isForeground = false
                coroutineJob?.cancel()
                coroutineJob = null
            }
            
        }
        
        return super.onStartCommand(intent, flags, startId)
    }
    
    private fun launchCheckingConnection() {
        tryCatch { coroutineJob?.cancel() }
        coroutineJob = GlobalScope.launch(Dispatchers.Main) {
            var connection: HttpURLConnection?
            while (isForeground) {
                logE(TAG, "checkConnection")
                
                connection = withContext(Dispatchers.IO) {
                    tryRun { URL(settings!!.link).openConnection() }
                } as HttpURLConnection?
                
                if (connection == null) {
                    sendBroadcast(Intent(ACTION_DISCONNECTED_TO_SERVER))
                    continue
                }
                
                connection.connectTimeout = settings!!.connect
                connection.readTimeout = settings!!.read
                
                // Should run in non-main-thread
                // otherwise exception will be thrown
                if (withContext(Dispatchers.IO) {
                        try {
                            @Suppress("BlockingMethodInNonBlockingContext")
                            connection.connect()
                            connection.disconnect()
                            true
                        } catch (e: Exception) {
                            false
                        }
                    }) {
                    sendBroadcast(Intent(ACTION_CONNECTED_TO_SERVER))
                } else {
                    sendBroadcast(Intent(ACTION_DISCONNECTED_TO_SERVER))
                }
                
                delay(settings!!.heartbeat)
            }
        }
    }
    
    private fun getNotificationManager(): NotificationManagerCompat {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(NotificationChannel(ChannelId, ChannelName, IMPORTANCE_HIGH))
        }
        return NotificationManagerCompat.from(this)
    }
    
    private fun getNotification() = NotificationCompat.Builder(this, ChannelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
        .setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_content))
        .build()
    
    override fun onDestroy() {
        logE(TAG, "Service: onDestroy")
        tryCatch { wakeLock?.release() }
        wakeLock = null
    }
    
    override fun onBind(p0: Intent?): IBinder? {
        logE(TAG, "Service: onBind")
        return null
    }
    
}