package com.example

import com.example.apk.ApkBuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.KnowledgeItem
import com.example.data.model.ProjectItem
import com.example.data.model.UserMemory
import com.example.ui.viewmodel.JarvisTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JarvisUnitTests {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testJarvisTabs_definitions() {
    assertEquals(9, JarvisTab.values().size)
    assertEquals("Assistant", JarvisTab.ASSISTANT.title)
    assertEquals("Live Search", JarvisTab.SEARCH.title)
    assertEquals("Knowledge", JarvisTab.KNOWLEDGE.title)
    assertEquals("Code Studio", JarvisTab.STUDIO.title)
    assertEquals("Media Studio", JarvisTab.MEDIA_STUDIO.title)
    assertEquals("Doc Assistant", JarvisTab.DOCUMENTS.title)
    assertEquals("APK Builder", JarvisTab.APK_BUILDER.title)
    assertEquals("Device Tools", JarvisTab.TOOLS.title)
    assertEquals("Owner Panel", JarvisTab.OWNER.title)
  }

  @Test
  fun testChatMessage_instantiation() {
    val msg = ChatMessage(
      id = "test-1",
      role = "user",
      content = "System status check",
      language = "en"
    )
    assertEquals("user", msg.role)
    assertEquals("System status check", msg.content)
    assertNotNull(msg.timestamp)
  }

  @Test
  fun testKnowledgeItem_instantiation() {
    val item = KnowledgeItem(
      id = "k-1",
      userId = "user_123",
      title = "Quantum Computing",
      category = "Science",
      content = "Qubits and superposition principles."
    )
    assertEquals("Science", item.category)
    assertEquals("user_123", item.userId)
    assertEquals("Quantum Computing", item.title)
  }

  @Test
  fun testProjectItem_instantiation() {
    val project = ProjectItem(
      id = "p-1",
      userId = "user_123",
      name = "Autonomous Rover",
      type = "android",
      description = "ROS2 based navigation unit",
      status = "In Progress",
      codeSnippet = "// Autonomous control"
    )
    assertEquals("In Progress", project.status)
    assertEquals("android", project.type)
    assertEquals("Autonomous Rover", project.name)
  }

  @Test
  fun testUserMemory_instantiation() {
    val memory = UserMemory(
      id = "m-1",
      userId = "user_123",
      key = "owner_name",
      value = "Commander",
      category = "preference",
      isApproved = true
    )
    assertEquals("owner_name", memory.key)
    assertEquals("Commander", memory.value)
    assertTrue(memory.isApproved)
  }

  @Test
  fun testApkBuildConfig_defaults() {
    val config = ApkBuildConfig(
      appName = "JARVIS AI",
      packageName = "com.aistudio.jarvisai.kxmpzq",
      versionCode = 1,
      versionName = "1.0.0",
      minSdk = 24,
      targetSdk = 36,
      buildType = "debug",
      permissions = listOf("INTERNET", "RECORD_AUDIO", "CAMERA")
    )
    assertEquals("debug", config.buildType)
    assertEquals("JARVIS AI", config.appName)
    assertEquals("com.aistudio.jarvisai.kxmpzq", config.packageName)
    assertEquals(3, config.permissions.size)
  }
}
