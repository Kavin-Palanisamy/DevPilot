package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun DashboardScreen(
    user: UserEntity?,
    repositories: List<RepositoryEntity>,
    tasks: List<TaskEntity>,
    risks: List<EngineeringRiskEntity>,
    goals: List<ProjectGoalEntity>,
    nextBestAction: NextBestAction,
    dailyPlan: DailyPlanEntity?,
    activities: List<DeveloperActivityEntity>,
    totalFocusMinutes: Int,
    onNavigate: (String) -> Unit,
    onSelectRepo: (String) -> Unit,
    onConvertRiskToTask: (EngineeringRiskEntity) -> Unit,
    onResolveRisk: (String) -> Unit,
    onOpenArchitectureMap: () -> Unit,
    onAskAi: (String) -> Unit
) {
    val openTasksCount = tasks.count { it.status != TaskStatus.COMPLETED }
    val completedTasksCount = tasks.count { it.status == TaskStatus.COMPLETED }
    val totalReposCount = repositories.size
    val totalIssuesCount = repositories.sumOf { it.openIssues }
    val totalPrsCount = repositories.sumOf { it.openPrs }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // 1. Developer Greeting & Workspace Breadcrumb Header
        item {
            DevPilotCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WORKSPACE / DEV PILOT CORE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Welcome back, ${user?.name ?: "Alex Chen"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "AI engine synchronized • $totalReposCount active repositories tracked",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DevPilotButton(
                        text = "Focus",
                        icon = Icons.Filled.PlayArrow,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { onNavigate("focus") }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Navigation Shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DevPilotButton(
                        text = "AI Studio",
                        icon = Icons.Filled.AutoAwesome,
                        variant = DevPilotButtonVariant.SECONDARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { onNavigate("ai_workspace") },
                        modifier = Modifier.weight(1f)
                    )
                    DevPilotButton(
                        text = "Planner",
                        icon = Icons.Filled.CalendarMonth,
                        variant = DevPilotButtonVariant.SECONDARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { onNavigate("planner") },
                        modifier = Modifier.weight(1f)
                    )
                    DevPilotButton(
                        text = "Topology",
                        icon = Icons.Filled.Hub,
                        variant = DevPilotButtonVariant.SECONDARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = onOpenArchitectureMap,
                        modifier = Modifier.weight(1f)
                    )
                    DevPilotButton(
                        text = "Kanban",
                        icon = Icons.Filled.ViewKanban,
                        variant = DevPilotButtonVariant.SECONDARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { onNavigate("tasks") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Next Best Action Decision Card (Signature SaaS Feature)
        item {
            NextBestActionCard(
                action = nextBestAction,
                onStartAction = {
                    if (nextBestAction.actionType == "FIX_RISK") {
                        onNavigate("repos")
                    } else {
                        onNavigate("focus")
                    }
                },
                onOpenPlan = { onNavigate("planner") },
                onAskAi = onAskAi
            )
        }

        // 3. Engineering Risk Center (Security, Architecture, Tech Debt)
        item {
            EngineeringRiskCenter(
                risks = risks,
                onConvertRiskToTask = onConvertRiskToTask,
                onResolveRisk = onResolveRisk,
                onInspectLocation = { loc -> onAskAi("Audit risk and propose fix for code at $loc") }
            )
        }

        // 4. Strategic Project Goals & Milestones Roadmap
        item {
            DevPilotCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DevPilotSectionHeader(
                        title = "STRATEGIC GOALS & ROADMAP",
                        count = goals.size
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    goals.forEach { goal ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${goal.progressPercentage}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (goal.progressPercentage > 50) DevPilotSuccess else DevPilotCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = goal.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { goal.progressPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (goal.progressPercentage > 50) DevPilotSuccess else DevPilotCyan,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Developer Stats Grid (4 Metric Tiles with Trends)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        title = "PRODUCTIVITY SCORE",
                        value = "${user?.productivityScore ?: 89}/100",
                        subtitle = "Top 5% developer velocity",
                        icon = Icons.Filled.Insights,
                        accentColor = DevPilotCyan,
                        trendText = "↑ 6%",
                        isTrendPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "CODING STREAK",
                        value = "${user?.codingStreakDays ?: 18} Days",
                        subtitle = "Personal best streak",
                        icon = Icons.Filled.LocalFireDepartment,
                        accentColor = DevPilotWarning,
                        trendText = "Active",
                        isTrendPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTile(
                        title = "ACTIVE REPOSITORIES",
                        value = "$totalReposCount Repos",
                        subtitle = "$totalIssuesCount issues • $totalPrsCount PRs",
                        icon = Icons.Filled.Folder,
                        accentColor = DevPilotViolet,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        title = "TASKS & FOCUS",
                        value = "$completedTasksCount Done",
                        subtitle = "$openTasksCount pending • ${totalFocusMinutes}m focus",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = DevPilotSuccess,
                        trendText = "↑ 12%",
                        isTrendPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Connected Repositories Overview
        item {
            DevPilotSectionHeader(
                title = "REPOSITORIES",
                count = repositories.size,
                actionSlot = {
                    TextButton(onClick = { onNavigate("repos") }) {
                        Text("View all", color = DevPilotCyan, style = MaterialTheme.typography.labelMedium)
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(repositories) { repo ->
                    DevPilotCard(
                        modifier = Modifier
                            .width(260.dp)
                            .clickable {
                                onSelectRepo(repo.id)
                                onNavigate("repos")
                            },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = repo.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            HealthScorePill(repo.healthScore)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = repo.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            minLines = 2
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LanguageDot(repo.language, repo.languageColor)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = DevPilotWarning, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${repo.stars}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Recent Developer Activities Stream
        item {
            DevPilotSectionHeader(
                title = "RECENT ACTIVITY STREAM",
                count = activities.size
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                activities.take(4).forEach { act ->
                    DevPilotCard(
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (icon, tint) = when (act.type) {
                                ActivityType.COMMIT -> Pair(Icons.Filled.Commit, DevPilotCyan)
                                ActivityType.PR -> Pair(Icons.Filled.Merge, DevPilotViolet)
                                ActivityType.TASK_DONE -> Pair(Icons.Filled.CheckCircle, DevPilotSuccess)
                                ActivityType.FOCUS -> Pair(Icons.Filled.Timer, DevPilotWarning)
                                else -> Pair(Icons.Filled.Code, MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(tint.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(15.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = act.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${act.repoName} • Just now",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
