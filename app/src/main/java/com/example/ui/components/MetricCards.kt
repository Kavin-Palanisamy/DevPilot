package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.ui.theme.*

@Composable
fun PriorityBadge(priority: TaskPriority) {
    val (dotColor, label) = when (priority) {
        TaskPriority.CRITICAL -> Pair(DevPilotDanger, "Critical")
        TaskPriority.HIGH -> Pair(DevPilotWarning, "High")
        TaskPriority.MEDIUM -> Pair(DevPilotCyan, "Medium")
        TaskPriority.LOW -> Pair(DevPilotSuccess, "Low")
    }

    DevPilotStatusDotBadge(
        label = label,
        dotColor = dotColor
    )
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val (dotColor, label) = when (status) {
        TaskStatus.BACKLOG -> Pair(TextMutedDark, "Backlog")
        TaskStatus.TODO -> Pair(DevPilotCyan, "Todo")
        TaskStatus.IN_PROGRESS -> Pair(DevPilotViolet, "In Progress")
        TaskStatus.REVIEW -> Pair(DevPilotWarning, "In Review")
        TaskStatus.COMPLETED -> Pair(DevPilotSuccess, "Done")
    }

    DevPilotStatusDotBadge(
        label = label,
        dotColor = dotColor
    )
}

@Composable
fun LanguageDot(language: String, colorHex: String = "#00E5FF") {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        DevPilotCyan
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(parsedColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}

@Composable
fun HealthScorePill(score: Int) {
    val color = when {
        score >= 85 -> DevPilotSuccess
        score >= 70 -> DevPilotWarning
        else -> DevPilotDanger
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$score/100",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun StatTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    trendText: String? = null,
    isTrendPositive: Boolean = true
) {
    DevPilotMetricTile(
        title = title,
        value = value,
        subtitle = subtitle,
        icon = icon,
        accentColor = accentColor,
        modifier = modifier,
        trendText = trendText,
        isTrendPositive = isTrendPositive
    )
}
