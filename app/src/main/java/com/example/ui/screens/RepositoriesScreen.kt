package com.example.ui.screens

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
import com.example.data.model.RepositoryAnalysisEntity
import com.example.data.model.RepositoryEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun RepositoriesScreen(
    repositories: List<RepositoryEntity>,
    selectedRepoId: String?,
    onSelectRepo: (String) -> Unit,
    selectedRepoAnalysis: RepositoryAnalysisEntity?,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: High Health, 2: Needs Attention

    val filteredRepos = repositories.filter { repo ->
        val matchesSearch = repo.name.contains(searchQuery, ignoreCase = true) ||
                repo.language.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            1 -> repo.healthScore >= 90
            2 -> repo.healthScore < 85
            else -> true
        }
        matchesSearch && matchesTab
    }

    val activeRepo = repositories.firstOrNull { it.id == selectedRepoId } ?: repositories.firstOrNull()

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
                title = "Repository Analyzer",
                subtitle = "Deep architecture analysis, health scoring & dependency telemetry",
                breadcrumb = "WORKSPACE / DEV PILOT / REPOSITORIES"
            )
        }

        // Search Bar & Filter Chips
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search repositories by name or tech stack...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = DevPilotCyan, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
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
                    .testTag("repo_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            DevPilotSegmentedTabGroup(
                items = listOf(0, 1, 2),
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it },
                labelProvider = {
                    when (it) {
                        0 -> "All (${repositories.size})"
                        1 -> "Health 90+"
                        else -> "Needs Attention"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Selected Repository Deep Health Analysis Card
        if (activeRepo != null) {
            item {
                DevPilotCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    borderColor = DevPilotCyan.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeRepo.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                LanguageDot(activeRepo.language, activeRepo.languageColor)
                            }
                            Text(
                                text = activeRepo.fullName,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        HealthScorePill(activeRepo.healthScore)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = activeRepo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Architecture Summary
                    Text(
                        text = "ARCHITECTURE & STACK SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ArchMetricBadge(label = "Project Type", value = selectedRepoAnalysis?.projectType ?: "Full-stack Backend", modifier = Modifier.weight(1f))
                        ArchMetricBadge(label = "Framework", value = selectedRepoAnalysis?.framework ?: "Jetpack Compose / FastAPI", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ArchMetricBadge(label = "Database", value = selectedRepoAnalysis?.database ?: "PostgreSQL / SQLite", modifier = Modifier.weight(1f))
                        ArchMetricBadge(label = "Architecture", value = selectedRepoAnalysis?.architecture ?: "Clean Architecture", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Health Score Breakdown
                    Text(
                        text = "HEALTH SCORE BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HealthMetricBar(title = "Documentation", score = selectedRepoAnalysis?.documentationScore ?: 90)
                    HealthMetricBar(title = "Testing & Coverage", score = selectedRepoAnalysis?.testingScore ?: 75)
                    HealthMetricBar(title = "Code Structure", score = selectedRepoAnalysis?.codeStructureScore ?: 88)
                    HealthMetricBar(title = "Git Activity", score = selectedRepoAnalysis?.gitActivityScore ?: 84)
                    HealthMetricBar(title = "Security Compliance", score = selectedRepoAnalysis?.securityScore ?: 88)
                    HealthMetricBar(title = "CI/CD Pipeline", score = selectedRepoAnalysis?.ciCdScore ?: 85)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick AI Action Buttons for Repo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DevPilotButton(
                            text = "Generate Docs",
                            icon = Icons.Filled.Description,
                            variant = DevPilotButtonVariant.SECONDARY,
                            size = DevPilotButtonSize.SMALL,
                            onClick = { onNavigate("docs") },
                            modifier = Modifier.weight(1f)
                        )

                        DevPilotButton(
                            text = "AI Review",
                            icon = Icons.Filled.AutoAwesome,
                            variant = DevPilotButtonVariant.SECONDARY,
                            size = DevPilotButtonSize.SMALL,
                            onClick = { onNavigate("ai_workspace") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Repository List
        item {
            DevPilotSectionHeader(
                title = "ALL REPOSITORIES",
                count = filteredRepos.size
            )
        }

        items(filteredRepos) { repo ->
            val isSelected = repo.id == selectedRepoId
            DevPilotCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectRepo(repo.id) },
                shape = RoundedCornerShape(6.dp),
                borderColor = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.outline
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = repo.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = repo.fullName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    HealthScorePill(repo.healthScore)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LanguageDot(repo.language, repo.languageColor)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = DevPilotWarning, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${repo.stars}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AltRoute, contentDescription = null, tint = DevPilotCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${repo.forks}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = DevPilotDanger, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${repo.openIssues}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchMetricBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextMutedDark)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        }
    }
}

@Composable
fun HealthMetricBar(title: String, score: Int) {
    val color = when {
        score >= 85 -> DevPilotSuccess
        score >= 70 -> DevPilotWarning
        else -> DevPilotDanger
    }

    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$score%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = color, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}
