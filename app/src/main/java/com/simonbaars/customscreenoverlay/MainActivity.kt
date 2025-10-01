package com.simonbaars.customscreenoverlay

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var prefs: SharedPreferences
    private lateinit var messageEditText: EditText
    private lateinit var textSizeSeekBar: SeekBar
    private lateinit var textSizeValue: TextView
    private lateinit var xPositionSeekBar: SeekBar
    private lateinit var xPositionValue: TextView
    private lateinit var yPositionSeekBar: SeekBar
    private lateinit var yPositionValue: TextView
    private lateinit var toggleButton: Button
    private lateinit var statusText: TextView
    
    private var selectedColor: Int = Color.WHITE
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences("OverlayPrefs", MODE_PRIVATE)
        
        initViews()
        loadSettings()
        setupListeners()
        updateUI()
    }
    
    private fun initViews() {
        messageEditText = findViewById(R.id.messageEditText)
        textSizeSeekBar = findViewById(R.id.textSizeSeekBar)
        textSizeValue = findViewById(R.id.textSizeValue)
        xPositionSeekBar = findViewById(R.id.xPositionSeekBar)
        xPositionValue = findViewById(R.id.xPositionValue)
        yPositionSeekBar = findViewById(R.id.yPositionSeekBar)
        yPositionValue = findViewById(R.id.yPositionValue)
        toggleButton = findViewById(R.id.toggleOverlayButton)
        statusText = findViewById(R.id.statusText)
    }
    
    private fun loadSettings() {
        messageEditText.setText(prefs.getString("message", getString(R.string.default_message)))
        
        val textSize = prefs.getInt("textSize", 24)
        textSizeSeekBar.progress = textSize
        textSizeValue.text = "${textSize}sp"
        
        selectedColor = prefs.getInt("textColor", Color.WHITE)
        
        val xPos = prefs.getInt("xPosition", 50)
        xPositionSeekBar.progress = xPos
        xPositionValue.text = "$xPos%"
        
        val yPos = prefs.getInt("yPosition", 30)
        yPositionSeekBar.progress = yPos
        yPositionValue.text = "$yPos%"
    }
    
    private fun setupListeners() {
        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = if (progress < 10) 10 else progress
                textSizeValue.text = "${size}sp"
                saveSettings()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        xPositionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                xPositionValue.text = "$progress%"
                saveSettings()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        yPositionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                yPositionValue.text = "$progress%"
                saveSettings()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        findViewById<Button>(R.id.whiteColorButton).setOnClickListener {
            selectedColor = Color.WHITE
            saveSettings()
            Toast.makeText(this, "Color: White", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.blackColorButton).setOnClickListener {
            selectedColor = Color.BLACK
            saveSettings()
            Toast.makeText(this, "Color: Black", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.redColorButton).setOnClickListener {
            selectedColor = Color.RED
            saveSettings()
            Toast.makeText(this, "Color: Red", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.blueColorButton).setOnClickListener {
            selectedColor = Color.BLUE
            saveSettings()
            Toast.makeText(this, "Color: Blue", Toast.LENGTH_SHORT).show()
        }
        
        toggleButton.setOnClickListener {
            if (isServiceRunning()) {
                stopOverlayService()
            } else {
                if (checkOverlayPermission()) {
                    startOverlayService()
                } else {
                    requestOverlayPermission()
                }
            }
        }
    }
    
    private fun saveSettings() {
        prefs.edit().apply {
            putString("message", messageEditText.text.toString())
            putInt("textSize", textSizeSeekBar.progress.coerceAtLeast(10))
            putInt("textColor", selectedColor)
            putInt("xPosition", xPositionSeekBar.progress)
            putInt("yPosition", yPositionSeekBar.progress)
            apply()
        }
        
        // Update overlay if running
        if (isServiceRunning()) {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "UPDATE_OVERLAY"
            startService(intent)
        }
    }
    
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun startOverlayService() {
        saveSettings()
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "START_OVERLAY"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        updateUI()
    }
    
    private fun stopOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        stopService(intent)
        updateUI()
    }
    
    private fun isServiceRunning(): Boolean {
        return prefs.getBoolean("serviceRunning", false)
    }
    
    private fun updateUI() {
        val isRunning = isServiceRunning()
        if (isRunning) {
            statusText.text = getString(R.string.overlay_running)
            statusText.setTextColor(Color.GREEN)
            toggleButton.text = getString(R.string.stop_overlay)
        } else {
            statusText.text = getString(R.string.overlay_stopped)
            statusText.setTextColor(Color.RED)
            toggleButton.text = getString(R.string.start_overlay)
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateUI()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (checkOverlayPermission()) {
                startOverlayService()
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1234
    }
}
