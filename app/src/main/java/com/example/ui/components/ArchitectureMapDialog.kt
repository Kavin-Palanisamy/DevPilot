package com.example.ui.components

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CodebaseModuleNode
import com.example.data.model.ProductionReadinessAudit
import com.example.ui.theme.*

@Composable
fun ArchitectureMapDialog(
    repoName: String,
    modules: List<CodebaseModuleNode>,
    readinessAudit: ProductionReadinessAudit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VioletSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Hub,
                                contentDescription = null,
                                tint = VioletSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Codebase Architecture Map",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$repoName • Modular Clean Architecture",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Production Readiness Audit Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PRODUCTION READINESS SCORE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMutedDark
                                    )
                                    Text(
                                        text = "${readinessAudit.overallScore}/100",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (readinessAudit.overallScore >= 90) EmeraldSuccess else CyanPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress bars grid
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    ScoreProgressBar("Testing & Mocks", readinessAudit.testingScore, EmeraldSuccess)
                                    ScoreProgressBar("Security & Auth", readinessAudit.securityScore, AmberWarning)
                                    ScoreProgressBar("Documentation", readinessAudit.documentationScore, CyanPrimary)
                                    ScoreProgressBar("CI/CD Pipeline", readinessAudit.cicdScore, VioletSecondary)
                                }
                            }
                        }
                    }

                    // 2. Layer & Module Nodes Section
                    item {
                        Text(
                            text = "ARCHITECTURE LAYERS & TOPOLOGY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(modules) { module ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
                            border = BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = module.layer.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    HealthScorePill(module.healthScore)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = module.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )

                                Text(
                                    text = "Tech: ${module.technology}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VioletLight,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = module.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )

                                if (module.dependencies.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Depends on: ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark,
                                            fontSize = 10.sp
                                        )
                                        module.dependencies.forEach { dep ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF0F172A),
                                                modifier = Modifier.padding(end = 4.dp)
                                            ) {
                                                Text(
                                                    text = dep,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CyanLight,
                                                    fontSize = 9.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = BackgroundDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close Architecture Map", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ScoreProgressBar(label: String, score: Int, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(110.dp),
            fontSize = 11.sp
        )
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = tint,
            trackColor = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$score%",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}
