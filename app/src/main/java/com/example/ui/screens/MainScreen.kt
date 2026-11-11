package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.tabs.ApkBuilderTab
import com.example.ui.tabs.AssistantTab
import com.example.ui.tabs.DocumentAssistantTab
import com.example.ui.tabs.KnowledgeTab
import com.example.ui.tabs.MediaStudioTab
import com.example.ui.tabs.OwnerTab
import com.example.ui.tabs.SearchTab
import com.example.ui.tabs.StudioTab
import com.example.ui.tabs.ToolsTab
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisTab
import com.example.ui.viewmodel.JarvisViewModel

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MovieCreation

@Composable
fun MainScreen(
  currentUserId: String,
  onSignOut: () -> Unit
) {
  val viewModel: JarvisViewModel = viewModel(key = currentUserId)
  val currentTab by viewModel.currentTab.collectAsState()
  val masterOn by viewModel.masterOn.collectAsState()
  val privateMode by viewModel.privateMode.collectAsState()

  Scaffold(
    containerColor = JarvisDeepNavy,
    contentWindowInsets = WindowInsets.statusBars,
    topBar = {
      // Futuristic HUD Top Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(JarvisSurfaceDark)
          .border(1.dp, JarvisCardBorder)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(if (masterOn) JarvisAccentGlow else Color.Red, CircleShape)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "JARVIS AI",
            color = JarvisCyan,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
          )
          Spacer(modifier = Modifier.weight(1f))
          if (privateMode) {
            Box(
              modifier = Modifier
                .background(Color(0xFF7F1D1D), CircleShape)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("PRIVATE", color = Color(0xFFFCA5A5), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
            text = if (masterOn) "CORE: 100%" else "STANDBY",
            color = if (masterOn) JarvisAccentGlow else Color.Gray,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    },
    bottomBar = {
      val tabs = listOf(
        JarvisTab.ASSISTANT to Icons.Default.SmartToy,
        JarvisTab.SEARCH to Icons.Default.Search,
        JarvisTab.KNOWLEDGE to Icons.AutoMirrored.Filled.MenuBook,
        JarvisTab.STUDIO to Icons.Default.Terminal,
        JarvisTab.MEDIA_STUDIO to Icons.Default.MovieCreation,
        JarvisTab.DOCUMENTS to Icons.Default.Description,
        JarvisTab.APK_BUILDER to Icons.Default.Android,
        JarvisTab.TOOLS to Icons.Default.Build,
        JarvisTab.OWNER to Icons.Default.Shield
      )

      NavigationBar(
        containerColor = JarvisSurfaceDark,
        contentColor = JarvisCyan,
        tonalElevation = 8.dp,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .border(1.dp, JarvisCardBorder)
          .horizontalScroll(rememberScrollState())
      ) {
        tabs.forEach { (tab, icon) ->
          val isSelected = currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { viewModel.setTab(tab) },
            icon = {
              Icon(
                imageVector = icon,
                contentDescription = tab.title,
                modifier = Modifier.size(20.dp)
              )
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = JarvisDeepNavy,
              selectedTextColor = JarvisCyan,
              indicatorColor = JarvisCyan,
              unselectedIconColor = Color(0xFF64748B),
              unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (currentTab) {
        JarvisTab.ASSISTANT -> AssistantTab(viewModel = viewModel)
        JarvisTab.SEARCH -> SearchTab(viewModel = viewModel)
        JarvisTab.KNOWLEDGE -> KnowledgeTab(viewModel = viewModel)
        JarvisTab.STUDIO -> StudioTab(viewModel = viewModel)
        JarvisTab.MEDIA_STUDIO -> MediaStudioTab(viewModel = viewModel)
        JarvisTab.DOCUMENTS -> DocumentAssistantTab(viewModel = viewModel)
        JarvisTab.APK_BUILDER -> ApkBuilderTab(viewModel = viewModel)
        JarvisTab.TOOLS -> ToolsTab(viewModel = viewModel)
        JarvisTab.OWNER -> OwnerTab(viewModel = viewModel, onSignOut = onSignOut)
      }
    }
  }
}
