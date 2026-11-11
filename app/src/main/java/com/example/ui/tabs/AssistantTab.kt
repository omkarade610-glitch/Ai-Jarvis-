package com.example.ui.tabs

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.components.HolographicCore
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanVariant
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel
import java.util.Locale

@Composable
fun AssistantTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val chatInput by viewModel.chatInput.collectAsState()
  val isAiThinking by viewModel.isAiThinking.collectAsState()
  val voiceListening by viewModel.voiceListening.collectAsState()
  val selectedLanguage by viewModel.selectedLanguage.collectAsState()
  val listState = rememberLazyListState()

  // Speech to text activity result launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val data = result.data
      val spokenList = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val spokenText = spokenList?.firstOrNull()
      if (!spokenText.isNullOrBlank()) {
        viewModel.sendMessage(spokenText)
      }
    }
  }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisDeepNavy)
  ) {
    // HUD Header with Core
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceDark.copy(alpha = 0.6f))
        .padding(vertical = 8.dp, horizontal = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "NEURAL LINK: ACTIVE",
            color = JarvisCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
          Text(
            text = "LANG: $selectedLanguage",
            color = Color(0xFF8B9BB4),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        HolographicCore(
          size = 64.dp,
          isActive = true,
          isListening = voiceListening
        )

        IconButton(
          onClick = { viewModel.toggleVoiceListening() },
          modifier = Modifier
            .size(40.dp)
            .background(
              if (voiceListening) JarvisCyan.copy(alpha = 0.2f) else JarvisSurfaceCard,
              CircleShape
            )
            .border(
              1.dp,
              if (voiceListening) JarvisCyan else JarvisCardBorder,
              CircleShape
            )
            .testTag("voice_listen_toggle")
        ) {
          Icon(
            imageVector = if (voiceListening) Icons.Default.Mic else Icons.Default.MicOff,
            contentDescription = "Toggle Voice Listening",
            tint = if (voiceListening) JarvisCyan else Color.Gray,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Language & Quick prompt pills
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val quickPills = listOf(
        "English" to "English",
        "हिंदी" to "Hindi",
        "मराठी" to "Marathi",
        "Explain Quantum Computing" to "prompt",
        "Write Python script" to "prompt",
        "Generate Android App" to "prompt"
      )

      items(quickPills) { (label, type) ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (type != "prompt" && selectedLanguage == type) JarvisCyan.copy(alpha = 0.25f)
              else JarvisSurfaceCard
            )
            .border(
              1.dp,
              if (type != "prompt" && selectedLanguage == type) JarvisCyan else JarvisCardBorder,
              RoundedCornerShape(16.dp)
            )
            .clickable {
              if (type == "prompt") {
                viewModel.sendMessage(label)
              } else {
                viewModel.setLanguage(type)
              }
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = label,
            color = if (type != "prompt" && selectedLanguage == type) JarvisCyan else Color(0xFFB0C0D8),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    // Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        MessageBubble(
          message = msg,
          onSpeak = { viewModel.deviceHelper.speak(msg.content, msg.language) }
        )
      }

      if (isAiThinking) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = JarvisCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "JARVIS computing response...",
              color = JarvisCyan,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }

    // Chat input bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceDark)
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = {
          val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
              RecognizerIntent.EXTRA_LANGUAGE_MODEL,
              RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            val langCode = when (selectedLanguage.lowercase()) {
              "hindi" -> "hi-IN"
              "marathi" -> "mr-IN"
              else -> Locale.getDefault().toLanguageTag()
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS AI...")
          }
          try {
            speechLauncher.launch(intent)
          } catch (e: Exception) {
            viewModel.deviceHelper.speak("Microphone speech recognition unavailable on device.")
          }
        },
        modifier = Modifier
          .size(44.dp)
          .testTag("mic_input_button")
      ) {
        Icon(
          imageVector = Icons.Default.Mic,
          contentDescription = "Voice Input",
          tint = JarvisCyanVariant
        )
      }

      OutlinedTextField(
        value = chatInput,
        onValueChange = { viewModel.updateChatInput(it) },
        placeholder = {
          Text(
            text = "Ask JARVIS in English, हिंदी, मराठी...",
            color = Color(0xFF6B7A94),
            fontSize = 13.sp
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color(0xFFE2E8F0),
          focusedBorderColor = JarvisCyan,
          unfocusedBorderColor = JarvisCardBorder,
          focusedContainerColor = JarvisSurfaceCard,
          unfocusedContainerColor = JarvisSurfaceCard
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 6.dp)
          .testTag("chat_input_field")
      )

      IconButton(
        onClick = { viewModel.sendMessage() },
        enabled = chatInput.isNotBlank(),
        modifier = Modifier
          .size(44.dp)
          .background(
            if (chatInput.isNotBlank()) JarvisCyan else Color.DarkGray,
            CircleShape
          )
          .testTag("send_message_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send Message",
          tint = JarvisDeepNavy,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun MessageBubble(
  message: ChatMessage,
  onSpeak: () -> Unit
) {
  val isUser = message.role == "user"

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 320.dp)
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
        .background(
          if (isUser) JarvisCyan.copy(alpha = 0.18f) else JarvisSurfaceCard
        )
        .border(
          1.dp,
          if (isUser) JarvisCyan.copy(alpha = 0.5f) else JarvisCardBorder,
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
        .padding(12.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isUser) "YOU" else "JARVIS",
            color = if (isUser) JarvisCyanVariant else JarvisAccentGlow,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )

          if (!isUser) {
            IconButton(
              onClick = onSpeak,
              modifier = Modifier.size(22.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Read Aloud",
                tint = JarvisCyan,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = message.content,
          color = if (isUser) Color.White else Color(0xFFE2E8F0),
          fontSize = 13.5.sp,
          lineHeight = 20.sp
        )
      }
    }
  }
}
