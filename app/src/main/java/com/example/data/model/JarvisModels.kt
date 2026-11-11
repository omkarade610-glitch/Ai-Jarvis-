package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class ChatSession(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val lastMessage: String = "",
  val messageCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

data class SearchSource(
  val title: String = "",
  val url: String = "",
  val snippet: String = ""
)

data class ChatMessage(
  val id: String = "",
  val role: String = "assistant", // "user", "assistant"
  val content: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val language: String = "en",
  val isVoice: Boolean = false,
  val searchSources: List<SearchSource> = emptyList()
)

@IgnoreExtraProperties
data class KnowledgeItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val category: String = "Science",
  val content: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ProjectItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val type: String = "android", // "web", "android", "script", "apk"
  val description: String = "",
  val status: String = "draft",
  val codeSnippet: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserMemory(
  @DocumentId val id: String = "",
  val userId: String = "",
  val key: String = "",
  val value: String = "",
  val category: String = "preference",
  val isApproved: Boolean = true,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)
