package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    user: UserEntity?,
    tasks: List<TaskEntity>,
    focusSessions: List<FocusSessionEntity>,
    totalFocusMinutes: Int
) {
    val completedCount = tasks.count { it.status == com.example.data.model.TaskStatus.COMPLETED }
    val totalCount = tasks.size
    val completionRate = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            DevPilotPageHeader(
                title = "Developer Productivity Analytics",
                subtitle = "Velocity metrics, cognitive flow tracking & AI engineering benchmarks",
                breadcrumb = "WORKSPACE / DEV PILOT / ANALYTICS"
            )
        }

        // Top Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    title = "VELOCITY SCORE",
                    value = "${user?.productivityScore ?: 89}/100",
                    subtitle = "Top 5% engineer throughput",
                    icon = Icons.Filled.Speed,
                    accentColor = DevPilotCyan,
                    trendText = "↑ 6%",
                    isTrendPositive = true,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "TASK COMPLETION",
                    value = "$completionRate%",
                    subtitle = "$completedCount of $totalCount completed",
                    icon = Icons.Filled.CheckCircle,
                    accentColor = DevPilotSuccess,
                    trendText = "↑ 12%",
                    isTrendPositive = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(
                    title = "DEEP WORK LOGGED",
                    value = "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m",
                    subtitle = "${focusSessions.size} Pomodoro blocks",
                    icon = Icons.Filled.Timer,
                    accentColor = DevPilotWarning,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "ACTIVE STREAK",
                    value = "${user?.codingStreakDays ?: 18} Days",
                    subtitle = "Consistent daily PRs",
                    icon = Icons.Filled.LocalFireDepartment,
                    accentColor = DevPilotViolet,
                    trendText = "Active",
                    isTrendPositive = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Velocity Bar Chart
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WEEKLY TASK & COMMIT VELOCITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                val days = listOf("Mon" to 0.7f, "Tue" to 0.95f, "Wed" to 0.6f, "Thu" to 0.85f, "Fri" to 1.0f, "Sat" to 0.4f, "Sun" to 0.5f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEach { (day, fraction) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height((90 * fraction).dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(if (fraction >= 0.9f) DevPilotCyan else DevPilotViolet)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(day, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextMutedDark, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // AI Coaching & Productivity Insights
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                borderColor = DevPilotCyan.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                DevPilotStatusDotBadge(
                    label = "AI Productivity Recommendations",
                    dotColor = DevPilotCyan
                )

                Spacer(modifier = Modifier.height(10.dp))

                val tips = listOf(
                    "• Peak Energy: You commit most code between 09:30 - 12:00 AM. Schedule critical architecture tasks during this window.",
                    "• Context Switching: Breaking tasks into 45m chunks reduced rework by 32%.",
                    "• Code Review Time: Average review turnaround is 1.4 hours, placing you in the top 10% of team contributors."
                )

                tips.forEach { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }
    }
}
