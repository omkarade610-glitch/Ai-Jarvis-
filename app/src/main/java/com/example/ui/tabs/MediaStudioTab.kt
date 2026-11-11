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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayCircle
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
fun MediaStudioTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val animeStyle by viewModel.animeStyle.collectAsState()
  val mediaOutput by viewModel.mediaOutput.collectAsState()
  val isMediaProcessing by viewModel.isMediaProcessing.collectAsState()

  var selectedMode by remember { mutableStateOf("ANIME") } // "ANIME" or "VIDEO"
  var promptInput by remember { mutableStateOf("Cybernetic hero with glowing blue energy blade and holographic HUD visor") }
  val scrollState = rememberScrollState()

  val animeStyles = listOf("Cyberpunk Anime", "Studio Ghibli", "Shonen Action", "3D CGI Movie", "Manga Noir", "Pencil Sketch")
  val videoStyles = listOf("Cinematic Sci-Fi", "Anime Episode", "YouTube Short", "Tech Explainer 3D", "Documentary")

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
        imageVector = Icons.Default.MovieCreation,
        contentDescription = "Media Studio",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "AI ANIME & VIDEO CREATION STUDIO",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Mode Selector (Anime vs Video)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          selectedMode = "ANIME"
          promptInput = "Cybernetic hero with glowing blue energy blade and holographic HUD visor"
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (selectedMode == "ANIME") JarvisCyan else JarvisSurfaceCard,
          contentColor = if (selectedMode == "ANIME") JarvisDeepNavy else Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Anime & Character Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = {
          selectedMode = "VIDEO"
          promptInput = "Journey inside a quantum neural network supercomputer"
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (selectedMode == "VIDEO") JarvisCyan else JarvisSurfaceCard,
          contentColor = if (selectedMode == "VIDEO") JarvisDeepNavy else Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Video & CGI Director", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Style picker
    Text(
      text = if (selectedMode == "ANIME") "ARTISTIC STYLE PRESET" else "DIRECTING & PRODUCTION STYLE",
      color = Color(0xFF8B9BB4),
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace
    )

    Spacer(modifier = Modifier.height(6.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      val styles = if (selectedMode == "ANIME") animeStyles else videoStyles
      items(styles) { style ->
        val isSelected = animeStyle == style
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurfaceCard)
            .border(1.dp, if (isSelected) JarvisCyan else JarvisCardBorder, RoundedCornerShape(10.dp))
            .clickable { viewModel.setAnimeStyle(style) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = style,
            color = if (isSelected) JarvisCyan else Color(0xFFB0C0D8),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Prompt input card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(JarvisSurfaceCard, RoundedCornerShape(14.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
      Column {
        Text(
          text = if (selectedMode == "ANIME") "CHARACTER / SCENE DESCRIPTION" else "VIDEO TOPIC & NARRATIVE",
          color = JarvisAccentGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = promptInput,
          onValueChange = { promptInput = it },
          minLines = 3,
          maxLines = 6,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color(0xFFE2E8F0),
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisCardBorder,
            focusedContainerColor = JarvisSurfaceDark,
            unfocusedContainerColor = JarvisSurfaceDark
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("media_prompt_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = {
            if (selectedMode == "ANIME") {
              viewModel.generateAnimeConcept(promptInput)
            } else {
              viewModel.generateVideoScript(promptInput, animeStyle)
            }
          },
          enabled = promptInput.isNotBlank() && !isMediaProcessing,
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("generate_media_button")
        ) {
          if (isMediaProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = JarvisDeepNavy, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("RENDERING AI PRODUCTION PACKAGE...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (selectedMode == "ANIME") "GENERATE CHARACTER & STORYBOARD" else "CREATE VIDEO SCRIPT & SCENE DIRECTIVES",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Production Output Console
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
            text = "PRODUCTION SPECIFICATIONS & OUTPUT",
            color = JarvisCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          if (isMediaProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = JarvisCyan, strokeWidth = 2.dp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (mediaOutput.isNotBlank()) mediaOutput else "Configure prompt and tap generate to produce character reference sheets, scene storyboards, and video rendering plans.",
          color = if (mediaOutput.isNotBlank()) Color(0xFFE2E8F0) else Color(0xFF64748B),
          fontSize = 12.5.sp,
          fontFamily = FontFamily.Monospace,
          lineHeight = 18.sp
        )
      }
    }
  }
}
