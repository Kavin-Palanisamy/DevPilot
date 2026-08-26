package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * DevPilot Project Workspace — The heart of DevPilot.
 * Unified 6-stage workflow: Understand → Plan → Build → Debug → Review → Improve.
 */
@Composable
fun ProjectWorkspaceScreen(
    repository: RepositoryEntity?,
    analysis: RepositoryAnalysisEntity?,
    tasks: List<TaskEntity>,
    activeStage: WorkflowStage,
    onStageSelect: (WorkflowStage) -> Unit,
    onBackToProjects: () -> Unit,
    // AI Workspace delegates
    onAskAiQuestion: (String) -> Unit,
    aiAnswerResult: String?,
    isAiLoading: Boolean,
    // Plan delegates
    onGeneratePlan: (String) -> Unit,
    decomposedSteps: List<DecomposedSubtask>,
    isPlanningLoading: Boolean,
    onConvertPlanToTasks: () -> Unit,
    // Build delegates
    onUpdateTaskStatus: (TaskEntity, TaskStatus) -> Unit,
    onSaveTask: (String, String, TaskPriority, TaskStatus, Int) -> Unit,
    // Debug delegates
    onRunDebugger: (error: String, stackTrace: String) -> Unit,
    debugResult: DebugAnalysisResult?,
    isDebugging: Boolean,
    // Review delegates
    onRunReview: () -> Unit,
    reviewResult: CodeReviewResult?,
    isReviewing: Boolean,
    // Improve delegates
    risks: List<EngineeringRiskEntity>,
    onConvertRiskToTask: (EngineeringRiskEntity) -> Unit,
    onResolveRisk: (String) -> Unit
) {
    val repo = repository ?: RepositoryEntity(
        id = "repo_default",
        name = "devpilot-api",
        fullName = "acme/devpilot-api",
        description = "Core backend service for developer workspace analytics.",
        language = "Kotlin",
        languageColor = "#7F52FF",
        healthScore = 86
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Workspace Top Bar: Repo Header + Quick Actions
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onBackToProjects,
                            modifier = Modifier.size(32.dp).testTag("workspace_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back to projects",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = repo.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DevPilotCyan.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, DevPilotCyan.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "${repo.language} • ${repo.healthScore}% Health",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DevPilotCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = repo.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // The 6-Stage Workflow Switcher Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeStage.ordinal,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        // Subtle cyan bottom indicator
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(DevPilotCyan, RoundedCornerShape(2.dp))
                        )
                    }
                ) {
                    WorkflowStage.values().forEach { stage ->
                        val isSelected = stage == activeStage
                        val icon = when (stage) {
                            WorkflowStage.UNDERSTAND -> Icons.Filled.AccountTree
                            WorkflowStage.PLAN -> Icons.Filled.AutoAwesome
                            WorkflowStage.BUILD -> Icons.Filled.Code
                            WorkflowStage.DEBUG -> Icons.Filled.BugReport
                            WorkflowStage.REVIEW -> Icons.Filled.RateReview
                            WorkflowStage.IMPROVE -> Icons.Filled.TrendingUp
                        }

                        Tab(
                            selected = isSelected,
                            onClick = { onStageSelect(stage) },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) DevPilotCyan else TextMutedDark,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = stage.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            modifier = Modifier.testTag("stage_tab_${stage.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Active Stage Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (activeStage) {
                WorkflowStage.UNDERSTAND -> UnderstandStageView(
                    repo = repo,
                    analysis = analysis,
                    onAskAiQuestion = onAskAiQuestion,
                    aiAnswerResult = aiAnswerResult,
                    isAiLoading = isAiLoading
                )
                WorkflowStage.PLAN -> PlanStageView(
                    repo = repo,
                    onGeneratePlan = onGeneratePlan,
                    decomposedSteps = decomposedSteps,
                    isPlanningLoading = isPlanningLoading,
                    onConvertPlanToTasks = onConvertPlanToTasks
                )
                WorkflowStage.BUILD -> BuildStageView(
                    repo = repo,
                    tasks = tasks.filter { it.repositoryId == repo.id || it.repositoryName == repo.name },
                    onUpdateTaskStatus = onUpdateTaskStatus,
                    onSaveTask = onSaveTask
                )
                WorkflowStage.DEBUG -> DebugStageView(
                    repo = repo,
                    onRunDebugger = onRunDebugger,
                    debugResult = debugResult,
                    isDebugging = isDebugging,
                    onSaveTask = onSaveTask
                )
                WorkflowStage.REVIEW -> ReviewStageView(
                    repo = repo,
                    onRunReview = onRunReview,
                    reviewResult = reviewResult,
                    isReviewing = isReviewing
                )
                WorkflowStage.IMPROVE -> ImproveStageView(
                    repo = repo,
                    risks = risks.filter { it.repoId == repo.id || it.repoName == repo.name },
                    onConvertRiskToTask = onConvertRiskToTask,
                    onResolveRisk = onResolveRisk
                )
            }
        }
    }
}

/**
 * 1. UNDERSTAND STAGE: Architecture diagram, technologies, important files, and interactive AI Q&A.
 */
@Composable
private fun UnderstandStageView(
    repo: RepositoryEntity,
    analysis: RepositoryAnalysisEntity?,
    onAskAiQuestion: (String) -> Unit,
    aiAnswerResult: String?,
    isAiLoading: Boolean
) {
    var aiQuestion by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Architecture Overview Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Architecture & Service Flow",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = analysis?.architecture ?: "Clean Modular",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = DevPilotCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive architecture layer visualization
                val layers = listOf(
                    Triple("API Gateway / Routers", "FastAPI / HTTP Endpoints & JWT Auth middleware", DevPilotCyan),
                    Triple("Domain & Business Services", "Core execution logic, token verification & state machines", DevPilotViolet),
                    Triple("Repository & Persistence", "Room ORM / PostgreSQL with connection pooling", DevPilotSuccess),
                    Triple("External Integrations", "GitHub REST/GraphQL API & Gemini AI Engine", DevPilotWarning)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    layers.forEachIndexed { index, (layerName, desc, color) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = layerName,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
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

        // Technology Stack Breakdown
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Technology Stack",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                val techPills = listOf(
                    "Primary Language" to repo.language,
                    "Framework" to (analysis?.framework ?: "FastAPI"),
                    "Database" to (analysis?.database ?: "PostgreSQL"),
                    "Testing Suite" to (analysis?.testing ?: "Unit & Integration"),
                    "CI/CD Pipeline" to "GitHub Actions"
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    techPills.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = key, style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Important Files Directory
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Important Files & Entry Points",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                val importantFiles = listOf(
                    "src/main.py" to "Application entry point & middleware router setup",
                    "src/auth/service.py" to "JWT token encoding, validation & OAuth verification",
                    "src/models/user.py" to "User schema, database mapping & permissions",
                    "tests/test_auth.py" to "Authentication integration & token refresh test suite"
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    importantFiles.forEach { (file, role) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = DevPilotCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = file,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = role,
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

        // "Ask AI About This Project" Interactive Box
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                borderColor = DevPilotCyan.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = DevPilotCyan, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Ask AI About This Project",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DevPilotCyan
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Get architectural explanations, locate components, or clarify data flow.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = aiQuestion,
                    onValueChange = { aiQuestion = it },
                    placeholder = { Text("e.g. How is token rotation handled across services?", fontSize = 13.sp, color = TextMutedDark) },
                    modifier = Modifier.fillMaxWidth().testTag("understand_ai_question_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                DevPilotButton(
                    text = "Ask DevPilot AI",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.SMALL,
                    isLoading = isAiLoading,
                    enabled = aiQuestion.isNotBlank(),
                    onClick = { onAskAiQuestion(aiQuestion) },
                    testTag = "understand_ask_ai_button"
                )

                // AI Answer output
                if (aiAnswerResult != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DevPilot AI Explanation:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = aiAnswerResult,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. PLAN STAGE: Feature goal input → AI decomposition → implementation plan → create tasks.
 */
@Composable
private fun PlanStageView(
    repo: RepositoryEntity,
    onGeneratePlan: (String) -> Unit,
    decomposedSteps: List<DecomposedSubtask>,
    isPlanningLoading: Boolean,
    onConvertPlanToTasks: () -> Unit
) {
    var goalText by remember { mutableStateOf("Implement Google OAuth2 Sign-In and Token Refresh Flow") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Goal Input Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "What do you want to build?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Describe a feature or goal. DevPilot will generate a prioritized, step-by-step implementation plan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    placeholder = { Text("e.g. Add webhook delivery with HMAC verification...", fontSize = 13.sp, color = TextMutedDark) },
                    modifier = Modifier.fillMaxWidth().testTag("plan_goal_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                DevPilotButton(
                    text = "Generate Implementation Plan →",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    isLoading = isPlanningLoading,
                    enabled = goalText.isNotBlank(),
                    onClick = { onGeneratePlan(goalText) },
                    testTag = "plan_generate_button"
                )
            }
        }

        // Implementation Plan Results
        if (decomposedSteps.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generated Implementation Plan (${decomposedSteps.size} steps)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    DevPilotButton(
                        text = "Add All to Build Stage",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = onConvertPlanToTasks,
                        testTag = "plan_add_all_to_tasks"
                    )
                }
            }

            items(decomposedSteps) { step ->
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
                                text = step.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Estimated effort: ${step.estimatedMinutes} mins • ${step.priority.name} Priority",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DevPilotCyan.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, DevPilotCyan.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${step.estimatedMinutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. BUILD STAGE: Current active task, progress, interactive completion check, code assistant.
 */
@Composable
private fun BuildStageView(
    repo: RepositoryEntity,
    tasks: List<TaskEntity>,
    onUpdateTaskStatus: (TaskEntity, TaskStatus) -> Unit,
    onSaveTask: (String, String, TaskPriority, TaskStatus, Int) -> Unit
) {
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val activeTasks = tasks.filter { it.status != TaskStatus.COMPLETED }
    val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }
    val progress = if (tasks.isNotEmpty()) (completedTasks.size.toFloat() / tasks.size.toFloat()) else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Build Progress Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Build & Execution Progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${completedTasks.size} of ${tasks.size} tasks completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedDark
                        )
                    }

                    DevPilotButton(
                        text = "+ New Task",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { showAddTaskDialog = true },
                        testTag = "build_new_task_button"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = DevPilotCyan,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        // Active Tasks Section
        item {
            Text(
                text = "Remaining Tasks (${activeTasks.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (activeTasks.isEmpty()) {
            item {
                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "All tasks completed! Head to the Review stage to inspect your pull requests.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DevPilotSuccess
                    )
                }
            }
        } else {
            items(activeTasks) { task ->
                DevPilotCard(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("build_task_${task.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { onUpdateTaskStatus(task, TaskStatus.COMPLETED) },
                            colors = CheckboxDefaults.colors(checkedColor = DevPilotCyan)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (task.description.isNotBlank()) {
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${task.estimatedMinutes}m est • ${task.priority.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                        }

                        // Status switch
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                val nextStatus = if (task.status == TaskStatus.TODO) TaskStatus.IN_PROGRESS else TaskStatus.TODO
                                onUpdateTaskStatus(task, nextStatus)
                            }
                        ) {
                            Text(
                                text = task.status.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DevPilotCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Completed Section
        if (completedTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Completed (${completedTasks.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMutedDark
                )
            }

            items(completedTasks) { task ->
                DevPilotCard(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = DevPilotSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedDark
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            repoId = repo.id,
            repoName = repo.name,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, prio, est ->
                onSaveTask(title, desc, prio, TaskStatus.TODO, est)
                showAddTaskDialog = false
            }
        )
    }
}

/**
 * 4. DEBUG STAGE: Input error / stack trace → AI root cause analysis → fix tasks.
 */
@Composable
private fun DebugStageView(
    repo: RepositoryEntity,
    onRunDebugger: (error: String, stackTrace: String) -> Unit,
    debugResult: DebugAnalysisResult?,
    isDebugging: Boolean,
    onSaveTask: (String, String, TaskPriority, TaskStatus, Int) -> Unit
) {
    var errorMessage by remember { mutableStateOf("NullPointerException: Parameter 'userToken' cannot be null in AuthHandler") }
    var stackTrace by remember { mutableStateOf("at com.example.auth.AuthHandler.validate(AuthHandler.kt:42)\nat com.example.api.Gateway.handleRequest(Gateway.kt:18)") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Debug Input Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "What's going wrong?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Paste your error message, stack trace, or buggy code snippet for immediate AI diagnosis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Error Message", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = errorMessage,
                    onValueChange = { errorMessage = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("debug_error_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Stack Trace / Snippet", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = stackTrace,
                    onValueChange = { stackTrace = it },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth().testTag("debug_stack_trace_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                DevPilotButton(
                    text = "Analyze Error with AI →",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    isLoading = isDebugging,
                    enabled = errorMessage.isNotBlank(),
                    onClick = { onRunDebugger(errorMessage, stackTrace) },
                    testTag = "debug_analyze_button"
                )
            }
        }

        // Diagnosis Output
        debugResult?.let { res ->
            item {
                DevPilotCard(
                    shape = RoundedCornerShape(10.dp),
                    borderColor = DevPilotDanger.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth().testTag("debug_result_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Root Cause Diagnosis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DevPilotDanger
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DevPilotDanger.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = res.errorType,
                                style = MaterialTheme.typography.labelSmall,
                                color = DevPilotDanger,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Root Cause: ${res.rootCause}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = res.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Recommended Solution Steps:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    res.solutionSteps.forEachIndexed { i, step ->
                        Text(
                            text = "${i + 1}. $step",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    if (res.correctedCode.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Corrected Code Snippet:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DevPilotSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CodeBlockCard(
                            code = res.correctedCode,
                            language = "Kotlin"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DevPilotButton(
                        text = "+ Create Task to Fix This",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = {
                            onSaveTask("Fix: ${res.rootCause}", res.explanation, TaskPriority.HIGH, TaskStatus.TODO, 30)
                        },
                        testTag = "debug_create_fix_task"
                    )
                }
            }
        }
    }
}

/**
 * 5. REVIEW STAGE: Pull Request / Code Review with risk analysis and findings.
 */
@Composable
private fun ReviewStageView(
    repo: RepositoryEntity,
    onRunReview: () -> Unit,
    reviewResult: CodeReviewResult?,
    isReviewing: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Review Trigger Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Code Review & PR Quality Gate",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Scan PR diffs for security issues, test gaps, and performance bottlenecks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                DevPilotButton(
                    text = "Run AI Code Review →",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    isLoading = isReviewing,
                    onClick = onRunReview,
                    testTag = "review_run_button"
                )
            }
        }

        // Review Findings Output
        reviewResult?.let { res ->
            item {
                DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Review Assessment Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (res.securityRisk == "Low") DevPilotSuccess.copy(alpha = 0.12f) else DevPilotWarning.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Risk: ${res.securityRisk}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (res.securityRisk == "Low") DevPilotSuccess else DevPilotWarning,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = res.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Specific Findings & Recommendations:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        res.findings.forEach { finding ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = finding.line,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = DevPilotCyan
                                        )
                                        Text(
                                            text = finding.type,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DevPilotWarning
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = finding.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Suggestion: ${finding.suggestion}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DevPilotSuccess
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

/**
 * 6. IMPROVE STAGE: Priority-based project improvements with effort estimates & instant task creation.
 */
@Composable
private fun ImproveStageView(
    repo: RepositoryEntity,
    risks: List<EngineeringRiskEntity>,
    onConvertRiskToTask: (EngineeringRiskEntity) -> Unit,
    onResolveRisk: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Project Improvements & Technical Debt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ranked by priority and effort. Each recommendation can be converted into an active task.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (risks.isEmpty()) {
            item {
                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No open technical debt or high-risk items found for ${repo.name}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DevPilotSuccess
                    )
                }
            }
        } else {
            items(risks) { risk ->
                DevPilotCard(
                    shape = RoundedCornerShape(8.dp),
                    borderColor = when (risk.severity) {
                        RiskSeverity.CRITICAL -> DevPilotDanger.copy(alpha = 0.4f)
                        RiskSeverity.HIGH -> DevPilotWarning.copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.outline
                    },
                    modifier = Modifier.fillMaxWidth().testTag("improve_risk_${risk.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = risk.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (risk.severity) {
                                RiskSeverity.CRITICAL -> DevPilotDanger.copy(alpha = 0.12f)
                                RiskSeverity.HIGH -> DevPilotWarning.copy(alpha = 0.12f)
                                else -> DevPilotCyan.copy(alpha = 0.12f)
                            }
                        ) {
                            Text(
                                text = "${risk.severity.name} PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (risk.severity) {
                                    RiskSeverity.CRITICAL -> DevPilotDanger
                                    RiskSeverity.HIGH -> DevPilotWarning
                                    else -> DevPilotCyan
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = risk.impact,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Suggested Fix: ${risk.suggestedFix}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DevPilotCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Est. effort: ${risk.estimatedEffortMinutes} mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )

                        DevPilotButton(
                            text = "+ Create Task",
                            variant = DevPilotButtonVariant.PRIMARY,
                            size = DevPilotButtonSize.SMALL,
                            onClick = { onConvertRiskToTask(risk) },
                            testTag = "improve_create_task_${risk.id}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    repoId: String,
    repoName: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, priority: TaskPriority, estimatedMinutes: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var estimatedMinutes by remember { mutableStateOf(60) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        DevPilotCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "New Implementation Task",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Task Title", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                placeholder = { Text("e.g. Add token expiration refresh handler", color = TextMutedDark, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Description", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Implementation notes & context...", color = TextMutedDark, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevPilotButton(
                    text = "Cancel",
                    variant = DevPilotButtonVariant.GHOST,
                    size = DevPilotButtonSize.SMALL,
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.width(8.dp))
                DevPilotButton(
                    text = "Create Task",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.SMALL,
                    enabled = title.isNotBlank(),
                    onClick = { onConfirm(title, description, priority, estimatedMinutes) }
                )
            }
        }
    }
}
