package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DailyPlannerScreen(
    dailyPlan: DailyPlanEntity?,
    availableHours: Float,
    onHoursChange: (Float) -> Unit,
    isLoading: Boolean,
    onGeneratePlan: () -> Unit
) {
    val scheduleItems = remember(dailyPlan) {
        dailyPlan?.scheduleItemsJson?.split("#")?.filter { it.isNotBlank() } ?: emptyList()
    }

    var completedIndexes by remember { mutableStateOf(setOf(0, 1)) }

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
                title = "AI Daily Planner & Timeblocker",
                subtitle = "Gemini-optimized schedule based on task priorities, cognitive load & focus windows",
                breadcrumb = "WORKSPACE / DEV PILOT / PLANNER"
            )
        }

        // Available Hours & Generate Plan Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                borderColor = DevPilotCyan.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVAILABLE WORK HOURS TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${"%.1f".format(availableHours)} Hours",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = DevPilotCyan
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = availableHours,
                    onValueChange = onHoursChange,
                    valueRange = 2.0f..10.0f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = DevPilotCyan,
                        activeTrackColor = DevPilotCyan,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("hours_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                DevPilotButton(
                    text = "Generate AI Daily Schedule",
                    icon = Icons.Filled.AutoAwesome,
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    onClick = onGeneratePlan,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "generate_daily_plan_button"
                )
            }
        }

        // High Priority Focus Banner
        if (dailyPlan != null) {
            item {
                DevPilotCard(
                    shape = RoundedCornerShape(8.dp),
                    borderColor = DevPilotViolet.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DevPilotStatusDotBadge(
                        label = "Primary Daily Objective",
                        dotColor = DevPilotViolet
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dailyPlan.highPriorityFocus,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 AI Coach Tip: ${dailyPlan.aiProductivityTip}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Schedule Timeblocks
        item {
            DevPilotSectionHeader(
                title = "TIME-BLOCKED SCHEDULE",
                count = scheduleItems.size,
                actionSlot = {
                    Text(
                        text = "${completedIndexes.size}/${scheduleItems.size} Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = DevPilotSuccess,
                        fontFamily = FontFamily.Monospace
                    )
                }
            )
        }

        if (scheduleItems.isEmpty()) {
            item {
                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Tap 'Generate AI Daily Schedule' above to create today's timeblocks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedDark
                        )
                    }
                }
            }
        } else {
            itemsIndexed(scheduleItems) { index, item ->
                val parts = item.split("|")
                val timeRange = parts.getOrNull(0)?.trim() ?: "09:00 - 10:00"
                val taskName = parts.getOrNull(1)?.trim() ?: item
                val category = parts.getOrNull(2)?.trim() ?: "Coding"
                val isDone = completedIndexes.contains(index)

                val (catColor, catIcon) = when (category.lowercase()) {
                    "coding" -> Pair(DevPilotCyan, Icons.Filled.Code)
                    "review" -> Pair(DevPilotViolet, Icons.Filled.Merge)
                    "testing" -> Pair(DevPilotSuccess, Icons.Filled.CheckCircle)
                    "docs" -> Pair(DevPilotWarning, Icons.Filled.Description)
                    "break" -> Pair(TextMutedDark, Icons.Filled.Coffee)
                    else -> Pair(DevPilotCyan, Icons.Filled.Task)
                }

                DevPilotCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            completedIndexes = if (isDone) completedIndexes - index else completedIndexes + index
                        },
                    borderColor = if (isDone) DevPilotSuccess.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = {
                                completedIndexes = if (isDone) completedIndexes - index else completedIndexes + index
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = DevPilotSuccess,
                                checkmarkColor = Color(0xFF090D16)
                            )
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text(
                                        text = timeRange,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isDone) TextMutedDark else DevPilotCyan,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                DevPilotStatusDotBadge(label = category.uppercase(), dotColor = catColor)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = taskName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (isDone) TextMutedDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
