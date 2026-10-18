package com.gerasimov_mobile_dev.techstore.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedSkeletonBox(
    modifier: Modifier,
    cornerRadius: Dp = 0.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing , delayMillis = 100),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val baseColor = Color.Gray.copy(alpha = 0.2f)
    val highlightColor = Color.Gray.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(baseColor)
            .drawWithCache {
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        highlightColor,
                        Color.Transparent
                    ),
                    start = Offset(translateAnim, 0f),
                    end = Offset(translateAnim + 800f, 400f)
                )
                onDrawBehind {
                    drawRect(brush = brush)
                }
            }
    )
}

@Composable
fun SkeletonBox(
    modifier: Modifier,
    cornerRadius: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(cornerRadius))
    )
}

@Composable
fun AnimatedSkeletonRow(
    height: Dp,
    count: Int,
    gap: Dp = 8.dp,
    lastCornerRadius: Dp? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        repeat(count) { index ->
            val corner =
                if (index == count - 1 && lastCornerRadius != null) lastCornerRadius else 4.dp
            AnimatedSkeletonBox(
                modifier = Modifier
                    .height(height)
                    .weight(1f),
                cornerRadius = corner
            )
        }
    }
}