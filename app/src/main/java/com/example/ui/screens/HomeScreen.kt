package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * DevPilot Home Screen — Answers "What should I do next?".
 * 1. AI Recommendation / Next Best Action
 * 2. Projects at a glance with Health Score
 * 3. Recent Activity Stream
 */
@Composable
fun HomeScreen(
    user: UserEntity?,
    repositories: List<RepositoryEntity>,
    nextBestAction: NextBestAction?,
    recentActivities: List<DeveloperActivityEntity>,
    tasks: List<TaskEntity>,
    onSelectProject: (String) -> Unit,
    onNavigateToStage: (repoId: String, stage: WorkflowStage) -> Unit,
    onNavigateToProjects: () -> Unit,
    onCreateTaskFromAction: (NextBestAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Daily Focus Banner
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome back, ${user?.name?.split(" ")?.firstOrNull() ?: "Developer"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Here is what DevPilot recommends for your codebase today.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick streak badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DevPilotWarning.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DevPilotWarning.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = DevPilotWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.codingStreakDays ?: 18}d streak",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotWarning
                            )
                        }
                    }
                }
            }
        }

        // 1. AI Recommendation (Next Best Action)
        item {
            nextBestAction?.let { action ->
                DevPilotCard(
                    shape = RoundedCornerShape(10.dp),
                    borderColor = DevPilotCyan.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth().testTag("home_next_best_action_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = DevPilotCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "RECOMMENDED NEXT ACTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotCyan,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Priority Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DevPilotDanger.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, DevPilotDanger.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${action.priority.name} PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotDanger,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = action.impact,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target repo and effort
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null,
                                tint = TextMutedDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = action.targetRepoName,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondaryDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = TextMutedDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = action.estimatedEffort,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DevPilotButton(
                            text = "Create Task",
                            variant = DevPilotButtonVariant.PRIMARY,
                            size = DevPilotButtonSize.SMALL,
                            icon = Icons.Filled.Add,
                            onClick = { onCreateTaskFromAction(action) },
                            testTag = "home_create_task_from_action"
                        )

                        DevPilotButton(
                            text = "Open in Improve Stage",
                            variant = DevPilotButtonVariant.SECONDARY,
                            size = DevPilotButtonSize.SMALL,
                            icon = Icons.Filled.TrendingUp,
                            onClick = { onNavigateToStage(action.targetRepoId, WorkflowStage.IMPROVE) },
                            testTag = "home_view_in_improve"
                        )
                    }
                }
            }
        }

        // 2. Your Projects Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Projects",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "View All (${repositories.size}) →",
                        style = MaterialTheme.typography.labelSmall,
                        color = DevPilotCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToProjects() }
                            .padding(4.dp)
                            .testTag("home_view_all_projects")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repositories.take(3).forEach { repo ->
                        DevPilotCard(
                            shape = RoundedCornerShape(8.dp),
                            onClick = { onSelectProject(repo.id) },
                            modifier = Modifier.fillMaxWidth().testTag("home_project_${repo.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Code,
                                            contentDescription = null,
                                            tint = DevPilotCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = repo.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "• ${repo.language}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMutedDark,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = repo.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Health Score indicator
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (repo.healthScore >= 80) DevPilotSuccess.copy(alpha = 0.12f)
                                    else DevPilotWarning.copy(alpha = 0.12f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (repo.healthScore >= 80) DevPilotSuccess.copy(alpha = 0.3f)
                                        else DevPilotWarning.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${repo.healthScore}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (repo.healthScore >= 80) DevPilotSuccess else DevPilotWarning
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Active Tasks Summary
        item {
            val inProgressTasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.TODO }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Tasks (${inProgressTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (inProgressTasks.isEmpty()) {
                    DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No active tasks. Use the Plan stage to create implementation tasks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        inProgressTasks.take(3).forEach { task ->
                            DevPilotCard(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${task.repositoryName ?: "Core"} • ${task.estimatedMinutes}m est",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (task.priority) {
                                            TaskPriority.CRITICAL -> DevPilotDanger.copy(alpha = 0.12f)
                                            TaskPriority.HIGH -> DevPilotWarning.copy(alpha = 0.12f)
                                            else -> DevPilotCyan.copy(alpha = 0.12f)
                                        }
                                    ) {
                                        Text(
                                            text = task.priority.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (task.priority) {
                                                TaskPriority.CRITICAL -> DevPilotDanger
                                                TaskPriority.HIGH -> DevPilotWarning
                                                else -> DevPilotCyan
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Recent Developer Activity
        item {
            Column {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        recentActivities.take(4).forEach { activity ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (activity.type) {
                                                ActivityType.COMMIT -> DevPilotCyan.copy(alpha = 0.12f)
                                                ActivityType.PR -> DevPilotViolet.copy(alpha = 0.12f)
                                                ActivityType.TASK_DONE -> DevPilotSuccess.copy(alpha = 0.12f)
                                                else -> DevPilotWarning.copy(alpha = 0.12f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (activity.type) {
                                            ActivityType.COMMIT -> Icons.Filled.Commit
                                            ActivityType.PR -> Icons.Filled.CallMerge
                                            ActivityType.TASK_DONE -> Icons.Filled.CheckCircle
                                            else -> Icons.Filled.RateReview
                                        },
                                        contentDescription = null,
                                        tint = when (activity.type) {
                                            ActivityType.COMMIT -> DevPilotCyan
                                            ActivityType.PR -> DevPilotViolet
                                            ActivityType.TASK_DONE -> DevPilotSuccess
                                            else -> DevPilotWarning
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${activity.repoName} • ${activity.extraInfo}",
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
