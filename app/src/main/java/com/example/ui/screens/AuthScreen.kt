package com.example.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.components.HolographicCore
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCardBorder
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceDark
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(JarvisDeepNavy, JarvisSurfaceDark, JarvisDeepNavy)
        )
      )
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 480.dp)
        .fillMaxWidth()
        .background(JarvisSurfaceCard.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
        .border(1.dp, JarvisCardBorder, RoundedCornerShape(24.dp))
        .padding(horizontal = 24.dp, vertical = 36.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      HolographicCore(size = 130.dp, isActive = true)

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "JARVIS AI",
        color = JarvisCyan,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 2.sp
      )

      Text(
        text = "PERSONAL ARTIFICIAL INTELLIGENCE",
        color = Color(0xFF8B9BB4),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Protocol 10-A: Secure authorization required to access AI Neural Core, Knowledge Library, and System Tools.",
        color = Color(0xFFB0C0D8),
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        lineHeight = 18.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      if (errorMessage != null) {
        Text(
          text = errorMessage!!,
          color = MaterialTheme.colorScheme.error,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        )
      }

      Button(
        onClick = {
          isLoading = true
          errorMessage = null
          val clientId = try {
            context.getString(R.string.default_web_client_id)
          } catch (e: Exception) {
            errorMessage = "Google Web Client ID missing."
            isLoading = false
            return@Button
          }

          val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
          val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

          coroutineScope.launch {
            try {
              val result = credentialManager.getCredential(context as Activity, request)
              val credential = result.credential
              if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                isLoading = false
                onAuthSuccess()
              } else {
                errorMessage = "Unexpected credential response"
                isLoading = false
              }
            } catch (e: GetCredentialCancellationException) {
              Log.w("Auth", "Google Sign-In flow cancelled: ${e.message}", e)
              isLoading = false
            } catch (e: Exception) {
              Log.e("Auth", "Sign-In error", e)
              errorMessage = e.localizedMessage ?: "Sign-in authorization failed"
              isLoading = false
            }
          }
        },
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
          containerColor = JarvisCyan,
          contentColor = JarvisDeepNavy
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("google_sign_in_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = JarvisDeepNavy,
            strokeWidth = 2.dp
          )
        } else {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Security Shield",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.size(8.dp))
          Text(
            text = "AUTHORIZE WITH GOOGLE",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Cloud Firestore Zero-Trust • Gemini 3.8 Flash Neural Core",
        color = Color(0xFF5E6D88),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}
