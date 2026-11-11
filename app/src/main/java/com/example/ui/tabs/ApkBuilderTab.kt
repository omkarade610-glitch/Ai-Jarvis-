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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
fun ApkBuilderTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val apkConfig by viewModel.apkConfig.collectAsState()
  val apkRecords by viewModel.apkRecords.collectAsState()
  val scrollState = rememberScrollState()

  var appName by remember { mutableStateOf(apkConfig.appName) }
  var packageName by remember { mutableStateOf(apkConfig.packageName) }
  var versionName by remember { mutableStateOf(apkConfig.versionName) }
  var buildType by remember { mutableStateOf(apkConfig.buildType) }

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
        imageVector = Icons.Default.Android,
        contentDescription = "APK Builder",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "REAL ANDROID APK & AAB BUILD ENGINE",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Form card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(16.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "BUILD SPECIFICATIONS",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = appName,
            onValueChange = {
              appName = it
              viewModel.updateApkConfig(it, packageName, versionName, apkConfig.versionCode, buildType)
            },
            label = { Text("App Name", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = JarvisCyan
            ),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = versionName,
            onValueChange = {
              versionName = it
              viewModel.updateApkConfig(appName, packageName, it, apkConfig.versionCode, buildType)
            },
            label = { Text("Version", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = JarvisCyan
            ),
            modifier = Modifier.width(100.dp)
          )
        }

        OutlinedTextField(
          value = packageName,
          onValueChange = {
            packageName = it
            viewModel.updateApkConfig(appName, it, versionName, apkConfig.versionCode, buildType)
          },
          label = { Text("Application ID / Package Name", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("debug", "release", "aab").forEach { type ->
            val isSelected = buildType == type
            Button(
              onClick = {
                buildType = type
                viewModel.updateApkConfig(appName, packageName, versionName, apkConfig.versionCode, type)
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) JarvisCyan else JarvisSurfaceDark,
                contentColor = if (isSelected) JarvisDeepNavy else Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(type.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = { viewModel.triggerApkBuild() },
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("trigger_apk_build_button")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("VALIDATE & TRIGGER GRADLE BUILD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Build Logs & Records
    Text(
      text = "BUILD JOBS & GRADLE EXECUTION LOGS",
      color = JarvisCyan,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (apkRecords.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(JarvisSurfaceDark, RoundedCornerShape(12.dp))
          .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "No build jobs executed yet. Configure specifications and tap trigger.",
          color = Color(0xFF64748B),
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    } else {
      apkRecords.forEach { record ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(JarvisSurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, if (record.status == "SUCCESS") JarvisCyan else Color.Red, RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${record.config.appName} • ${record.config.buildType.uppercase()}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (record.status == "SUCCESS") Icons.Default.CheckCircle else Icons.Default.Warning,
                  contentDescription = null,
                  tint = if (record.status == "SUCCESS") JarvisAccentGlow else Color.Red,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = record.status,
                  color = if (record.status == "SUCCESS") JarvisAccentGlow else Color.Red,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            Text(
              text = "Time: ${record.timestamp}",
              color = Color(0xFF64748B),
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )

            if (record.artifactPath != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Verified Artifact: ${record.artifactPath}",
                color = JarvisCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Log console
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(JarvisDeepNavy, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Column {
                record.logs.forEach { logLine ->
                  Text(
                    text = logLine,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }
    }
  }
}
