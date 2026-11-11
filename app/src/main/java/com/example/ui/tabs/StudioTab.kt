package com.example.ui.tabs

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
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
fun StudioTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val codeLang by viewModel.codeLang.collectAsState()
  val codeText by viewModel.codeText.collectAsState()
  val codeOutput by viewModel.codeOutput.collectAsState()
  val isCodeRunning by viewModel.isCodeRunning.collectAsState()
  var projectPrompt by remember { mutableStateOf("") }
  var isSavedNotice by remember { mutableStateOf(false) }

  val languages = listOf("Kotlin", "Python", "JavaScript", "HTML", "C++", "Rust", "SQL")
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisDeepNavy)
      .padding(16.dp)
      .verticalScroll(scrollState)
  ) {
    // Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = "Code Studio",
          tint = JarvisCyan,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "AI CODING & DEV STUDIO",
          color = JarvisCyan,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
      }

      IconButton(
        onClick = {
          viewModel.saveProject("Project_${System.currentTimeMillis() % 1000}", codeLang.lowercase(), "Studio Code", codeText)
          isSavedNotice = true
        },
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = if (isSavedNotice) Icons.Default.Check else Icons.Default.Save,
          contentDescription = "Save Project",
          tint = if (isSavedNotice) JarvisAccentGlow else JarvisCyan
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick AI App/Website Generator Prompt
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(12.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Column {
        Text(
          text = "AI APP & WEB BUILDER (NATURAL LANGUAGE)",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
            value = projectPrompt,
            onValueChange = { projectPrompt = it },
            placeholder = { Text("Describe app/website e.g., 'Modern Portfolio with Dark Mode'...", fontSize = 11.sp, color = Color(0xFF6B7A94)) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = JarvisCyan
            ),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (projectPrompt.isNotBlank()) {
                viewModel.updateCodeText("// Generating $projectPrompt...")
                viewModel.runCodeAction("Generate complete project for: $projectPrompt")
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("BUILD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Language pills
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(languages) { lang ->
        val isSelected = codeLang == lang
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurfaceCard)
            .border(1.dp, if (isSelected) JarvisCyan else JarvisCardBorder, RoundedCornerShape(10.dp))
            .clickable { viewModel.setCodeLanguage(lang) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = lang,
            color = if (isSelected) JarvisCyan else Color(0xFFB0C0D8),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Code Editor
    OutlinedTextField(
      value = codeText,
      onValueChange = { viewModel.updateCodeText(it) },
      minLines = 8,
      maxLines = 14,
      textStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = Color(0xFFE2E8F0)
      ),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = JarvisCyan,
        unfocusedBorderColor = JarvisCardBorder,
        focusedContainerColor = JarvisSurfaceDark,
        unfocusedContainerColor = JarvisSurfaceDark
      ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("code_editor_textarea")
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Action buttons row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { viewModel.runCodeAction("explain") },
        enabled = !isCodeRunning,
        colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard, contentColor = JarvisCyan),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Explain", fontSize = 11.sp)
      }

      Button(
        onClick = { viewModel.runCodeAction("fix bugs and detect errors") },
        enabled = !isCodeRunning,
        colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard, contentColor = Color(0xFFFF3366)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Fix Bugs", fontSize = 11.sp)
      }

      Button(
        onClick = { viewModel.runCodeAction("optimize and enhance performance") },
        enabled = !isCodeRunning,
        colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Optimize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Output console
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceDark, RoundedCornerShape(12.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "AI STUDIO OUTPUT CONSOLE",
            color = JarvisCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          if (isCodeRunning) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              color = JarvisCyan,
              strokeWidth = 2.dp
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (codeOutput.isNotBlank()) codeOutput else if (isCodeRunning) "JARVIS neural compilation in progress..." else "Tap Explain, Fix Bugs, or Optimize to execute AI analysis.",
          color = if (codeOutput.isNotBlank()) Color(0xFFCBD5E1) else Color(0xFF64748B),
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          lineHeight = 18.sp
        )
      }
    }
  }
}
