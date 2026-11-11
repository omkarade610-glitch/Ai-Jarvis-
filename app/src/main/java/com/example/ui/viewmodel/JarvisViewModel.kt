package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAssistantService
import com.example.apk.ApkBuildConfig
import com.example.apk.ApkBuildRecord
import com.example.apk.ApkBuilderManager
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.KnowledgeItem
import com.example.data.model.ProjectItem
import com.example.data.model.SearchSource
import com.example.data.model.UserMemory
import com.example.data.repository.JarvisRepository
import com.example.device.DeviceAssistantHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class JarvisTab(val title: String, val iconName: String) {
  ASSISTANT("Assistant", "SmartToy"),
  SEARCH("Live Search", "Search"),
  KNOWLEDGE("Knowledge", "MenuBook"),
  STUDIO("Code Studio", "Terminal"),
  MEDIA_STUDIO("Media Studio", "MovieCreation"),
  DOCUMENTS("Doc Assistant", "Description"),
  APK_BUILDER("APK Builder", "Android"),
  TOOLS("Device Tools", "Build"),
  OWNER("Owner Panel", "Shield")
}

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = JarvisRepository(application.applicationContext)
  private val aiService = AiAssistantService()
  val deviceHelper = DeviceAssistantHelper(application.applicationContext)
  private val apkManager = ApkBuilderManager(application.applicationContext)

  private val _currentTab = MutableStateFlow(JarvisTab.ASSISTANT)
  val currentTab: StateFlow<JarvisTab> = _currentTab.asStateFlow()

  // Chat State
  private val _messages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        id = "welcome",
        role = "assistant",
        content = "Good day, sir. JARVIS AI online. Neural core linked to Gemini 3.8. All sub-systems including Live Search, Knowledge Library, Coding Studio, and Android APK Builder are fully operational. How may I assist you today?",
        language = "en"
      )
    )
  )
  val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

  private val _chatInput = MutableStateFlow("")
  val chatInput: StateFlow<String> = _chatInput.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  private val _selectedLanguage = MutableStateFlow("English")
  val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

  // Master switches & Privacy
  private val _masterOn = MutableStateFlow(true)
  val masterOn: StateFlow<Boolean> = _masterOn.asStateFlow()

  private val _voiceListening = MutableStateFlow(false)
  val voiceListening: StateFlow<Boolean> = _voiceListening.asStateFlow()

  private val _privateMode = MutableStateFlow(false)
  val privateMode: StateFlow<Boolean> = _privateMode.asStateFlow()

  // Search State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _searchAnswer = MutableStateFlow("")
  val searchAnswer: StateFlow<String> = _searchAnswer.asStateFlow()

  private val _searchSources = MutableStateFlow<List<SearchSource>>(emptyList())
  val searchSources: StateFlow<List<SearchSource>> = _searchSources.asStateFlow()

  private val _isSearching = MutableStateFlow(false)
  val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

  // Studio Code State
  private val _codeLang = MutableStateFlow("Kotlin")
  val codeLang: StateFlow<String> = _codeLang.asStateFlow()

  private val _codeText = MutableStateFlow(
    """// JARVIS Android Jetpack Compose Component
@Composable
fun HolographicCore(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(140.dp)
            .background(Color(0xFF00E5FF).copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("JARVIS CORE", color = Color(0xFF00E5FF))
    }
}"""
  )
  val codeText: StateFlow<String> = _codeText.asStateFlow()

  private val _codeOutput = MutableStateFlow("")
  val codeOutput: StateFlow<String> = _codeOutput.asStateFlow()

  private val _isCodeRunning = MutableStateFlow(false)
  val isCodeRunning: StateFlow<Boolean> = _isCodeRunning.asStateFlow()

  // APK Builder State
  private val _apkConfig = MutableStateFlow(ApkBuildConfig())
  val apkConfig: StateFlow<ApkBuildConfig> = _apkConfig.asStateFlow()

  private val _apkRecords = MutableStateFlow<List<ApkBuildRecord>>(emptyList())
  val apkRecords: StateFlow<List<ApkBuildRecord>> = _apkRecords.asStateFlow()

  // Knowledge and Memory streams from Firestore
  val knowledgeItems: StateFlow<List<KnowledgeItem>> = repository.observeKnowledge()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userProjects: StateFlow<List<ProjectItem>> = repository.observeProjects()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userMemories: StateFlow<List<UserMemory>> = repository.observeMemories()
    .catch { emit(emptyList()) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Media & Anime Studio State
  private val _animeStyle = MutableStateFlow("Cyberpunk Anime")
  val animeStyle: StateFlow<String> = _animeStyle.asStateFlow()

  private val _mediaOutput = MutableStateFlow("")
  val mediaOutput: StateFlow<String> = _mediaOutput.asStateFlow()

  private val _isMediaProcessing = MutableStateFlow(false)
  val isMediaProcessing: StateFlow<Boolean> = _isMediaProcessing.asStateFlow()

  // Document Assistant State
  private val _docTitle = MutableStateFlow("System_Specification.txt")
  val docTitle: StateFlow<String> = _docTitle.asStateFlow()

  private val _docContent = MutableStateFlow(
    """JARVIS System Architecture Spec: Version 1.0.0.
Neural Core: Google Gemini 3.8 Flash.
Data Engine: Zero-Trust Firebase Firestore with Google Sign-In.
Security: Firebase App Check with reCAPTCHA Enterprise & Debug attestation.
Mobile OS: Android 14/15/16 with Jetpack Compose edge-to-edge UI.
Build Pipeline: Cloud Gradle build system for Debug APK, Release APK, and AAB."""
  )
  val docContent: StateFlow<String> = _docContent.asStateFlow()

  private val _docQuestion = MutableStateFlow("Summarize the neural core and security specs.")
  val docQuestion: StateFlow<String> = _docQuestion.asStateFlow()

  private val _docAnalysis = MutableStateFlow("")
  val docAnalysis: StateFlow<String> = _docAnalysis.asStateFlow()

  private val _isDocAnalyzing = MutableStateFlow(false)
  val isDocAnalyzing: StateFlow<Boolean> = _isDocAnalyzing.asStateFlow()

  fun setTab(tab: JarvisTab) {
    _currentTab.value = tab
  }

  fun updateChatInput(text: String) {
    _chatInput.value = text
  }

  fun setLanguage(lang: String) {
    _selectedLanguage.value = lang
  }

  fun toggleMasterSwitch() {
    _masterOn.value = !_masterOn.value
    if (!_masterOn.value) {
      _voiceListening.value = false
      deviceHelper.stopSpeaking()
    }
  }

  fun toggleVoiceListening() {
    if (!_masterOn.value) return
    _voiceListening.value = !_voiceListening.value
  }

  fun togglePrivateMode() {
    _privateMode.value = !_privateMode.value
  }

  fun sendMessage(userText: String? = null) {
    val text = (userText ?: _chatInput.value).trim()
    if (text.isBlank() || !_masterOn.value) return

    _chatInput.value = ""
    val userMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      role = "user",
      content = text,
      language = _selectedLanguage.value
    )
    _messages.value = _messages.value + userMsg

    viewModelScope.launch {
      _isAiThinking.value = true
      try {
        // Collect approved memories as context
        val memoryContext = if (!_privateMode.value) {
          userMemories.value.joinToString("\n") { "- ${it.key}: ${it.value}" }
        } else {
          "[PRIVATE MODE ENGAGED - DO NOT LOG MEMORIES]"
        }

        val history = _messages.value.takeLast(6).map { it.role to it.content }
        val response = aiService.generateResponse(
          prompt = text,
          systemContext = "Preferred language: ${_selectedLanguage.value}\nKnown user preferences:\n$memoryContext",
          chatHistory = history
        )

        val assistantMsg = ChatMessage(
          id = UUID.randomUUID().toString(),
          role = "assistant",
          content = response,
          language = _selectedLanguage.value
        )
        _messages.value = _messages.value + assistantMsg

        // If voice listening is on, speak response
        if (_voiceListening.value) {
          deviceHelper.speak(response, _selectedLanguage.value)
        }

        // Save session preview to Firestore unless private mode
        if (!_privateMode.value) {
          try {
            repository.saveChat(
              ChatSession(
                title = text.take(30),
                lastMessage = response.take(100),
                messageCount = _messages.value.size.toLong()
              )
            )
          } catch (e: Exception) {
            // Firestore log already handled
          }
        }
      } catch (e: Exception) {
        val errorMsg = ChatMessage(
          id = UUID.randomUUID().toString(),
          role = "assistant",
          content = "System error: ${e.localizedMessage ?: "Neural computation failure."}"
        )
        _messages.value = _messages.value + errorMsg
      } finally {
        _isAiThinking.value = false
      }
    }
  }

  fun executeSearch(query: String) {
    if (query.isBlank() || !_masterOn.value) return
    _searchQuery.value = query
    _isSearching.value = true
    viewModelScope.launch {
      try {
        val (answer, sources) = aiService.searchWebSynthesized(query)
        _searchAnswer.value = answer
        _searchSources.value = sources
      } catch (e: Exception) {
        _searchAnswer.value = "Failed to query real-time data: ${e.localizedMessage}"
        _searchSources.value = emptyList()
      } finally {
        _isSearching.value = false
      }
    }
  }

  fun addKnowledgeDocument(title: String, category: String, content: String) {
    if (title.isBlank() || content.isBlank()) return
    viewModelScope.launch {
      try {
        repository.addKnowledge(title, category, content)
      } catch (e: Exception) {
        // error logged in repository
      }
    }
  }

  fun deleteKnowledge(id: String) {
    viewModelScope.launch {
      try {
        repository.deleteKnowledge(id)
      } catch (e: Exception) {
        // error logged
      }
    }
  }

  fun setCodeLanguage(lang: String) {
    _codeLang.value = lang
  }

  fun updateCodeText(code: String) {
    _codeText.value = code
  }

  fun runCodeAction(action: String) {
    val code = _codeText.value
    if (code.isBlank() || !_masterOn.value) return
    _isCodeRunning.value = true
    viewModelScope.launch {
      try {
        val output = aiService.analyzeCodeOrFix(_codeLang.value, code, action)
        _codeOutput.value = output
      } catch (e: Exception) {
        _codeOutput.value = "Error executing $action: ${e.localizedMessage}"
      } finally {
        _isCodeRunning.value = false
      }
    }
  }

  fun saveProject(name: String, type: String, description: String, snippet: String) {
    viewModelScope.launch {
      try {
        repository.saveProject(
          ProjectItem(
            name = name,
            type = type,
            description = description,
            codeSnippet = snippet
          )
        )
      } catch (e: Exception) {
        // error logged
      }
    }
  }

  fun triggerApkBuild() {
    val record = apkManager.executeLocalValidation(_apkConfig.value)
    _apkRecords.value = listOf(record) + _apkRecords.value
  }

  fun updateApkConfig(
    appName: String,
    packageName: String,
    versionName: String,
    versionCode: Int,
    buildType: String
  ) {
    _apkConfig.value = _apkConfig.value.copy(
      appName = appName,
      packageName = packageName,
      versionName = versionName,
      versionCode = versionCode,
      buildType = buildType
    )
  }

  fun addMemory(key: String, value: String, category: String) {
    viewModelScope.launch {
      try {
        repository.addMemory(key, value, category)
      } catch (e: Exception) {
        // error logged
      }
    }
  }

  fun deleteMemory(id: String) {
    viewModelScope.launch {
      try {
        repository.deleteMemory(id)
      } catch (e: Exception) {
        // error logged
      }
    }
  }

  fun setAnimeStyle(style: String) {
    _animeStyle.value = style
  }

  fun generateAnimeConcept(prompt: String) {
    if (prompt.isBlank() || !_masterOn.value) return
    _isMediaProcessing.value = true
    viewModelScope.launch {
      try {
        _mediaOutput.value = aiService.generateAnimeConcept(_animeStyle.value, prompt)
      } catch (e: Exception) {
        _mediaOutput.value = "Anime generation error: ${e.localizedMessage}"
      } finally {
        _isMediaProcessing.value = false
      }
    }
  }

  fun generateVideoScript(topic: String, format: String) {
    if (topic.isBlank() || !_masterOn.value) return
    _isMediaProcessing.value = true
    viewModelScope.launch {
      try {
        _mediaOutput.value = aiService.generateVideoStory(topic, format)
      } catch (e: Exception) {
        _mediaOutput.value = "Video script generation error: ${e.localizedMessage}"
      } finally {
        _isMediaProcessing.value = false
      }
    }
  }

  fun updateDocTitle(title: String) {
    _docTitle.value = title
  }

  fun updateDocContent(content: String) {
    _docContent.value = content
  }

  fun updateDocQuestion(question: String) {
    _docQuestion.value = question
  }

  fun analyzeDocument() {
    val title = _docTitle.value
    val content = _docContent.value
    val question = _docQuestion.value
    if (content.isBlank() || !_masterOn.value) return
    _isDocAnalyzing.value = true
    viewModelScope.launch {
      try {
        _docAnalysis.value = aiService.analyzeDocument(title, content, question)
      } catch (e: Exception) {
        _docAnalysis.value = "Document analysis failed: ${e.localizedMessage}"
      } finally {
        _isDocAnalyzing.value = false
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    deviceHelper.shutdown()
  }
}
