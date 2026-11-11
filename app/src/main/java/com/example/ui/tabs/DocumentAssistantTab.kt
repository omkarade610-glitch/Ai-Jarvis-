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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun DocumentAssistantTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val docTitle by viewModel.docTitle.collectAsState()
  val docContent by viewModel.docContent.collectAsState()
  val docQuestion by viewModel.docQuestion.collectAsState()
  val docAnalysis by viewModel.docAnalysis.collectAsState()
  val isDocAnalyzing by viewModel.isDocAnalyzing.collectAsState()
  val scrollState = rememberScrollState()

  val presets = listOf(
    "Executive Summary",
    "Extract Key Data Points",
    "Security & Vulnerabilities",
    "Action Items List"
  )

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
        imageVector = Icons.Default.Description,
        contentDescription = "Document Assistant",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "DOCUMENT & FILE INTELLIGENCE ASSISTANT",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Document Metadata & Content Form
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(14.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "FILE SPECIFICATION (PDF / TXT / DOCX / CSV)",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        OutlinedTextField(
          value = docTitle,
          onValueChange = { viewModel.updateDocTitle(it) },
          label = { Text("File Name", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisCardBorder,
            focusedContainerColor = JarvisSurfaceDark,
            unfocusedContainerColor = JarvisSurfaceDark
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = docContent,
          onValueChange = { viewModel.updateDocContent(it) },
          label = { Text("Document Content / Text Extract", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
          minLines = 4,
          maxLines = 8,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisCardBorder,
            focusedContainerColor = JarvisSurfaceDark,
            unfocusedContainerColor = JarvisSurfaceDark
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = docQuestion,
          onValueChange = { viewModel.updateDocQuestion(it) },
          label = { Text("Question or Analysis Directive", color = Color(0xFF8B9BB4), fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisCardBorder,
            focusedContainerColor = JarvisSurfaceDark,
            unfocusedContainerColor = JarvisSurfaceDark
          ),
          modifier = Modifier.fillMaxWidth()
        )

        // Preset Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(presets) { preset ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(JarvisSurfaceDark)
                .border(1.dp, JarvisCardBorder, RoundedCornerShape(8.dp))
                .clickable {
                  viewModel.updateDocQuestion(preset)
                  viewModel.analyzeDocument()
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(preset, color = Color(0xFFB0C0D8), fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }

        Button(
          onClick = { viewModel.analyzeDocument() },
          enabled = docContent.isNotBlank() && !isDocAnalyzing,
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("analyze_document_button")
        ) {
          if (isDocAnalyzing) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = JarvisDeepNavy, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ANALYZING DOCUMENT...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("EXECUTE DEEP DOCUMENT ANALYSIS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Analysis Findings Console
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceDark, RoundedCornerShape(14.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "INTELLIGENCE REPORT & FINDINGS",
            color = JarvisCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          if (isDocAnalyzing) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = JarvisCyan, strokeWidth = 2.dp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (docAnalysis.isNotBlank()) docAnalysis else "Enter document text and query to extract summaries, tables, and answers.",
          color = if (docAnalysis.isNotBlank()) Color(0xFFE2E8F0) else Color(0xFF64748B),
          fontSize = 12.5.sp,
          fontFamily = FontFamily.Monospace,
          lineHeight = 18.sp
        )
      }
    }
  }
}
