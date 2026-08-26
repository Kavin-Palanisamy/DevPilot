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
import com.example.data.model.OrganizationWorkspaceEntity
import com.example.data.model.TeamMemberEntity
import com.example.ui.theme.*

@Composable
fun OrganizationWorkspaceDialog(
    organization: OrganizationWorkspaceEntity?,
    teamMembers: List<TeamMemberEntity>,
    onInviteMember: (String, String, String) -> Unit,
    onOpenUpgrade: () -> Unit,
    onDismiss: () -> Unit
) {
    var showInviteForm by remember { mutableStateOf(false) }
    var inviteName by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }
    var inviteRole by remember { mutableStateOf("Developer") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
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
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Business,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = organization?.name ?: "Acme Cloud Engineering",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Organization Workspace • ${organization?.planType ?: "PRO"} Plan",
                                style = MaterialTheme.typography.labelSmall,
                                color = VioletSecondary,
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
                    // Quota usage card
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
                                        text = "MONTHLY USAGE & LIMITS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMutedDark
                                    )
                                    TextButton(onClick = onOpenUpgrade, contentPadding = PaddingValues(0.dp)) {
                                        Text("Upgrade Plan →", style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val aiUsed = organization?.aiQuotaUsed ?: 6840
                                val aiMax = organization?.aiQuotaMax ?: 10000
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("AI Request Quota", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                                    Text("$aiUsed / $aiMax requests", style = MaterialTheme.typography.labelSmall, color = CyanPrimary, fontFamily = FontFamily.Monospace)
                                }
                                LinearProgressIndicator(
                                    progress = { aiUsed.toFloat() / aiMax.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CyanPrimary,
                                    trackColor = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                val repoUsed = organization?.repoQuotaUsed ?: 4
                                val repoMax = organization?.repoQuotaMax ?: 20
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Connected Repositories", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                                    Text("$repoUsed / $repoMax repos", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess, fontFamily = FontFamily.Monospace)
                                }
                                LinearProgressIndicator(
                                    progress = { repoUsed.toFloat() / repoMax.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = EmeraldSuccess,
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    // Team Roster Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEAM MEMBERS (${teamMembers.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { showInviteForm = !showInviteForm },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyanPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invite Member", style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
                            }
                        }
                    }

                    // Invite form if open
                    if (showInviteForm) {
                        item {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
                                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Invite New Engineer", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    OutlinedTextField(
                                        value = inviteName,
                                        onValueChange = { inviteName = it },
                                        label = { Text("Full Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = inviteEmail,
                                        onValueChange = { inviteEmail = it },
                                        label = { Text("Email Address") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { showInviteForm = false }) { Text("Cancel") }
                                        Button(
                                            onClick = {
                                                if (inviteName.isNotBlank() && inviteEmail.isNotBlank()) {
                                                    onInviteMember(inviteName, inviteEmail, inviteRole)
                                                    inviteName = ""
                                                    inviteEmail = ""
                                                    showInviteForm = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = BackgroundDark)
                                        ) {
                                            Text("Send Invite", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(teamMembers) { member ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(VioletSecondary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.avatarInitials,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VioletLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = member.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = member.email, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = member.role,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanPrimary,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

@Composable
fun UpgradeSubscriptionDialog(
    currentPlan: String,
    onSelectPlan: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Upgrade DevPilot Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Unlock unlimited AI models & team seats",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Plan 1: Pro Tier
                    item {
                        PlanCard(
                            name = "Developer Pro",
                            price = "$29 / mo",
                            badge = "MOST POPULAR",
                            isCurrent = currentPlan == "PRO",
                            features = listOf(
                                "10,000 Gemini 2.5 Flash / Pro requests/mo",
                                "Unlimited connected GitHub repositories",
                                "Continuous Risk & Tech Debt automated audits",
                                "AI Daily Planner & Pomodoro focus integration"
                            ),
                            accentColor = CyanPrimary,
                            onChoose = { onSelectPlan("PRO") }
                        )
                    }

                    // Plan 2: Team Tier
                    item {
                        PlanCard(
                            name = "Team & Organization",
                            price = "$79 / mo",
                            badge = "COLLABORATION",
                            isCurrent = currentPlan == "TEAM",
                            features = listOf(
                                "50,000 AI requests with multi-engineer seats",
                                "PR review assistant with automated CI/CD comments",
                                "Shared architectural maps & milestone tracking",
                                "SAML SSO & SOC2 Type II audit logging"
                            ),
                            accentColor = VioletSecondary,
                            onChoose = { onSelectPlan("TEAM") }
                        )
                    }

                    // Plan 3: Enterprise
                    item {
                        PlanCard(
                            name = "Enterprise Dedicated",
                            price = "Custom Pricing",
                            badge = "ON-PREM / VPC",
                            isCurrent = currentPlan == "ENTERPRISE",
                            features = listOf(
                                "Dedicated VPC hosting & private Gemini model fine-tuning",
                                "Zero data retention & custom security policies",
                                "24/7 dedicated solutions engineering support"
                            ),
                            accentColor = EmeraldSuccess,
                            onChoose = { onSelectPlan("ENTERPRISE") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    name: String,
    price: String,
    badge: String,
    isCurrent: Boolean,
    features: List<String>,
    accentColor: Color,
    onChoose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(if (isCurrent) 2.dp else 1.dp, if (isCurrent) accentColor else BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text(text = price, style = MaterialTheme.typography.labelLarge, color = accentColor, fontWeight = FontWeight.Bold)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isCurrent) "ACTIVE PLAN" else badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            features.forEach { feature ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onChoose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) Color(0xFF1E293B) else accentColor,
                    contentColor = if (isCurrent) TextPrimaryDark else BackgroundDark
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isCurrent
            ) {
                Text(
                    text = if (isCurrent) "Current Active Plan" else "Select $name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
