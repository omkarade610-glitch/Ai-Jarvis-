package com.example.ui.tabs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SearchSource
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanVariant
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun SearchTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var inputQuery by remember { mutableStateOf("") }
  val searchQuery by viewModel.searchQuery.collectAsState()
  val searchAnswer by viewModel.searchAnswer.collectAsState()
  val searchSources by viewModel.searchSources.collectAsState()
  val isSearching by viewModel.isSearching.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisDeepNavy)
      .padding(16.dp)
  ) {
    // Header
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Language,
        contentDescription = "Search Core",
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "LIVE INTERNET SEARCH ENGINE",
        color = JarvisCyan,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputQuery,
        onValueChange = { inputQuery = it },
        placeholder = {
          Text(
            text = "Search global news, research, tech...",
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
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .weight(1f)
          .testTag("search_query_input")
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = { viewModel.executeSearch(inputQuery) },
        enabled = inputQuery.isNotBlank() && !isSearching,
        modifier = Modifier
          .size(50.dp)
          .background(
            if (inputQuery.isNotBlank()) JarvisCyan else Color.DarkGray,
            CircleShape
          )
          .testTag("submit_search_button")
      ) {
        if (isSearching) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = JarvisDeepNavy,
            strokeWidth = 2.dp
          )
        } else {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = JarvisDeepNavy
          )
        }
      }
    }

    // Quick search pills
    Spacer(modifier = Modifier.height(10.dp))
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val trends = listOf(
        "Latest AI breakthroughs 2026",
        "Android 16 developer preview",
        "Quantum computing progress",
        "Space exploration news"
      )
      items(trends) { topic ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(JarvisSurfaceCard)
            .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
            .clickable {
              inputQuery = topic
              viewModel.executeSearch(topic)
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = topic,
            color = Color(0xFF8B9BB4),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Results content
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      if (searchAnswer.isNotBlank()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(JarvisSurfaceCard, RoundedCornerShape(16.dp))
              .border(1.dp, JarvisCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .padding(16.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "SYNTHESIZED INSIGHTS",
                  color = JarvisAccentGlow,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "VERIFIED FACT CHECK",
                  color = JarvisCyan,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = searchAnswer,
                color = Color.White,
                fontSize = 13.5.sp,
                lineHeight = 21.sp
              )
            }
          }
        }
      }

      if (searchSources.isNotEmpty()) {
        item {
          Text(
            text = "AUTHORITATIVE SOURCE CITATIONS",
            color = JarvisCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp)
          )
        }

        items(searchSources) { source ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(JarvisSurfaceDark)
              .border(1.dp, JarvisCardBorder, RoundedCornerShape(12.dp))
              .clickable {
                if (source.url.isNotBlank() && (source.url.startsWith("http://") || source.url.startsWith("https://"))) {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    // Ignore invalid url
                  }
                }
              }
              .padding(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = source.title.ifBlank { "Source Link" },
                  color = JarvisCyan,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                if (source.snippet.isNotBlank()) {
                  Text(
                    text = source.snippet,
                    color = Color(0xFF8B9BB4),
                    fontSize = 11.sp,
                    maxLines = 2
                  )
                }
                Text(
                  text = source.url,
                  color = Color(0xFF4A5D78),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open Source",
                tint = JarvisCyanVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      } else if (searchAnswer.isBlank() && !isSearching) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF334155),
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Enter any topic to fetch real-time grounded intelligence.",
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }
  }
}
