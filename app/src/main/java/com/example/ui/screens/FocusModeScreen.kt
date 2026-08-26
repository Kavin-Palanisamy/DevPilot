package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun FocusModeScreen(
    remainingSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    selectedTask: TaskEntity?,
    tasks: List<TaskEntity>,
    onSelectTask: (TaskEntity?) -> Unit,
    onSetDuration: (Int) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onFinishEarly: () -> Unit,
    focusSessions: List<FocusSessionEntity>,
    totalFocusMinutes: Int
) {
    var showTaskPicker by remember { mutableStateOf(false) }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "focus_progress")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            DevPilotPageHeader(
                title = "Deep Work Focus",
                subtitle = "Distraction-free flow timer • Session logs automatically sync to productivity score",
                breadcrumb = "WORKSPACE / DEV PILOT / FOCUS"
            )
        }

        // Active Task Selector Card
        item {
            DevPilotCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTaskPicker = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DevPilotCyan.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.CenterFocusStrong, contentDescription = null, tint = DevPilotCyan, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ACTIVE FOCUS TARGET", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextMutedDark)
                            Text(
                                text = selectedTask?.title ?: "Deep Work (Unlinked Session)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                    Text("Change →", style = MaterialTheme.typography.labelSmall, color = DevPilotCyan)
                }
            }
        }

        // Circular Timer Display
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    // Background track
                    drawCircle(
                        color = Color(0xFF1B2232),
                        style = Stroke(width = strokeWidth)
                    )
                    // Animated Arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(DevPilotCyan, DevPilotViolet, DevPilotCyan)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DevPilotStatusDotBadge(
                        label = if (isRunning) "FLOW STATE ACTIVE" else "PAUSED",
                        dotColor = if (isRunning) DevPilotSuccess else DevPilotWarning
                    )
                }
            }
        }

        // Timer Controls (Start/Pause, Reset, Finish Early)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevPilotButton(
                    text = "Reset",
                    icon = Icons.Filled.Replay,
                    variant = DevPilotButtonVariant.OUTLINE,
                    size = DevPilotButtonSize.MEDIUM,
                    onClick = onResetTimer
                )

                DevPilotButton(
                    text = if (isRunning) "Pause" else "Start Focus",
                    icon = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    variant = if (isRunning) DevPilotButtonVariant.SECONDARY else DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.LARGE,
                    onClick = onToggleTimer,
                    testTag = "toggle_focus_timer"
                )

                DevPilotButton(
                    text = "Finish",
                    icon = Icons.Filled.Check,
                    variant = DevPilotButtonVariant.OUTLINE,
                    size = DevPilotButtonSize.MEDIUM,
                    onClick = onFinishEarly
                )
            }
        }

        // Preset Duration Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val presets = listOf(15, 25, 45, 60)
                    presets.forEach { mins ->
                        val isSelected = totalSeconds == mins * 60
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.clickable { onSetDuration(mins) }
                        ) {
                            Text(
                                text = "${mins}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Session Stats & History
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("COMPLETED FOCUS LOG", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Total: ${totalFocusMinutes}m", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = DevPilotSuccess, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (focusSessions.isEmpty()) {
                    Text("No sessions completed today yet. Start a 25m focus block above.", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                } else {
                    focusSessions.take(4).forEach { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Timer, contentDescription = null, tint = DevPilotWarning, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(session.taskTitle ?: "Deep Work", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DevPilotSuccess.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, DevPilotSuccess.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "+${session.durationMinutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DevPilotSuccess,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Task Picker Dialog
    if (showTaskPicker) {
        AlertDialog(
            onDismissRequest = { showTaskPicker = false },
            title = { Text("Link Focus Session to Task", style = MaterialTheme.typography.titleMedium) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectTask(null)
                                    showTaskPicker = false
                                }
                        ) {
                            Text("Unlinked General Deep Work", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    items(tasks) { task ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectTask(task)
                                    showTaskPicker = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(task.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                Text(task.repositoryName ?: "devpilot-core", style = MaterialTheme.typography.labelSmall, color = DevPilotCyan, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                DevPilotButton(
                    text = "Cancel",
                    variant = DevPilotButtonVariant.OUTLINE,
                    size = DevPilotButtonSize.SMALL,
                    onClick = { showTaskPicker = false }
                )
            }
        )
    }
}
