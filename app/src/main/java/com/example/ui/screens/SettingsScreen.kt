package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    user: UserEntity?,
    githubUsername: String,
    onUsernameChange: (String) -> Unit,
    isSyncing: Boolean,
    onSyncGithub: (token: String?) -> Unit
) {
    val context = LocalContext.current
    var tokenInput by remember { mutableStateOf("") }
    val isDemoMode = user?.isDemoUser ?: true

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
                title = "Settings & Integrations",
                subtitle = "GitHub OAuth, Gemini AI credentials & workspace configuration",
                breadcrumb = "WORKSPACE / DEV PILOT / SETTINGS"
            )
        }

        // Profile Summary
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.dp, DevPilotCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.username?.take(2)?.uppercase() ?: "DP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DevPilotCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = user?.name ?: "Alex Chen",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@${user?.username ?: "alex-developer"} • ${user?.email ?: "alex.chen@devpilot.io"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = user?.bio ?: "Full-stack architect & OSS builder",
                            style = MaterialTheme.typography.bodySmall,
                            color = DevPilotCyan,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // GitHub Sync Configuration
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                borderColor = DevPilotCyan.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = DevPilotCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GitHub Account Connection", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    DevPilotStatusDotBadge(
                        label = if (isDemoMode) "Demo Mode" else "Live Connected",
                        dotColor = if (isDemoMode) DevPilotViolet else DevPilotSuccess
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = githubUsername,
                    onValueChange = onUsernameChange,
                    label = { Text("GitHub Username", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("github_username_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("Personal Access Token (Optional for private repos)", fontSize = 12.sp) },
                    placeholder = { Text("ghp_xxxxxxxxxxxx", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                DevPilotButton(
                    text = "Sync GitHub Repositories",
                    icon = Icons.Filled.Sync,
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.MEDIUM,
                    onClick = {
                        onSyncGithub(tokenInput.ifBlank { null })
                        Toast.makeText(context, "Synchronizing GitHub repositories...", Toast.LENGTH_SHORT).show()
                    },
                    isLoading = isSyncing,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "sync_github_button"
                )
            }
        }

        // Gemini AI Engine Status
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = DevPilotViolet, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini AI Intelligence Engine", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    DevPilotStatusDotBadge(
                        label = "Active",
                        dotColor = DevPilotSuccess
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Model: Gemini 3.5 Flash (60s timeout, REST pipeline with robust fallback)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "API Key: Configured securely via Secrets & BuildConfig",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // App Information
        item {
            DevPilotCard(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DevPilot v1.0.0 (Production Build)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Architected with Jetpack Compose, Room ORM, and Gemini 3.5 Flash.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark
                )
            }
        }
    }
}
