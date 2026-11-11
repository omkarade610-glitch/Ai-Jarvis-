package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisAccentGlow
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanVariant
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicCore(
  modifier: Modifier = Modifier,
  size: Dp = 120.dp,
  isActive: Boolean = true,
  isListening: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "hud_loop")

  val rotationOuter by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation_outer"
  )

  val rotationInner by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(5000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation_inner"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isListening) 600 else 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isListening) 400 else 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow"
  )

  Box(
    modifier = modifier
      .size(size)
      .testTag("holographic_core_hud"),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val center = Offset(size.toPx() / 2, size.toPx() / 2)
      val radius = (size.toPx() / 2) * 0.9f * pulseScale

      // Background radial glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            if (isListening) JarvisAccentGlow.copy(alpha = glowAlpha) else JarvisCyan.copy(alpha = glowAlpha * 0.4f),
            Color.Transparent
          ),
          center = center,
          radius = radius
        ),
        radius = radius,
        center = center
      )

      if (isActive) {
        // Outer segmented ring
        drawArc(
          color = JarvisCyan.copy(alpha = 0.7f),
          startAngle = rotationOuter,
          sweepAngle = 70f,
          useCenter = false,
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
          color = JarvisCyanVariant.copy(alpha = 0.5f),
          startAngle = rotationOuter + 120f,
          sweepAngle = 60f,
          useCenter = false,
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
          color = JarvisCyan.copy(alpha = 0.8f),
          startAngle = rotationOuter + 240f,
          sweepAngle = 80f,
          useCenter = false,
          style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Inner counter-rotating ring
        drawArc(
          color = JarvisAccentGlow.copy(alpha = 0.6f),
          startAngle = rotationInner,
          sweepAngle = 90f,
          useCenter = false,
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
          color = JarvisCyan.copy(alpha = 0.4f),
          startAngle = rotationInner + 180f,
          sweepAngle = 100f,
          useCenter = false,
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Core tick marks
        val tickCount = 12
        for (i in 0 until tickCount) {
          val angle = Math.toRadians((i * (360.0 / tickCount) + rotationOuter).toDouble())
          val startX = center.x + (radius * 0.72f) * cos(angle).toFloat()
          val startY = center.y + (radius * 0.72f) * sin(angle).toFloat()
          val endX = center.x + (radius * 0.82f) * cos(angle).toFloat()
          val endY = center.y + (radius * 0.82f) * sin(angle).toFloat()
          drawLine(
            color = JarvisCyan.copy(alpha = 0.5f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx()
          )
        }
      }

      // Center Core Circle
      drawCircle(
        color = if (isActive) JarvisCyan else Color.Gray,
        radius = radius * 0.28f,
        center = center,
        style = Stroke(width = 2.dp.toPx())
      )
    }

    Text(
      text = if (isListening) "LISTENING" else if (isActive) "JARVIS" else "STANDBY",
      color = if (isListening) JarvisAccentGlow else if (isActive) JarvisCyan else Color.Gray,
      fontSize = (size.value * 0.10f).sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp
    )
  }
}
