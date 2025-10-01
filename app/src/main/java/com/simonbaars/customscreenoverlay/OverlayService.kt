package com.simonbaars.customscreenoverlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var overlayView: TextView? = null
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        prefs = getSharedPreferences("OverlayPrefs", MODE_PRIVATE)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_OVERLAY" -> {
                startForeground(NOTIFICATION_ID, createNotification())
                showOverlay()
                prefs.edit().putBoolean("serviceRunning", true).apply()
            }
            "UPDATE_OVERLAY" -> {
                updateOverlay()
            }
            else -> {
                stopSelf()
            }
        }
        return START_STICKY
    }
    
    private fun showOverlay() {
        if (overlayView != null) {
            return // Already showing
        }
        
        overlayView = TextView(this).apply {
            text = prefs.getString("message", getString(R.string.default_message))
            textSize = prefs.getInt("textSize", 24).toFloat()
            setTextColor(prefs.getInt("textColor", Color.WHITE))
            setTypeface(null, Typeface.BOLD)
            setShadowLayer(8f, 0f, 0f, Color.BLACK)
        }
        
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        
        params.gravity = Gravity.TOP or Gravity.START
        
        // Calculate position based on percentages
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val xPercent = prefs.getInt("xPosition", 50)
        val yPercent = prefs.getInt("yPosition", 30)
        
        params.x = (displayMetrics.widthPixels * xPercent / 100)
        params.y = (displayMetrics.heightPixels * yPercent / 100)
        
        windowManager.addView(overlayView, params)
    }
    
    private fun updateOverlay() {
        overlayView?.let { view ->
            view.text = prefs.getString("message", getString(R.string.default_message))
            view.textSize = prefs.getInt("textSize", 24).toFloat()
            view.setTextColor(prefs.getInt("textColor", Color.WHITE))
            
            // Update position
            val params = view.layoutParams as WindowManager.LayoutParams
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            
            val xPercent = prefs.getInt("xPosition", 50)
            val yPercent = prefs.getInt("yPosition", 30)
            
            params.x = (displayMetrics.widthPixels * xPercent / 100)
            params.y = (displayMetrics.heightPixels * yPercent / 100)
            
            windowManager.updateViewLayout(view, params)
        }
    }
    
    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        prefs.edit().putBoolean("serviceRunning", false).apply()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Screen overlay notification"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.overlay_service_notification))
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()
    
    companion object {
        private const val CHANNEL_ID = "overlay_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}
