package com.hermes.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.presentation.ui.theme.HermesColors
import kotlinx.coroutines.delay

/**
 * Gesture hint overlay showing animated dots for swipe gestures
 *
 * Displays 3 animated dots with a hint message for first-time users
 */
@Composable
fun GestureHintOverlay(
    message: String = "尝试滑动卡片进行操作",
    durationMs: Long = 3000,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    // Auto-dismiss after duration
    LaunchedEffect(Unit) {
        delay(durationMs)
        isVisible = false
        onDismiss()
    }

    // Animated dots
    val infiniteTransition = rememberInfiniteTransition(label = "gesture_hint")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1_alpha"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, delayMillis = 150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2_alpha"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3_alpha"
    )

    if (isVisible) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = HermesColors.Surface.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Three animated dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .alpha(dot1Alpha)
                            .background(HermesColors.Primary, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .alpha(dot2Alpha)
                            .background(HermesColors.Primary, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .alpha(dot3Alpha)
                            .background(HermesColors.Primary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = message,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = HermesColors.TextSecondary
                )
            }
        }
    }
}