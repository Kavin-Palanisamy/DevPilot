package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.CodeReviewResult
import com.example.data.model.DebugAnalysisResult
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWorkspaceScreen(
    inputCode: String,
    onCodeChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    assistantResult: String?,
    isAiLoading: Boolean,
    onExecuteAction: (String) -> Unit,
    // Debugger props
    debugError: String,
    onDebugErrorChange: (String) -> Unit,
    debugStackTrace: String,
    onDebugStackTraceChange: (String) -> Unit,
    debugResult: DebugAnalysisResult?,
    isDebugging: Boolean,
    onRunDebugger: () -> Unit,
    // Code Review props
    reviewResult: CodeReviewResult?,
    isReviewing: Boolean,
    onRunCodeReview: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Assistant, 1: Debugger, 2: Code Review

    val languages = listOf("Kotlin", "Python", "TypeScript", "Rust", "Go", "Java", "SQL")

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
                title = "AI Developer Studio",
                subtitle = "Code intelligence, error diagnostics & automated security audit",
                breadcrumb = "WORKSPACE / DEV PILOT / AI STUDIO"
            )
        }

        // Mode Segmented Switcher
        item {
            DevPilotSegmentedTabGroup(
                items = listOf(0, 1, 2),
                selectedItem = activeTab,
                onItemSelected = { activeTab = it },
                labelProvider = {
                    when (it) {
                        0 -> "Code Copilot"
                        1 -> "AI Debugger"
                        else -> "Code Review"
                    }
                },
                iconProvider = {
                    when (it) {
                        0 -> Icons.Filled.AutoAwesome
                        1 -> Icons.Filled.BugReport
                        else -> Icons.Filled.FactCheck
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- TAB 0: AI CODE ASSISTANT ---
        if (activeTab == 0) {
            item {
                DevPilotCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    // Language selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TARGET LANGUAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            languages.take(4).forEach { lang ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (language == lang) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (language == lang) DevPilotCyan else MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.clickable { onLanguageChange(lang) }
                                ) {
                                    Text(
                                        text = lang,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (language == lang) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Monospace Code Editor
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = onCodeChange,
                        label = { Text("Code / Query Context", fontSize = 12.sp) },
                        textStyle = CodeTextStyle,
                        minLines = 4,
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DevPilotCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = CodeBg,
                            unfocusedContainerColor = CodeBg
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_code_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action Buttons
                    Text(
                        text = "AI ACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DevPilotButton(
                                text = "Explain Code",
                                icon = Icons.Filled.AutoAwesome,
                                variant = DevPilotButtonVariant.PRIMARY,
                                size = DevPilotButtonSize.SMALL,
                                onClick = { onExecuteAction("EXPLAIN") },
                                isLoading = isAiLoading,
                                modifier = Modifier.weight(1f),
                                testTag = "action_explain"
                            )
                            DevPilotButton(
                                text = "Refactor",
                                icon = Icons.Filled.Code,
                                variant = DevPilotButtonVariant.SECONDARY,
                                size = DevPilotButtonSize.SMALL,
                                onClick = { onExecuteAction("REFACTOR") },
                                isLoading = isAiLoading,
                                modifier = Modifier.weight(1f),
                                testTag = "action_refactor"
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DevPilotButton(
                                text = "Optimize",
                                variant = DevPilotButtonVariant.OUTLINE,
                                size = DevPilotButtonSize.SMALL,
                                onClick = { onExecuteAction("OPTIMIZE") },
                                isLoading = isAiLoading,
                                modifier = Modifier.weight(1f)
                            )
                            DevPilotButton(
                                text = "Gen Tests",
                                variant = DevPilotButtonVariant.OUTLINE,
                                size = DevPilotButtonSize.SMALL,
                                onClick = { onExecuteAction("TESTS") },
                                isLoading = isAiLoading,
                                modifier = Modifier.weight(1f)
                            )
                            DevPilotButton(
                                text = "Convert",
                                variant = DevPilotButtonVariant.OUTLINE,
                                size = DevPilotButtonSize.SMALL,
                                onClick = { onExecuteAction("CONVERT") },
                                isLoading = isAiLoading,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (isAiLoading) {
                item {
                    DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DevPilotCyan, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Analyzing context with Gemini 3.5 Flash...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DevPilotCyan
                            )
                        }
                    }
                }
            }

            if (assistantResult != null && !isAiLoading) {
                item {
                    DevPilotCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = DevPilotCyan.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DevPilotStatusDotBadge(
                                label = "AI Response",
                                dotColor = DevPilotCyan
                            )
                            Text(
                                text = "Gemini 3.5 Flash",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = TextMutedDark,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = assistantResult,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // --- TAB 1: AI DEBUGGER ---
        if (activeTab == 1) {
            item {
                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = debugError,
                        onValueChange = onDebugErrorChange,
                        label = { Text("Error / Exception Message *", fontSize = 12.sp) },
                        placeholder = { Text("e.g. NullPointerException or ECONNREFUSED", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DevPilotDanger,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("debug_error_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = debugStackTrace,
                        onValueChange = onDebugStackTraceChange,
                        label = { Text("Stack Trace or Logs", fontSize = 12.sp) },
                        textStyle = CodeTextStyle,
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DevPilotDanger,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = CodeBg,
                            unfocusedContainerColor = CodeBg
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DevPilotButton(
                        text = "Diagnose Error with AI",
                        icon = Icons.Filled.BugReport,
                        variant = DevPilotButtonVariant.DESTRUCTIVE,
                        size = DevPilotButtonSize.MEDIUM,
                        onClick = onRunDebugger,
                        enabled = debugError.isNotBlank(),
                        isLoading = isDebugging,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "run_debugger_button"
                    )
                }
            }

            if (debugResult != null && !isDebugging) {
                item {
                    DevPilotCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = DevPilotDanger.copy(alpha = 0.4f)
                    ) {
                        DevPilotStatusDotBadge(
                            label = "Diagnosis Report",
                            dotColor = DevPilotDanger
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Root Cause: ${debugResult.rootCause}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DevPilotFileBadge(path = debugResult.likelyLocation)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Recommended Solution:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DevPilotCyan
                        )
                        debugResult.solutionSteps.forEach { step ->
                            Text(
                                text = "• $step",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Corrected Code Fix:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DevPilotSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CodeBlockCard(code = debugResult.correctedCode, language = "Kotlin")
                    }
                }
            }
        }

        // --- TAB 2: AI CODE REVIEW ---
        if (activeTab == 2) {
            item {
                DevPilotCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Automated senior engineer code review. Scans for security vulnerabilities, memory bottlenecks, architecture anti-patterns, and unit test deficiencies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DevPilotButton(
                        text = "Audit Current Code",
                        icon = Icons.Filled.FactCheck,
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.MEDIUM,
                        onClick = onRunCodeReview,
                        isLoading = isReviewing,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "run_review_button"
                    )
                }
            }

            if (reviewResult != null && !isReviewing) {
                item {
                    DevPilotCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = DevPilotSuccess.copy(alpha = 0.4f)
                    ) {
                        DevPilotStatusDotBadge(
                            label = "Code Review Audit",
                            dotColor = DevPilotSuccess
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Security Risk", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextMutedDark)
                                    Text(reviewResult.securityRisk, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = DevPilotSuccess)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Performance", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = TextMutedDark)
                                    Text(reviewResult.performanceRating, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = DevPilotCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Line Findings & Recommendations:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        reviewResult.findings.forEach { finding ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        DevPilotFileBadge(path = finding.line)
                                        DevPilotStatusDotBadge(label = finding.type, dotColor = DevPilotWarning)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(finding.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("💡 Suggestion: ${finding.suggestion}", style = MaterialTheme.typography.labelSmall, color = DevPilotSuccess)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
