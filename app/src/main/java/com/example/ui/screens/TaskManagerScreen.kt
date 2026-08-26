package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TaskManagerScreen(
    tasks: List<TaskEntity>,
    repositories: List<RepositoryEntity>,
    onUpdateTaskStatus: (TaskEntity, TaskStatus) -> Unit,
    onDeleteTask: (String) -> Unit,
    onCreateTaskClick: () -> Unit,
    onOpenAiDecomposerClick: () -> Unit
) {
    var viewMode by remember { mutableIntStateOf(0) } // 0: Kanban Board, 1: List View
    var selectedTaskForDetails by remember { mutableStateOf<TaskEntity?>(null) }
    var selectedPriorityFilter by remember { mutableStateOf<TaskPriority?>(null) }

    val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
    val filteredTasks = tasks.filter { selectedPriorityFilter == null || it.priority == selectedPriorityFilter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header & Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            DevPilotPageHeader(
                title = "Tasks & Kanban",
                subtitle = "$completedCount of ${tasks.size} tasks completed • Linear-style developer workflow",
                breadcrumb = "WORKSPACE / DEV PILOT / TASKS",
                actionSlot = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DevPilotButton(
                            text = "Decompose",
                            icon = Icons.Filled.AccountTree,
                            variant = DevPilotButtonVariant.SECONDARY,
                            size = DevPilotButtonSize.SMALL,
                            onClick = onOpenAiDecomposerClick
                        )
                        DevPilotButton(
                            text = "New Task",
                            icon = Icons.Filled.Add,
                            variant = DevPilotButtonVariant.PRIMARY,
                            size = DevPilotButtonSize.SMALL,
                            onClick = onCreateTaskClick,
                            testTag = "add_task_fab"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // View Switcher & Priority Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevPilotSegmentedTabGroup(
                    items = listOf(0, 1),
                    selectedItem = viewMode,
                    onItemSelected = { viewMode = it },
                    labelProvider = { if (it == 0) "Board" else "List" },
                    iconProvider = { if (it == 0) Icons.Filled.ViewKanban else Icons.Filled.List }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (selectedPriorityFilter == null) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { selectedPriorityFilter = null }
                    ) {
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (selectedPriorityFilter == TaskPriority.HIGH || selectedPriorityFilter == TaskPriority.CRITICAL) DevPilotDanger.copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (selectedPriorityFilter == TaskPriority.HIGH || selectedPriorityFilter == TaskPriority.CRITICAL) DevPilotDanger else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable {
                            selectedPriorityFilter = if (selectedPriorityFilter == TaskPriority.HIGH) null else TaskPriority.HIGH
                        }
                    ) {
                        Text(
                            text = "High Priority",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (selectedPriorityFilter == TaskPriority.HIGH) DevPilotDanger else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

        // Content: Kanban Columns or List View
        if (viewMode == 0) {
            // Horizontal Kanban Board
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskStatus.values().forEach { colStatus ->
                    val columnTasks = filteredTasks.filter { it.status == colStatus }
                    KanbanColumn(
                        status = colStatus,
                        tasks = columnTasks,
                        onTaskClick = { selectedTaskForDetails = it },
                        onAdvanceStatus = { task ->
                            val next = when (task.status) {
                                TaskStatus.BACKLOG -> TaskStatus.TODO
                                TaskStatus.TODO -> TaskStatus.IN_PROGRESS
                                TaskStatus.IN_PROGRESS -> TaskStatus.REVIEW
                                TaskStatus.REVIEW -> TaskStatus.COMPLETED
                                TaskStatus.COMPLETED -> TaskStatus.TODO
                            }
                            onUpdateTaskStatus(task, next)
                        }
                    )
                }
            }
        } else {
            // Vertical List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredTasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { selectedTaskForDetails = task },
                        onToggleComplete = {
                            val newStatus = if (task.status == TaskStatus.COMPLETED) TaskStatus.TODO else TaskStatus.COMPLETED
                            onUpdateTaskStatus(task, newStatus)
                        },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }
            }
        }
    }

    // Task Details Modal
    if (selectedTaskForDetails != null) {
        val task = selectedTaskForDetails!!
        AlertDialog(
            onDismissRequest = { selectedTaskForDetails = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PriorityBadge(task.priority)
                }
            },
            text = {
                Column {
                    Text(
                        text = task.description.ifEmpty { "No extra description provided." },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DevPilotFileBadge(path = task.repositoryName ?: "devpilot-core")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${task.estimatedMinutes}m est",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Change Status:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TaskStatus.values().forEach { st ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (task.status == st) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (task.status == st) DevPilotCyan else MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onUpdateTaskStatus(task, st)
                                        selectedTaskForDetails = null
                                    }
                            ) {
                                Text(
                                    text = st.name.take(4),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (task.status == st) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                DevPilotButton(
                    text = "Delete",
                    variant = DevPilotButtonVariant.DESTRUCTIVE,
                    size = DevPilotButtonSize.SMALL,
                    onClick = {
                        onDeleteTask(task.id)
                        selectedTaskForDetails = null
                    }
                )
            },
            dismissButton = {
                DevPilotButton(
                    text = "Close",
                    variant = DevPilotButtonVariant.OUTLINE,
                    size = DevPilotButtonSize.SMALL,
                    onClick = { selectedTaskForDetails = null }
                )
            }
        )
    }
}

@Composable
fun KanbanColumn(
    status: TaskStatus,
    tasks: List<TaskEntity>,
    onTaskClick: (TaskEntity) -> Unit,
    onAdvanceStatus: (TaskEntity) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(270.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Column Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "${tasks.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tasks) { task ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick(task) },
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.repositoryName ?: "devpilot-core",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DevPilotCyan,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                                PriorityBadge(task.priority)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${task.estimatedMinutes}m est",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                IconButton(
                                    onClick = { onAdvanceStatus(task) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Advance Status",
                                        tint = DevPilotCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED

    DevPilotCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = DevPilotSuccess,
                    checkmarkColor = Color(0xFF090D16)
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isCompleted) TextMutedDark else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.repositoryName ?: "devpilot-core",
                        style = MaterialTheme.typography.labelSmall,
                        color = DevPilotCyan,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${task.estimatedMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            PriorityBadge(task.priority)
        }
    }
}
