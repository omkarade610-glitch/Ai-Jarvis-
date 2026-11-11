package com.example.ai

import android.util.Log
import com.example.data.model.SearchSource
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "JarvisAiService"

class AiAssistantService {

  // Model evaluated lazily at first call
  private val model by lazy {
    Firebase.ai.generativeModel("gemini-3.8-flash")
  }

  suspend fun generateResponse(
    prompt: String,
    systemContext: String = "",
    chatHistory: List<Pair<String, String>> = emptyList()
  ): String {
    return try {
      withTimeout(60_000L) {
        val fullPrompt = buildString {
          appendLine("You are JARVIS AI, an advanced, highly intelligent, futuristic, multilingual personal AI assistant.")
          appendLine("You speak fluent English, Hindi (हिंदी), Marathi (मराठी), and other languages.")
          appendLine("Personality: Professional, proactive, precise, futuristic, polite, and deeply knowledgeable.")
          if (systemContext.isNotBlank()) {
            appendLine("SYSTEM CONTEXT:")
            appendLine(systemContext)
          }
          if (chatHistory.isNotEmpty()) {
            appendLine("CONVERSATION HISTORY:")
            for ((role, text) in chatHistory.takeLast(6)) {
              appendLine("$role: $text")
            }
          }
          appendLine("CURRENT USER REQUEST: $prompt")
          appendLine("JARVIS:")
        }

        val response = model.generateContent(fullPrompt)
        val result = response.text
        if (!result.isNullOrBlank()) {
          result.trim()
        } else {
          "Sir, I have analyzed your query, but the neural network returned an empty response."
        }
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "AI generation timed out", e)
      "Sir, the neural processing timed out after 60 seconds. Please try again or rephrase your request."
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "AI generation failed", e)
      "Protocol alert: ${e.localizedMessage ?: "Unable to contact Gemini neural core. Please verify your connection."}"
    }
  }

  fun streamResponse(
    prompt: String,
    systemContext: String = ""
  ): Flow<String> = flow {
    val fullPrompt = buildString {
      appendLine("You are JARVIS AI, the futuristic personal AI assistant. Support multilingual conversations (English, Hindi, Marathi).")
      if (systemContext.isNotBlank()) {
        appendLine("CONTEXT: $systemContext")
      }
      appendLine("REQUEST: $prompt")
    }

    try {
      withTimeout(60_000L) {
        model.generateContentStream(fullPrompt).collect { chunk ->
          val text = chunk.text.orEmpty()
          if (text.isNotEmpty()) {
            emit(text)
          }
        }
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Streaming timed out", e)
      emit("\n[Processing timed out]")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Streaming failed", e)
      emit("\n[Error: ${e.localizedMessage ?: "Stream interrupted"}]")
    }
  }

  suspend fun searchWebSynthesized(query: String): Pair<String, List<SearchSource>> {
    val prompt = """
      You are JARVIS Live Web Search Engine.
      Analyze and answer this query with worldwide real-time information: "$query".
      Format your response with:
      1. Direct accurate answer & latest factual insights.
      2. Key verification points and dates.
      3. At the end, list 2-3 genuine authoritative sources in this exact format:
      ---SOURCES---
      Title: <Source Title> | URL: <Official URL or Reference> | Snippet: <Brief description>
    """.trimIndent()

    val responseText = generateResponse(prompt)
    val sources = mutableListOf<SearchSource>()
    val cleanAnswer = if (responseText.contains("---SOURCES---")) {
      val parts = responseText.split("---SOURCES---")
      val sourcesPart = parts.getOrNull(1).orEmpty()
      for (line in sourcesPart.lines()) {
        if (line.contains("Title:") && line.contains("URL:")) {
          val title = line.substringAfter("Title:").substringBefore("|").trim()
          val url = line.substringAfter("URL:").substringBefore("|").trim()
          val snippet = line.substringAfter("Snippet:").trim()
          sources.add(SearchSource(title = title, url = url, snippet = snippet))
        }
      }
      parts[0].trim()
    } else {
      responseText
    }

    return Pair(cleanAnswer, sources)
  }

  suspend fun analyzeCodeOrFix(
    language: String,
    code: String,
    action: String
  ): String {
    val prompt = """
      You are the JARVIS AI Coding Studio Engine.
      Language: $language
      Action requested: $action (generate / explain / fix / optimize)
      Code:
      ```$language
      $code
      ```
      Provide high-quality, production-ready output with explanations, fixes, and clean code snippets.
    """.trimIndent()
    return generateResponse(prompt)
  }

  suspend fun generateAppOrWebProject(
    projectType: String,
    description: String
  ): String {
    val prompt = """
      You are JARVIS AI App and Website Studio.
      Project Type: $projectType (Website or Android App)
      Description: $description
      Generate a complete, production-ready, beautiful implementation:
      - If Web: Provide complete standalone HTML with embedded CSS and modern JavaScript.
      - If Android: Provide the Jetpack Compose Kotlin screen, ViewModel, and Gradle configuration.
    """.trimIndent()
    return generateResponse(prompt)
  }

  suspend fun generateAnimeConcept(
    style: String,
    characterPrompt: String
  ): String {
    val prompt = """
      You are the JARVIS Anime & CGI Creation Studio.
      Art Style: $style (e.g. Modern Shonen, Studio Ghibli, Cyberpunk, 3D CGI Movie, Manga Sketch)
      Concept Description: $characterPrompt
      Produce a complete production package:
      1. Character Profile & Visual Reference Sheet (Face, Attire, Colors, Silhouette, Signature Pose)
      2. Scene Storyboard Breakdown (Shot Type, Camera Angle, Lighting, Background Elements)
      3. Dialogue & Voice Direction (Tone, Emotion, Japanese/English/Hindi dub cues)
      4. Detailed Image Prompt (Optimized for generative image models with high fidelity tags)
    """.trimIndent()
    return generateResponse(prompt)
  }

  suspend fun generateVideoStory(
    topic: String,
    style: String
  ): String {
    val prompt = """
      You are the JARVIS AI Video Creation & CGI Movie Director.
      Topic: $topic
      Format/Style: $style (e.g. Cinematic Movie, YouTube Short, Anime Episode, Tech Explainer)
      Create a scene-by-scene production script:
      - Scene 1..N: Visual Scene Description, Camera Motion (Pan, Dolly, Zoom), VFX/CGI instructions.
      - Voiceover / Narrator Script with timestamps.
      - Background Music & Sound Effects (SFX).
      - Transitions and Final MP4 Rendering Parameters.
    """.trimIndent()
    return generateResponse(prompt)
  }

  suspend fun analyzeDocument(
    docTitle: String,
    content: String,
    question: String
  ): String {
    val prompt = """
      You are the JARVIS Document & File Intelligence Assistant.
      Document Name: $docTitle
      Document Content:
      \"\"\"
      $content
      \"\"\"
      User Question / Directive: $question
      Provide a rigorous, structured response:
      1. Executive Summary & Key Takeaways
      2. Direct Answer to the User's Query with cited facts from the text
      3. Actionable Insights or Data Extraction (tabular if applicable)
    """.trimIndent()
    return generateResponse(prompt)
  }
}
