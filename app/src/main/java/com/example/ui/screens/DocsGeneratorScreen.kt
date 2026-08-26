package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepositoryEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsGeneratorScreen(
    repositories: List<RepositoryEntity>,
    selectedDocType: String,
    onDocTypeChange: (String) -> Unit,
    generatedDoc: String?,
    isLoading: Boolean,
    onGenerateDocs: (repoName: String, repoDesc: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var selectedRepo by remember { mutableStateOf(repositories.firstOrNull()) }

    val docTypes = listOf(
        "README.md",
        "API Reference",
        "Architecture Spec",
        "Environment .env Setup",
        "Contributing Guide"
    )

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
                title = "AI Documentation Generator",
                subtitle = "Generate production-grade markdown docs, API reference & architecture diagrams",
                breadcrumb = "WORKSPACE / DEV PILOT / DOCS GEN"
            )
        }

        // Configuration Card
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                borderColor = DevPilotCyan.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "TARGET REPOSITORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Repo selection chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repositories.take(3).forEach { repo ->
                        val isSelected = selectedRepo?.id == repo.id
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.clickable { selectedRepo = repo }
                        ) {
                            Text(
                                text = repo.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DOCUMENT TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    docTypes.forEach { type ->
                        val isSelected = selectedDocType == type
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDocTypeChange(type) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = if (isSelected) DevPilotCyan else TextMutedDark,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                DevPilotButton(
                    text = "Generate $selectedDocType",
                    icon = Icons.Filled.AutoAwesome,
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    onClick = {
                        val repo = selectedRepo ?: repositories.firstOrNull()
                        if (repo != null) {
                            onGenerateDocs(repo.name, repo.description)
                        }
                    },
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "generate_docs_button"
                )
            }
        }

        // Generated Markdown Preview
        if (generatedDoc != null && !isLoading) {
            item {
                DevPilotCard(
                    shape = RoundedCornerShape(8.dp),
                    borderColor = DevPilotSuccess.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DevPilotStatusDotBadge(
                            label = "Generated $selectedDocType",
                            dotColor = DevPilotSuccess
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedDoc))
                                Toast.makeText(context, "Copied documentation to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = DevPilotSuccess, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    CodeBlockCard(code = generatedDoc, language = "Markdown")
                }
            }
        }
    }
}
