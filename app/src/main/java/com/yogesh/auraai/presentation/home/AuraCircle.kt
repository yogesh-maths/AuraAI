package com.yogesh.auraai.presentation.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AuraState {
    Idle,
    Thinking
}
@Composable
fun AuraCircle(
    state: AuraState = AuraState.Idle,
    modifier: Modifier = Modifier
){

    val infiniteTransition = rememberInfiniteTransition(
        label = "aura"
    )
    val targetRadius =
        if (state == AuraState.Thinking) 190f else 160f
    val duration =
        if (state == AuraState.Thinking) 700 else 2000
    val radius by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = targetRadius,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Canvas(
        modifier = modifier
            .size(300.dp)
    ) {

        drawCircle(
            color = Color(0xFF19D3F3).copy(alpha = 0.08f),
            radius = radius + 75f
        )

        drawCircle(
            color = Color(0xFF4F8CFF).copy(alpha = 0.18f),
            radius = radius + 50f
        )

        drawCircle(
            color = Color(0xFF7B61FF).copy(alpha = 0.28f),
            radius = radius + 25f
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF22D3EE),
                    Color(0xFF3B82F6),
                    Color(0xFF8B5CF6)
                )
            ),
            radius = radius
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = radius * 0.25f
        )
    }

}