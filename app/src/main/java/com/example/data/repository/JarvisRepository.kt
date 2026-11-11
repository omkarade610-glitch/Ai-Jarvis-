package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ChatSession
import com.example.data.model.KnowledgeItem
import com.example.data.model.ProjectItem
import com.example.data.model.UserMemory
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class JarvisRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  // --- CHATS ---
  fun observeChats(): Flow<List<ChatSession>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/chats"
    emitAll(
      db.collection("users").document(uid).collection("chats")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(ChatSession::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveChat(chat: ChatSession): String {
    val uid = requireUserId()
    val docRef = if (chat.id.isNotBlank()) {
      db.collection("users").document(uid).collection("chats").document(chat.id)
    } else {
      db.collection("users").document(uid).collection("chats").document()
    }
    val path = docRef.path

    val isNew = chat.id.isBlank()
    try {
      if (isNew) {
        val payload = mapOf(
          "userId" to uid,
          "title" to chat.title.ifBlank { "New Conversation" },
          "lastMessage" to chat.lastMessage,
          "messageCount" to chat.messageCount,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
      } else {
        val payload = mapOf(
          "title" to chat.title,
          "lastMessage" to chat.lastMessage,
          "messageCount" to chat.messageCount,
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(payload).await()
      }
      return docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, if (isNew) OperationType.CREATE else OperationType.UPDATE, path)
      throw e
    }
  }

  suspend fun deleteChat(chatId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("chats").document(chatId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- KNOWLEDGE ---
  fun observeKnowledge(): Flow<List<KnowledgeItem>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/knowledge"
    emitAll(
      db.collection("users").document(uid).collection("knowledge")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(KnowledgeItem::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun addKnowledge(title: String, category: String, content: String): String {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("knowledge").document()
    val payload = mapOf(
      "userId" to uid,
      "title" to title,
      "category" to category,
      "content" to content,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      return docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteKnowledge(docId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("knowledge").document(docId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- PROJECTS ---
  fun observeProjects(): Flow<List<ProjectItem>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/projects"
    emitAll(
      db.collection("users").document(uid).collection("projects")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(ProjectItem::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveProject(project: ProjectItem): String {
    val uid = requireUserId()
    val docRef = if (project.id.isNotBlank()) {
      db.collection("users").document(uid).collection("projects").document(project.id)
    } else {
      db.collection("users").document(uid).collection("projects").document()
    }
    val isNew = project.id.isBlank()
    try {
      if (isNew) {
        val payload = mapOf(
          "userId" to uid,
          "name" to project.name,
          "type" to project.type,
          "description" to project.description,
          "status" to project.status,
          "codeSnippet" to project.codeSnippet,
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
      } else {
        val payload = mapOf(
          "name" to project.name,
          "type" to project.type,
          "description" to project.description,
          "status" to project.status,
          "codeSnippet" to project.codeSnippet,
          "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(payload).await()
      }
      return docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, if (isNew) OperationType.CREATE else OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteProject(projectId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("projects").document(projectId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- MEMORIES ---
  fun observeMemories(): Flow<List<UserMemory>> = flow {
    val uid = requireUserId()
    val path = "users/$uid/memories"
    emitAll(
      db.collection("users").document(uid).collection("memories")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(UserMemory::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun addMemory(key: String, value: String, category: String): String {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("memories").document()
    val payload = mapOf(
      "userId" to uid,
      "key" to key,
      "value" to value,
      "category" to category,
      "isApproved" to true,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      return docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteMemory(memoryId: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("memories").document(memoryId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }
}
