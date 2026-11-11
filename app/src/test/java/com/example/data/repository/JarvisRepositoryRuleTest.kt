package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.ChatSession
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class JarvisRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun addKnowledge_authenticatedUser_createsDocument() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = JarvisRepository(firestore)

    val docId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addKnowledge(
        title = "Neural Networks",
        category = "AI",
        content = "Deep learning architecture analysis."
      )
    }

    assertNotNull(docId)
    assertTrue(docId.isNotEmpty())
  }

  @Test
  fun observeKnowledge_authenticatedOwner_returnsDocuments() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = JarvisRepository(firestore)

    val docId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.addKnowledge(
        title = "Quantum Physics",
        category = "Science",
        content = "Wave particle duality."
      )
    }

    val list = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeKnowledge().first { items -> items.any { it.id == docId } }
    }
    assertTrue(list.any { it.id == docId })
  }

  @Test
  fun unauthenticatedUser_access_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = JarvisRepository(firestore)

    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        repository.addKnowledge("Test", "AI", "Test content")
      }
      fail("Expected exception for unauthenticated write")
    } catch (e: Exception) {
      assertTrue(e is IllegalStateException || e is FirebaseFirestoreException)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@jarvis.test"
    const val BOB_EMAIL = "bob@jarvis.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
