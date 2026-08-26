package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    repositories: List<RepositoryEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, repoId: String?, repoName: String?, priority: TaskPriority, status: TaskStatus, estMin: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedRepo by remember { mutableStateOf(repositories.firstOrNull()) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TODO) }
    var estimatedMinText by remember { mutableStateOf("60") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "New Development Task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    placeholder = { Text("e.g. Implement JWT authentication") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_desc_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Priority Selection
                Text("Priority", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskPriority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.name, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Estimated Minutes
                OutlinedTextField(
                    value = estimatedMinText,
                    onValueChange = { estimatedMinText = it.filter { c -> c.isDigit() } },
                    label = { Text("Estimated Effort (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    title.trim(),
                                    description.trim(),
                                    selectedRepo?.id,
                                    selectedRepo?.name,
                                    selectedPriority,
                                    selectedStatus,
                                    estimatedMinText.toIntOrNull() ?: 60
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.testTag("save_task_button")
                    ) {
                        Text("Create Task")
                    }
                }
            }
        }
    }
}

@Composable
fun AiDecompositionSheet(
    goal: String,
    onGoalChange: (String) -> Unit,
    subtasks: List<DecomposedSubtask>,
    isLoading: Boolean,
    onDecompose: () -> Unit,
    onConvert: (repoId: String?, repoName: String?) -> Unit,
    onDismiss: () -> Unit,
    repositories: List<RepositoryEntity>
) {
    var selectedRepo by remember { mutableStateOf(repositories.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = VioletSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Task Decomposer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Type any high-level objective and DevPilot will break it down into actionable subtasks with time & priority estimates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = goal,
                    onValueChange = onGoalChange,
                    label = { Text("High-Level Goal / Feature") },
                    placeholder = { Text("e.g. Add OAuth2 login and token rotation") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_decompose_goal_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDecompose,
                    enabled = goal.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_decompose_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = VioletSecondary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Decomposing Goal...")
                    } else {
                        Icon(Icons.Filled.AccountTree, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Decompose with Gemini AI")
                    }
                }

                if (subtasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "GENERATED SUBTASKS (${subtasks.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(subtasks) { sub ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(sub.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                        Text("${sub.estimatedMinutes} min estimated", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                                    }
                                    PriorityBadge(sub.priority)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onConvert(selectedRepo?.id, selectedRepo?.name)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("convert_subtasks_button")
                    ) {
                        Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add All as Active Tasks")
                    }
                }
            }
        }
    }
}
