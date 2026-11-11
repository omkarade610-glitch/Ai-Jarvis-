package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val JarvisDarkColorScheme = darkColorScheme(
  primary = JarvisCyan,
  onPrimary = JarvisDeepNavy,
  primaryContainer = JarvisSurfaceElevated,
  onPrimaryContainer = JarvisCyan,
  secondary = JarvisBlue,
  onSecondary = Color.White,
  secondaryContainer = JarvisSurfaceCard,
  onSecondaryContainer = JarvisCyanVariant,
  tertiary = JarvisAccentGlow,
  onTertiary = JarvisDeepNavy,
  background = JarvisDeepNavy,
  onBackground = JarvisTextPrimary,
  surface = JarvisSurfaceDark,
  onSurface = JarvisTextPrimary,
  surfaceVariant = JarvisSurfaceCard,
  onSurfaceVariant = JarvisTextSecondary,
  outline = JarvisCardBorder,
  error = JarvisRed,
  onError = Color.White
)

private val JarvisLightColorScheme = lightColorScheme(
  primary = JarvisBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD8E6FF),
  onPrimaryContainer = Color(0xFF001B3F),
  secondary = JarvisCyanVariant,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFC7F0FF),
  onSecondaryContainer = Color(0xFF001E2E),
  tertiary = JarvisCyan,
  onTertiary = JarvisDeepNavy,
  background = Color(0xFFF4F7FC),
  onBackground = Color(0xFF101828),
  surface = Color.White,
  onSurface = Color(0xFF101828),
  surfaceVariant = Color(0xFFE8EEF8),
  onSurfaceVariant = Color(0xFF475467),
  outline = Color(0xFFCBD5E1),
  error = JarvisRed,
  onError = Color.White
)

@Composable
fun JarvisTheme(
  darkTheme: Boolean = true, // Futuristic dark theme default for JARVIS
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> JarvisDarkColorScheme
    else -> JarvisLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  JarvisTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
