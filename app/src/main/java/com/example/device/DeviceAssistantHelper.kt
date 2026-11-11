package com.example.device

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.Locale

class DeviceAssistantHelper(private val context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isTtsReady = false
  private var isFlashlightOn = false

  init {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e("DeviceHelper", "Failed to init TTS", e)
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isTtsReady = true
      tts?.language = Locale.ENGLISH
    }
  }

  fun speak(text: String, language: String = "en") {
    if (!isTtsReady || tts == null) return
    val loc = when (language.lowercase()) {
      "hi", "hindi" -> Locale.forLanguageTag("hi-IN")
      "mr", "marathi" -> Locale.forLanguageTag("mr-IN")
      else -> Locale.ENGLISH
    }
    tts?.language = loc
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JARVIS_TTS")
  }

  fun stopSpeaking() {
    tts?.stop()
  }

  fun toggleFlashlight(on: Boolean): Boolean {
    return try {
      val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
      val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return false
      cameraManager.setTorchMode(cameraId, on)
      isFlashlightOn = on
      true
    } catch (e: Exception) {
      Log.e("DeviceHelper", "Flashlight error", e)
      false
    }
  }

  fun getBatteryInfo(): Pair<Int, Boolean> {
    return try {
      val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
      val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
      val isCharging = batteryManager?.isCharging == true
      Pair(level, isCharging)
    } catch (e: Exception) {
      Pair(-1, false)
    }
  }

  fun getVolumeInfo(): Pair<Int, Int> {
    return try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
      val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
      val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
      Pair(current, max)
    } catch (e: Exception) {
      Pair(0, 15)
    }
  }

  fun adjustVolume(increase: Boolean): Boolean {
    return try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
      val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
      audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
      true
    } catch (e: Exception) {
      false
    }
  }

  fun openSettings(action: String = Settings.ACTION_SETTINGS) {
    try {
      val intent = Intent(action).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Cannot open settings: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun createAlarm(message: String, hour: Int, minutes: Int) {
    try {
      val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_MESSAGE, message)
        putExtra(AlarmClock.EXTRA_HOUR, hour)
        putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Alarm intent not supported on device", Toast.LENGTH_SHORT).show()
    }
  }

  fun getSystemDiagnostics(): Map<String, String> {
    val statFs = StatFs(Environment.getDataDirectory().path)
    val availableBytes = statFs.availableBlocksLong * statFs.blockSizeLong
    val totalBytes = statFs.blockCountLong * statFs.blockSizeLong
    val freeGb = availableBytes / (1024 * 1024 * 1024)
    val totalGb = totalBytes / (1024 * 1024 * 1024)

    val (batLevel, isCharging) = getBatteryInfo()
    val (vol, maxVol) = getVolumeInfo()

    return mapOf(
      "Device" to "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
      "Android OS" to "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
      "Storage" to "$freeGb GB free of $totalGb GB",
      "Battery" to if (batLevel >= 0) "$batLevel% (${if (isCharging) "Charging" else "Discharging"})" else "Standard",
      "Audio Level" to "$vol / $maxVol",
      "Core Processor" to Build.HARDWARE,
      "Security Patch" to Build.VERSION.SECURITY_PATCH
    )
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (e: Exception) {
      // ignore
    }
  }
}
