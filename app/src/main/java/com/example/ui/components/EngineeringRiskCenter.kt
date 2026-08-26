package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.EngineeringRiskEntity
import com.example.data.model.RiskSeverity
import com.example.ui.theme.*

@Composable
fun EngineeringRiskCenter(
    risks: List<EngineeringRiskEntity>,
    onConvertRiskToTask: (EngineeringRiskEntity) -> Unit,
    onResolveRisk: (String) -> Unit,
    onInspectLocation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val activeRisks = risks.filter { !it.isResolved }
    val filteredRisks = when (selectedFilter) {
        "CRITICAL" -> activeRisks.filter { it.severity == RiskSeverity.CRITICAL }
        "HIGH" -> activeRisks.filter { it.severity == RiskSeverity.HIGH }
        "SECURITY" -> activeRisks.filter { it.category == "Security" }
        else -> activeRisks
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("engineering_risk_center"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, RoseError.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RoseError.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = RoseError,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ENGINEERING RISK CENTER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoseError
                        )
                        Text(
                            text = "${activeRisks.size} active architectural & security flags",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontSize = 10.sp
                        )
                    }
                }

                // Severity Counts Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF450A0A)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(RoseError)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${activeRisks.count { it.severity == RiskSeverity.CRITICAL }} Critical",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoseError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("ALL", "CRITICAL", "HIGH", "SECURITY").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSelected) CyanPrimary else BorderDark),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanPrimary else TextSecondaryDark,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Risk Cards List
            if (filteredRisks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Zero unresolved risks in this filter category.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldSuccess
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredRisks.forEach { risk ->
                        RiskItemCard(
                            risk = risk,
                            onConvertToTask = { onConvertRiskToTask(risk) },
                            onResolve = { onResolveRisk(risk.id) },
                            onInspect = { onInspectLocation(risk.location) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RiskItemCard(
    risk: EngineeringRiskEntity,
    onConvertToTask: () -> Unit,
    onResolve: () -> Unit,
    onInspect: () -> Unit
) {
    val severityColor = when (risk.severity) {
        RiskSeverity.CRITICAL -> RoseError
        RiskSeverity.HIGH -> AmberWarning
        RiskSeverity.MEDIUM -> CyanPrimary
        RiskSeverity.LOW -> EmeraldSuccess
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF131A2A),
        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = severityColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = risk.severity.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = severityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = risk.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "${risk.estimatedEffortMinutes}m fix",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = risk.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            // File citation / location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onInspect() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = risk.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Impact: ${risk.impact}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Convert to Task & Resolve
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onResolve,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Resolve", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                }
                Spacer(modifier = Modifier.width(4.dp))
                FilledTonalButton(
                    onClick = onConvertToTask,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyanPrimary.copy(alpha = 0.15f),
                        contentColor = CyanPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Filled.PlaylistAddCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Task", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
