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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OwnerTab(
  viewModel: JarvisViewModel,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  val auth = Firebase.auth
  val currentUser = auth.currentUser

  val masterOn by viewModel.masterOn.collectAsState()
  val voiceListening by viewModel.voiceListening.collectAsState()
  val privateMode by viewModel.privateMode.collectAsState()
  val userMemories by viewModel.userMemories.collectAsState()

  var showAddMemoryDialog by remember { mutableStateOf(false) }
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
        imageVector = Icons.Default.Shield,
        contentDescription = "Owner Shield",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "OWNER CONTROL & PRIVACY CENTER",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Profile Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(16.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .background(JarvisCyan.copy(alpha = 0.2f), CircleShape)
              .border(1.dp, JarvisCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = currentUser?.displayName ?: "Authorized Owner",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = currentUser?.email ?: "tusharade31@gmail.com",
              color = Color(0xFF8B9BB4),
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        IconButton(
          onClick = {
            coroutineScope.launch {
              withContext(NonCancellable) {
                try {
                  credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                  // ignore
                } finally {
                  auth.signOut()
                  onSignOut()
                }
              }
            }
          },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFFFF3366))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Privacy & Master Switches Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(16.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
          text = "MASTER SYSTEM PROTOCOLS",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        // Master Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("JARVIS Master AI Switch", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(if (masterOn) "System Online & Fully Functional" else "All Sub-systems in Standby", color = Color(0xFF8B9BB4), fontSize = 11.sp)
          }
          Switch(
            checked = masterOn,
            onCheckedChange = { viewModel.toggleMasterSwitch() },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisDeepNavy, checkedTrackColor = JarvisCyan),
            modifier = Modifier.testTag("master_switch_toggle")
          )
        }

        // Voice Listening
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Voice Capture & Listening", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(if (voiceListening) "Mic Capture & Voice Mode Active" else "Microphone Inactive", color = Color(0xFF8B9BB4), fontSize = 11.sp)
          }
          Switch(
            checked = voiceListening,
            onCheckedChange = { viewModel.toggleVoiceListening() },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisDeepNavy, checkedTrackColor = JarvisCyan)
          )
        }

        // Private Mode
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Private Conversation Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(if (privateMode) "No Chat or Memory Logging" else "Cloud Sync Enabled", color = Color(0xFF8B9BB4), fontSize = 11.sp)
          }
          Switch(
            checked = privateMode,
            onCheckedChange = { viewModel.togglePrivateMode() },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisDeepNavy, checkedTrackColor = JarvisAccentGlow)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Approved Memories Section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "PERSONAL MEMORY SYSTEM (${userMemories.size})",
        color = JarvisCyan,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )

      IconButton(
        onClick = { showAddMemoryDialog = true },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = JarvisCyan)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (userMemories.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(JarvisSurfaceDark, RoundedCornerShape(12.dp))
          .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("No user instructions or preferences memorized yet.", color = Color(0xFF64748B), fontSize = 11.sp)
      }
    } else {
      userMemories.forEach { memory ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(JarvisSurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = memory.key, color = JarvisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(text = memory.value, color = Color.White, fontSize = 12.sp)
            }
            IconButton(
              onClick = { viewModel.deleteMemory(memory.id) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }

  if (showAddMemoryDialog) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("preference") }

    AlertDialog(
      onDismissRequest = { showAddMemoryDialog = false },
      containerColor = JarvisSurfaceDark,
      title = {
        Text(
          text = "ADD APPROVED MEMORY",
          color = JarvisCyan,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Memory Key (e.g. favorite_language)") },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = JarvisCyan)
          )
          OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Instruction / Value") },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = JarvisCyan)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.addMemory(key, value, category)
            showAddMemoryDialog = false
          },
          enabled = key.isNotBlank() && value.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy)
        ) {
          Text("REMEMBER")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddMemoryDialog = false }) {
          Text("CANCEL", color = Color.Gray)
        }
      }
    )
  }
}
