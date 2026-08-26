package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * DevPilot Geometric Brand Logo & Wordmark.
 * Visual concept: Developer (Code brackets/nodes) + Intelligence (Hexagonal core) + Navigation (Forward vector).
 */
@Composable
fun DevPilotLogoIcon(
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        DevPilotCyan.copy(alpha = 0.6f),
                        DevPilotViolet.copy(alpha = 0.4f)
                    )
                ),
                RoundedCornerShape(size * 0.22f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.12f

            // Left bracket / node vector
            val leftPath = Path().apply {
                moveTo(w * 0.38f, h * 0.15f)
                lineTo(w * 0.15f, h * 0.50f)
                lineTo(w * 0.38f, h * 0.85f)
            }
            drawPath(
                path = leftPath,
                color = DevPilotCyan,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Right bracket / node vector
            val rightPath = Path().apply {
                moveTo(w * 0.62f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.50f)
                lineTo(w * 0.62f, h * 0.85f)
            }
            drawPath(
                path = rightPath,
                color = DevPilotViolet,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Central navigational pilot dot
            drawCircle(
                color = Color.White,
                radius = stroke * 0.9f,
                center = Offset(w * 0.5f, h * 0.5f)
            )
        }
    }
}

@Composable
fun DevPilotWordmark(
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    showTagline: Boolean = false,
    fontSize: Int = 18
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DevPilotLogoIcon(size = iconSize)
        Spacer(modifier = Modifier.width(9.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "DEV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PILOT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize.sp,
                    letterSpacing = 0.5.sp,
                    color = DevPilotCyan
                )
            }
            if (showTagline) {
                Text(
                    text = "INTELLIGENT WORKSPACE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = TextMutedDark
                )
            }
        }
    }
}
