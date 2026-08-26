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
import androidx.compose.ui.window.Dialog
import com.example.data.model.RepositoryEntity
import com.example.data.model.WorkflowStage
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Modern, clean Projects screen for DevPilot.
 * Lists repositories with health scores, issues, PRs, and instant workspace launch.
 */
@Composable
fun ProjectsScreen(
    repositories: List<RepositoryEntity>,
    onSelectProject: (String) -> Unit,
    onNavigateToStage: (repoId: String, stage: WorkflowStage) -> Unit,
    onAddProject: (name: String, description: String, language: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguageFilter by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val languages = remember(repositories) {
        repositories.map { it.language }.distinct()
    }

    val filteredRepositories = remember(repositories, searchQuery, selectedLanguageFilter) {
        repositories.filter { repo ->
            (searchQuery.isBlank() || repo.name.contains(searchQuery, ignoreCase = true) || repo.description.contains(searchQuery, ignoreCase = true)) &&
            (selectedLanguageFilter == null || repo.language == selectedLanguageFilter)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Projects",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${repositories.size} connected repositories in your workspace",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DevPilotButton(
                        text = "+ Add Project",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = { showAddDialog = true },
                        testTag = "add_project_button"
                    )
                }
            }

            // Search and Language Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search projects by name or keywords...", fontSize = 13.sp, color = TextMutedDark) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = TextMutedDark,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear",
                                        tint = TextMutedDark,
                                        modifier = Modifier.size(16.dp)
                                    )
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
                            .testTag("projects_search_input")
                    )

                    // Language Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedLanguageFilter == null,
                            onClick = { selectedLanguageFilter = null },
                            label = { Text("All (${repositories.size})", fontSize = 11.sp) }
                        )

                        languages.forEach { lang ->
                            FilterChip(
                                selected = selectedLanguageFilter == lang,
                                onClick = {
                                    selectedLanguageFilter = if (selectedLanguageFilter == lang) null else lang
                                },
                                label = { Text(lang, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Projects List
            if (filteredRepositories.isEmpty()) {
                item {
                    DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderOff,
                                contentDescription = null,
                                tint = TextMutedDark,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No matching repositories found",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                items(filteredRepositories) { repo ->
                    ProjectCardItem(
                        repo = repo,
                        onOpenWorkspace = { onSelectProject(repo.id) },
                        onNavigateToStage = { stage -> onNavigateToStage(repo.id, stage) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddProjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, lang ->
                onAddProject(name, desc, lang)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ProjectCardItem(
    repo: RepositoryEntity,
    onOpenWorkspace: () -> Unit,
    onNavigateToStage: (WorkflowStage) -> Unit
) {
    DevPilotCard(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("project_card_${repo.id}")
    ) {
        // Top Row: Title, Language, Health Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DevPilotCyan.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Code,
                        contentDescription = null,
                        tint = DevPilotCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = repo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = repo.fullName,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = TextMutedDark,
                        fontSize = 11.sp
                    )
                }
            }

            // Health Badge
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (repo.healthScore >= 80) DevPilotSuccess else DevPilotWarning)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${repo.healthScore}% Healthy",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (repo.healthScore >= 80) DevPilotSuccess else DevPilotWarning
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = repo.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Metadata Pills (Language, Issues, PRs, Stars)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (repo.language) {
                                "Kotlin" -> DevPilotViolet
                                "Python" -> DevPilotCyan
                                "Rust" -> DevPilotWarning
                                "Go" -> DevPilotInfo
                                else -> DevPilotSuccess
                            }
                        )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = repo.language,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Adjust,
                    contentDescription = "Issues",
                    tint = TextMutedDark,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repo.openIssues} issues",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CallMerge,
                    contentDescription = "PRs",
                    tint = TextMutedDark,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repo.openPrs} PRs",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Stars",
                    tint = DevPilotWarning,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repo.stars}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(10.dp))

        // Action Row: Open Workspace + 6 Quick Stage Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DevPilotButton(
                text = "Open Workspace →",
                variant = DevPilotButtonVariant.PRIMARY,
                size = DevPilotButtonSize.SMALL,
                onClick = onOpenWorkspace,
                testTag = "open_workspace_${repo.id}"
            )

            // Stage Jump Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    onClick = { onNavigateToStage(WorkflowStage.UNDERSTAND) },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "Map",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = DevPilotCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    onClick = { onNavigateToStage(WorkflowStage.PLAN) },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "Plan",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = DevPilotViolet,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    onClick = { onNavigateToStage(WorkflowStage.DEBUG) },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "Debug",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = DevPilotDanger,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    onClick = { onNavigateToStage(WorkflowStage.REVIEW) },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        text = "Review",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = DevPilotSuccess,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, language: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("Kotlin") }

    Dialog(onDismissRequest = onDismiss) {
        DevPilotCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Connect / Add New Project",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Project / Repository Name", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("e.g. payment-gateway", color = TextMutedDark, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().testTag("add_project_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Description", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Short description of service...", color = TextMutedDark, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Primary Language", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                singleLine = true,
                placeholder = { Text("Kotlin / TypeScript / Python / Rust", color = TextMutedDark, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    text = "Add & Index",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.SMALL,
                    enabled = name.isNotBlank(),
                    onClick = { onConfirm(name, description, language) },
                    testTag = "add_project_confirm_button"
                )
            }
        }
    }
}
