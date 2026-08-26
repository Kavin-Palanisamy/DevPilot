package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RepositoryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.WorkflowStage
import com.example.ui.theme.*

data class CommandAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val stage: WorkflowStage? = null,
    val screen: String? = null
)

/**
 * DevPilot ⌘K Global Command Palette.
 */
@Composable
fun GlobalSearchDialog(
    query: String,
    onQueryChange: (String) -> Unit,
    tasks: List<TaskEntity>,
    repositories: List<RepositoryEntity>,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onSelectRepo: (String) -> Unit,
    onNavigateToStage: (WorkflowStage) -> Unit
) {
    val quickCommands = listOf(
        CommandAction("Understand Project Architecture", "View tech stack, data flow & entry points", Icons.Filled.AccountTree, stage = WorkflowStage.UNDERSTAND),
        CommandAction("Plan Feature Implementation", "AI decomposition into estimated tasks", Icons.Filled.AutoAwesome, stage = WorkflowStage.PLAN),
        CommandAction("Build Active Tasks", "Execute tasks with progress check", Icons.Filled.Code, stage = WorkflowStage.BUILD),
        CommandAction("Debug Error & Stack Trace", "Root cause diagnosis and instant fixes", Icons.Filled.BugReport, stage = WorkflowStage.DEBUG),
        CommandAction("Review PR & Quality Gate", "Scan security, performance & diffs", Icons.Filled.RateReview, stage = WorkflowStage.REVIEW),
        CommandAction("Improve Technical Debt", "Ranked code health refactors", Icons.Filled.TrendingUp, stage = WorkflowStage.IMPROVE),
        CommandAction("Settings & Help", "Themes, GitHub token and workflow docs", Icons.Filled.Settings, screen = "settings")
    )

    val filteredRepos = if (query.isBlank()) repositories.take(4) else repositories.filter {
        it.name.contains(query, ignoreCase = true) || it.language.contains(query, ignoreCase = true)
    }

    val filteredTasks = if (query.isBlank()) emptyList() else tasks.filter {
        it.title.contains(query, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Command input field
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Type a command, project, or task...", fontSize = 13.sp, color = TextMutedDark) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = DevPilotCyan, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextMutedDark, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_command_palette_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Stage Commands
                    item {
                        Text(
                            text = "WORKFLOW COMMANDS",
                            style = MaterialTheme.typography.labelSmall,
                            color = DevPilotCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(quickCommands.filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }) { cmd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    onDismiss()
                                    if (cmd.stage != null) {
                                        onNavigateToStage(cmd.stage)
                                    } else if (cmd.screen != null) {
                                        onNavigate(cmd.screen)
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DevPilotCyan.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cmd.icon,
                                    contentDescription = null,
                                    tint = DevPilotCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cmd.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cmd.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Matching Repositories
                    if (filteredRepos.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "REPOSITORIES",
                                style = MaterialTheme.typography.labelSmall,
                                color = DevPilotCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(filteredRepos) { repo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        onDismiss()
                                        onSelectRepo(repo.id)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Folder,
                                    contentDescription = null,
                                    tint = DevPilotCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = repo.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${repo.language} • ${repo.healthScore}% health",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark,
                                        fontSize = 11.sp
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
