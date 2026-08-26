package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepositoryEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Clean, 4-step streamlined Onboarding experience.
 * Step 1: Welcome
 * Step 2: Connect GitHub
 * Step 3: Choose first project
 * Step 4: Live project analysis & indexing
 */
@Composable
fun OnboardingScreen(
    repositories: List<RepositoryEntity>,
    onCompleteOnboarding: (selectedRepoId: String) -> Unit,
    onSkipOnboarding: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var githubAccount by remember { mutableStateOf("alex-developer") }
    var selectedRepoId by remember { mutableStateOf(repositories.firstOrNull()?.id ?: "repo_1") }
    var analysisProgress by remember { mutableStateOf(0) }

    // Step 4 analysis simulator
    LaunchedEffect(currentStep) {
        if (currentStep == 4) {
            analysisProgress = 0
            while (analysisProgress < 6) {
                delay(600L)
                analysisProgress += 1
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Logo and Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevPilotWordmark(iconSize = 28.dp)

                if (currentStep < 4) {
                    Text(
                        text = "Skip setup",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        modifier = Modifier
                            .clickable { onSkipOnboarding() }
                            .padding(8.dp)
                            .testTag("onboarding_skip_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Progress Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (step in 1..4) {
                    val isActive = step <= currentStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isActive) DevPilotCyan else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Step Content container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentStep) {
                    1 -> Step1Welcome(onNext = { currentStep = 2 })
                    2 -> Step2ConnectGitHub(
                        username = githubAccount,
                        onUsernameChange = { githubAccount = it },
                        onNext = { currentStep = 3 }
                    )
                    3 -> Step3SelectProject(
                        repositories = repositories,
                        selectedRepoId = selectedRepoId,
                        onSelectRepo = { selectedRepoId = it },
                        onNext = { currentStep = 4 }
                    )
                    4 -> Step4AnalyzingProject(
                        progress = analysisProgress,
                        selectedRepo = repositories.find { it.id == selectedRepoId },
                        onFinish = { onCompleteOnboarding(selectedRepoId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Step1Welcome(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DevPilotCyan.copy(alpha = 0.12f))
                .border(1.dp, DevPilotCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = DevPilotCyan,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to DevPilot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your intelligent software development workspace. Designed to help you understand architecture, plan features, write code, debug errors, review PRs, and improve software.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        DevPilotButton(
            text = "Get Started →",
            size = DevPilotButtonSize.LARGE,
            variant = DevPilotButtonVariant.PRIMARY,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            testTag = "onboarding_step1_next"
        )
    }
}

@Composable
private fun Step2ConnectGitHub(
    username: String,
    onUsernameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Terminal,
                contentDescription = "GitHub",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Connect GitHub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "DevPilot connects directly to your GitHub repositories to index architecture, track open issues, and generate insights.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        DevPilotCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "GitHub Username or Organization",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                singleLine = true,
                placeholder = { Text("username or org", color = TextMutedDark, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DevPilotCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_github_username_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = DevPilotInfo.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, DevPilotInfo.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = DevPilotInfo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Read-only metadata access for code analysis & architecture mapping.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DevPilotInfo,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DevPilotButton(
            text = "Connect & Continue →",
            size = DevPilotButtonSize.LARGE,
            variant = DevPilotButtonVariant.PRIMARY,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            testTag = "onboarding_step2_next"
        )
    }
}

@Composable
private fun Step3SelectProject(
    repositories: List<RepositoryEntity>,
    selectedRepoId: String,
    onSelectRepo: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Your First Project",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Choose which repository DevPilot should analyze first.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(repositories) { repo ->
                val isSelected = repo.id == selectedRepoId
                Surface(
                    onClick = { onSelectRepo(repo.id) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) DevPilotCyan.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("repo_select_${repo.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectRepo(repo.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = DevPilotCyan,
                                unselectedColor = TextMutedDark
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = repo.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(
                                        text = repo.language,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = repo.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        // Health Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DevPilotSuccess.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, DevPilotSuccess.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${repo.healthScore}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = DevPilotSuccess,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DevPilotButton(
            text = "Analyze Project →",
            size = DevPilotButtonSize.LARGE,
            variant = DevPilotButtonVariant.PRIMARY,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            testTag = "onboarding_step3_next"
        )
    }
}

@Composable
private fun Step4AnalyzingProject(
    progress: Int,
    selectedRepo: RepositoryEntity?,
    onFinish: () -> Unit
) {
    val checks = listOf(
        "Repository structure & module boundary mapping",
        "Technology stack & language dependencies",
        "Critical API routes & data flow paths",
        "Documentation coverage & health metrics",
        "Unit test suite & edge case analysis",
        "Engineering risks & recommended actions"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        if (progress < 6) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = DevPilotCyan,
                strokeWidth = 3.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DevPilotSuccess.copy(alpha = 0.15f))
                    .border(1.dp, DevPilotSuccess, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = DevPilotSuccess,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (progress < 6) "Analyzing ${selectedRepo?.name ?: "Project"}..." else "Analysis Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (progress < 6) "Extracting architectural models and building your workspace." else "Your project is ready to understand, plan, build, debug, review, and improve.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        DevPilotCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                checks.forEachIndexed { index, label ->
                    val isDone = progress > index
                    val isCurrent = progress == index

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = DevPilotSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (isCurrent) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = DevPilotCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDone) MaterialTheme.colorScheme.onSurface else TextMutedDark,
                            fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        DevPilotButton(
            text = "Open Project Workspace →",
            size = DevPilotButtonSize.LARGE,
            variant = DevPilotButtonVariant.PRIMARY,
            enabled = progress >= 6,
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            testTag = "onboarding_open_workspace_button"
        )
    }
}
