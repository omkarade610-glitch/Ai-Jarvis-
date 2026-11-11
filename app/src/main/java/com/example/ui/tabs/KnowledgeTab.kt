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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.KnowledgeItem
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun KnowledgeTab(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val knowledgeItems by viewModel.knowledgeItems.collectAsState()
  var selectedCategory by remember { mutableStateOf("All") }
  var filterQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }

  val categories = listOf("All", "Science", "Technology", "AI", "Programming", "Android", "Mathematics", "General")

  val filteredItems = knowledgeItems.filter { item ->
    (selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)) &&
        (filterQuery.isBlank() || item.title.contains(filterQuery, ignoreCase = true) || item.content.contains(filterQuery, ignoreCase = true))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisDeepNavy)
      .padding(16.dp)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = "Knowledge Core",
            tint = JarvisCyan,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AI KNOWLEDGE LIBRARY",
            color = JarvisCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }

        Text(
          text = "${knowledgeItems.size} DOCS",
          color = Color(0xFF8B9BB4),
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search field
      OutlinedTextField(
        value = filterQuery,
        onValueChange = { filterQuery = it },
        placeholder = { Text("Filter saved knowledge by title or content...", color = Color(0xFF6B7A94), fontSize = 12.sp) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color(0xFFE2E8F0),
          focusedBorderColor = JarvisCyan,
          unfocusedBorderColor = JarvisCardBorder,
          focusedContainerColor = JarvisSurfaceCard,
          unfocusedContainerColor = JarvisSurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("knowledge_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Category chips
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) JarvisCyan.copy(alpha = 0.25f) else JarvisSurfaceCard)
              .border(1.dp, if (isSelected) JarvisCyan else JarvisCardBorder, RoundedCornerShape(12.dp))
              .clickable { selectedCategory = cat }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = cat,
              color = if (isSelected) JarvisCyan else Color(0xFFB0C0D8),
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Knowledge list
      if (filteredItems.isEmpty()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (knowledgeItems.isEmpty()) "No knowledge documents stored yet." else "No matching items found.",
              color = Color(0xFF64748B),
              fontSize = 13.sp,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Tap the + button to save facts, manuals, or research.",
              color = Color(0xFF475569),
              fontSize = 11.sp
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredItems, key = { it.id }) { item ->
            KnowledgeCard(item = item, onDelete = { viewModel.deleteKnowledge(item.id) })
          }
        }
      }
    }

    // FAB to add
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = JarvisCyan,
      contentColor = JarvisDeepNavy,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .testTag("add_knowledge_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Knowledge Document")
    }

    if (showAddDialog) {
      AddKnowledgeDialog(
        onDismiss = { showAddDialog = false },
        onConfirm = { title, cat, content ->
          viewModel.addKnowledgeDocument(title, cat, content)
          showAddDialog = false
        }
      )
    }
  }
}

@Composable
fun KnowledgeCard(
  item: KnowledgeItem,
  onDelete: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(JarvisSurfaceCard, RoundedCornerShape(14.dp))
      .border(1.dp, JarvisCardBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .background(JarvisCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = item.category.uppercase(),
            color = JarvisCyan,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Knowledge",
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = item.title,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = item.content,
        color = Color(0xFF94A3B8),
        fontSize = 12.5.sp,
        lineHeight = 18.sp,
        maxLines = 5
      )
    }
  }
}

@Composable
fun AddKnowledgeDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, category: String, content: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Technology") }
  var content by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = JarvisSurfaceDark,
    title = {
      Text(
        text = "SAVE KNOWLEDGE TO NEURAL REPO",
        color = JarvisCyan,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Category (e.g. Science, AI, Code)") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Content / Document Text") },
          minLines = 4,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = JarvisCyan
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(title, category, content) },
        enabled = title.isNotBlank() && content.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = JarvisDeepNavy)
      ) {
        Text("SAVE")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = Color.Gray)
      }
    }
  )
}
