package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun ToolsTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val deviceHelper = viewModel.deviceHelper
  var isFlashlightOn by remember { mutableStateOf(false) }
  val diagnostics = remember { deviceHelper.getSystemDiagnostics() }
  val (volCurrent, volMax) = remember { deviceHelper.getVolumeInfo() }
  val (batteryLevel, isCharging) = remember { deviceHelper.getBatteryInfo() }
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisDeepNavy)
      .padding(16.dp)
      .verticalScroll(scrollState)
  ) {
    // Header
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.Build,
        contentDescription = "Device Tools",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "ANDROID MOBILE DEVICE ASSISTANT",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Hardware Controls Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(16.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
          text = "HARDWARE & SYSTEM TELEMETRY",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        // Flashlight Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FlashlightOn,
              contentDescription = "Flashlight",
              tint = if (isFlashlightOn) JarvisCyan else Color.Gray,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Flashlight Controller", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              Text(if (isFlashlightOn) "Torch Active" else "Torch Off", color = Color(0xFF8B9BB4), fontSize = 11.sp)
            }
          }

          Switch(
            checked = isFlashlightOn,
            onCheckedChange = { checked ->
              val success = deviceHelper.toggleFlashlight(checked)
              if (success) isFlashlightOn = checked
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = JarvisDeepNavy,
              checkedTrackColor = JarvisCyan
            ),
            modifier = Modifier.testTag("flashlight_toggle_switch")
          )
        }

        // Volume Controls Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Audio Volume",
              tint = JarvisCyan,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Audio Stream Volume", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              Text("Level $volCurrent / $volMax", color = Color(0xFF8B9BB4), fontSize = 11.sp)
            }
          }

          Row {
            IconButton(
              onClick = { deviceHelper.adjustVolume(false) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "Volume Down", tint = JarvisCyan)
            }
            IconButton(
              onClick = { deviceHelper.adjustVolume(true) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume Up", tint = JarvisCyan)
            }
          }
        }

        // Battery Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.BatteryChargingFull,
              contentDescription = "Battery",
              tint = JarvisAccentGlow,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Battery Status", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
              Text(
                if (batteryLevel >= 0) "$batteryLevel% • ${if (isCharging) "Charging" else "On Battery"}" else "Sensor connected",
                color = Color(0xFF8B9BB4),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Device Actions Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = { deviceHelper.openSettings() },
        colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard, contentColor = JarvisCyan),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Settings", fontSize = 11.sp)
      }

      Button(
        onClick = { deviceHelper.createAlarm("JARVIS Reminder", 7, 30) },
        colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard, contentColor = JarvisAccentGlow),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Set Alarm", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // System Diagnostics Card
    Text(
      text = "SYSTEM ARCHITECTURE & DIAGNOSTICS",
      color = JarvisCyan,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceDark, RoundedCornerShape(14.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        diagnostics.forEach { (key, value) ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = key, color = Color(0xFF8B9BB4), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}
