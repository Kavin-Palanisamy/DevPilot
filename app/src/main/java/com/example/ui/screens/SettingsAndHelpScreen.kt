package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Settings, Appearance (Light/Dark), GitHub Connection, and Workflow Guide.
 */
@Composable
fun SettingsAndHelpScreen(
    user: UserEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    githubUsername: String,
    onUpdateGithubUsername: (String) -> Unit,
    onSyncGitHub: () -> Unit,
    isSyncing: Boolean,
    onSignOut: () -> Unit
) {
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }
    var tokenInput by remember { mutableStateOf("ghp_••••••••••••••••••••••••") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Page Title
        item {
            Column {
                Text(
                    text = "Settings & Help",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Manage your preferences, connections, and learn the DevPilot workflow.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Appearance / Theme Section
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Appearance & Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Customize the interface style to match your coding environment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Dark Mode Option
                    Surface(
                        onClick = { onToggleDarkMode(true) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDarkMode) DevPilotCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            if (isDarkMode) 1.5.dp else 1.dp,
                            if (isDarkMode) DevPilotCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.weight(1f).testTag("theme_dark_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DarkMode,
                                contentDescription = null,
                                tint = if (isDarkMode) DevPilotCyan else TextMutedDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isDarkMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDarkMode) DevPilotCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Light Mode Option
                    Surface(
                        onClick = { onToggleDarkMode(false) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isDarkMode) DevPilotCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            if (!isDarkMode) 1.5.dp else 1.dp,
                            if (!isDarkMode) DevPilotCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.weight(1f).testTag("theme_light_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LightMode,
                                contentDescription = null,
                                tint = if (!isDarkMode) DevPilotCyan else TextMutedDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Light Mode",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (!isDarkMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isDarkMode) DevPilotCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 2. GitHub Connection Section
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GitHub Integration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DevPilotSuccess.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DevPilotSuccess.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Connected",
                            style = MaterialTheme.typography.labelSmall,
                            color = DevPilotSuccess,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("GitHub Username", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = githubUsername,
                    onValueChange = onUpdateGithubUsername,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_github_username")
                )

                Spacer(modifier = Modifier.height(12.dp))

                DevPilotButton(
                    text = "Sync Repositories Now",
                    variant = DevPilotButtonVariant.SECONDARY,
                    size = DevPilotButtonSize.SMALL,
                    icon = Icons.Filled.Sync,
                    isLoading = isSyncing,
                    onClick = onSyncGitHub,
                    testTag = "settings_sync_github_button"
                )
            }
        }

        // 3. AI Engine Configuration
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Model Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DevPilotCyan.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Gemini 3.5 Flash",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = DevPilotCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "High-speed reasoning model powering architecture indexing, plan decomposition, error diagnosis, and PR reviews.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4. The 6-Stage DevPilot Workflow Guide
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DevPilot 6-Stage Workflow Guide",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "How to maximize your engineering throughput using the core workflow:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                val guideItems = listOf(
                    "1. Understand" to "View architecture maps, technology stacks, entry points, and ask interactive questions about any repo component.",
                    "2. Plan" to "Input feature goals to get instant AI-decomposed implementation plans with estimated task durations.",
                    "3. Build" to "Execute tasks with focus timers, track subtask checklists, and use AI code assistants for clean implementations.",
                    "4. Debug" to "Paste stack traces or error logs to obtain root causes, exact file locations, corrected code, and automated fix tasks.",
                    "5. Review" to "Run PR quality and security gates, verify test coverage, and catch performance bottlenecks prior to merge.",
                    "6. Improve" to "Continuously resolve ranked technical debt and architectural recommendations to maintain high code health."
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    guideItems.forEach { (title, desc) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DevPilotCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Account & Sign Out
        item {
            DevPilotCard(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = user?.name ?: "Alex Chen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user?.email ?: "alex.chen@devpilot.io",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedDark
                        )
                    }

                    DevPilotButton(
                        text = "Sign Out",
                        variant = DevPilotButtonVariant.DESTRUCTIVE,
                        size = DevPilotButtonSize.SMALL,
                        onClick = onSignOut,
                        testTag = "settings_sign_out_button"
                    )
                }
            }
        }
    }
}
