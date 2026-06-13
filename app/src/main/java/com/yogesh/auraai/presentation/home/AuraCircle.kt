package com.yogesh.auraai.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
enum class AuraState {
    Idle,
    Thinking
}
@Composable
fun AuraLogo(
    modifier: Modifier = Modifier,
    state: AuraState = AuraState.Idle
) {

    val infiniteTransition =
        rememberInfiniteTransition(label = "aura")

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue =
            if (state == AuraState.Thinking) 1.05f
            else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                if (state == AuraState.Thinking)
                    700
                else
                    2500
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis =
                    if (state == AuraState.Thinking)
                        2500
                    else
                        10000,
                easing = LinearEasing
            )
        ),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (state == AuraState.Thinking) 1.08f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == AuraState.Thinking) 700 else 2500
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Box(
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .size(260.dp)
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                }
        ){

            val radius = size.minDimension * 0.33f

            // Outer glow
            drawCircle(
                color = Color(0xFF8B5CF6).copy(alpha = 0.18f),
                radius = radius + 50f
            )

            drawCircle(
                color = Color(0xFF22D3EE).copy(alpha = 0.08f),
                radius = radius + 75f
            )
            drawCircle(
                color = Color(0xFF8B5CF6).copy(alpha = 0.08f),
                radius = 220f
            )
            // Rotating comet ring
            rotate(rotation) {

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF8B5CF6),
                            Color(0xFFB66CFF),
                            Color.White,
                            Color.White,
                            Color(0xFF22D3EE),
                            Color.Transparent
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = Stroke(
                        width = 42f,
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        radius * 2,
                        radius * 2
                    ),
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    )
                )
            }

            // Dark core
            drawCircle(
                color = Color(0xFF020617),
                radius = radius * 0.60f
            )

            // Left eye glow
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius * 0.10f,
                center = Offset(
                    center.x - radius * 0.26f,
                    center.y - radius * 0.16f
                )
            )

// Left eye
            drawCircle(
                color = Color.White,
                radius = radius * 0.06f,
                center = Offset(
                    center.x - radius * 0.26f,
                    center.y - radius * 0.16f
                )
            )

// Left eye highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = radius * 0.015f,
                center = Offset(
                    center.x - radius * 0.28f,
                    center.y - radius * 0.15f
                )
            )

// Right eye glow
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = radius * 0.10f,
                center = Offset(
                    center.x + radius * 0.26f,
                    center.y - radius * 0.16f
                )
            )

// Right eye
            drawCircle(
                color = Color.White,
                radius = radius * 0.06f,
                center = Offset(
                    center.x + radius * 0.26f,
                    center.y - radius * 0.16f
                )
            )

// Right eye highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = radius * 0.015f,
                center = Offset(
                    center.x + radius * 0.24f,
                    center.y - radius * 0.15f
                )
            )

        }
    }
}