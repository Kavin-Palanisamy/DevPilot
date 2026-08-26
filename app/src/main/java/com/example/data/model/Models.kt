package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class TaskStatus {
    BACKLOG, TODO, IN_PROGRESS, REVIEW, COMPLETED
}

enum class FocusSessionType {
    POMODORO, SHORT_BREAK, LONG_BREAK, CUSTOM
}

enum class AICategory {
    CODING_ASSISTANT, DEBUGGER, CODE_REVIEW, DOCS_GEN, TASK_PLANNER, GENERAL
}

enum class ActivityType {
    COMMIT, PR, ISSUE, FOCUS, TASK_DONE, REVIEW
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default",
    val githubId: Long = 1084291,
    val username: String = "alex-developer",
    val name: String = "Alex Chen",
    val email: String = "alex.chen@devpilot.io",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
    val bio: String = "Full-stack architect & OSS builder. Writing Kotlin, Python & TypeScript.",
    val publicRepos: Int = 14,
    val followers: Int = 382,
    val codingStreakDays: Int = 18,
    val productivityScore: Int = 89,
    val isDemoUser: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val fullName: String,
    val description: String,
    val language: String,
    val languageColor: String,
    val stars: Int = 0,
    val forks: Int = 0,
    val openIssues: Int = 0,
    val openPrs: Int = 0,
    val defaultBranch: String = "main",
    val healthScore: Int = 82,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val repoUrl: String = "https://github.com",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "repository_analyses",
    foreignKeys = [
        ForeignKey(
            entity = RepositoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["repositoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["repositoryId"], unique = true)]
)
data class RepositoryAnalysisEntity(
    @PrimaryKey val id: String,
    val repositoryId: String,
    val projectType: String,
    val primaryLanguage: String,
    val framework: String,
    val database: String,
    val testing: String,
    val architecture: String,
    val overallHealthScore: Int,
    val documentationScore: Int,
    val testingScore: Int,
    val codeStructureScore: Int,
    val gitActivityScore: Int,
    val securityScore: Int,
    val ciCdScore: Int,
    val summaryText: String,
    val findingsJson: String, // Stored as newline or pipe separated items
    val recommendationsJson: String,
    val analyzedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["repositoryId"]), Index(value = ["status"])]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val repositoryId: String? = null,
    val repositoryName: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val deadline: Long? = null,
    val estimatedMinutes: Int = 60,
    val actualMinutes: Int = 0,
    val labelsCsv: String = "feature",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class SubTaskEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(
    tableName = "focus_sessions",
    indices = [Index(value = ["taskId"])]
)
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val taskId: String? = null,
    val taskTitle: String? = null,
    val durationMinutes: Int = 25,
    val sessionType: FocusSessionType = FocusSessionType.POMODORO,
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "ai_conversations")
data class AIConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: AICategory = AICategory.CODING_ASSISTANT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "ai_messages",
    foreignKeys = [
        ForeignKey(
            entity = AIConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversationId"])]
)
data class AIMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val codeSnippet: String? = null,
    val codeLanguage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val dateKey: String, // e.g. "2026-08-25"
    val availableHours: Float = 6.0f,
    val highPriorityFocus: String = "Authentication & Security Audit",
    val scheduleItemsJson: String, // JSON / Formatted items: Time | Task | Category
    val aiProductivityTip: String,
    val completedCount: Int = 0,
    val totalItemsCount: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "developer_activities")
data class DeveloperActivityEntity(
    @PrimaryKey val id: String,
    val type: ActivityType,
    val title: String,
    val repoName: String,
    val extraInfo: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class CodeReviewResult(
    val criticalCount: Int,
    val warningCount: Int,
    val suggestionCount: Int,
    val securityRisk: String, // Low, Medium, High
    val performanceRating: String, // Excellent, Good, Needs Improvement
    val maintainabilityRating: String,
    val summary: String,
    val findings: List<ReviewFinding>
)

data class ReviewFinding(
    val line: String,
    val type: String, // Security, Performance, Bug, Style
    val description: String,
    val suggestion: String
)

data class DebugAnalysisResult(
    val errorType: String,
    val rootCause: String,
    val likelyLocation: String,
    val explanation: String,
    val solutionSteps: List<String>,
    val correctedCode: String,
    val preventionTips: String
)

data class DecomposedSubtask(
    val title: String,
    val estimatedMinutes: Int,
    val priority: TaskPriority
)

enum class RiskSeverity {
    CRITICAL, HIGH, MEDIUM, LOW
}

@Entity(tableName = "engineering_risks")
data class EngineeringRiskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Security", "Testing", "Performance", "Architecture", "Dependencies"
    val severity: RiskSeverity,
    val location: String, // e.g. "backend/auth/service.py:142"
    val impact: String,
    val suggestedFix: String,
    val repoId: String,
    val repoName: String,
    val estimatedEffortMinutes: Int = 45,
    val isResolved: Boolean = false,
    val detectedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_goals")
data class ProjectGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val progressPercentage: Int = 0,
    val category: String = "Core Engineering",
    val targetDeadline: Long? = null,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, PAUSED
    val milestoneCount: Int = 4,
    val completedMilestoneCount: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ProjectGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goalId"])]
)
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val goalId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val targetDate: String = "Q3 2026",
    val orderIndex: Int = 0
)

@Entity(tableName = "organizations")
data class OrganizationWorkspaceEntity(
    @PrimaryKey val id: String = "org_acme",
    val name: String = "Acme Engineering Cloud",
    val slug: String = "acme-engineering",
    val currentRole: String = "Admin",
    val planType: String = "PRO",
    val membersCount: Int = 8,
    val aiQuotaUsed: Int = 7420,
    val aiQuotaMax: Int = 10000,
    val repoQuotaUsed: Int = 6,
    val repoQuotaMax: Int = 20,
    val monthlyRenewalDate: String = "Sep 1, 2026"
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String, // Admin, Tech Lead, Senior Engineer, Reviewer
    val avatarInitials: String,
    val lastActive: String = "10m ago"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val type: String, // "ALERT", "REVIEW", "TASK", "SYSTEM"
    val isRead: Boolean = false,
    val actionRoute: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class SourceCitation(
    val filePath: String,
    val line: Int? = null,
    val description: String,
    val snippet: String? = null
)

data class CodebaseModuleNode(
    val id: String,
    val name: String,
    val layer: String, // "Presentation", "API Gateway", "Domain Service", "Storage & Cache", "Infrastructure"
    val technology: String,
    val description: String,
    val dependencies: List<String>,
    val healthScore: Int = 90
)

data class NextBestAction(
    val id: String,
    val title: String,
    val priority: TaskPriority,
    val estimatedEffort: String,
    val reasonBullets: List<String>,
    val impact: String,
    val targetRepoId: String,
    val targetRepoName: String,
    val actionType: String,
    val sourceFiles: List<String>
)

data class ProductionReadinessAudit(
    val overallScore: Int,
    val testingScore: Int,
    val securityScore: Int,
    val documentationScore: Int,
    val errorHandlingScore: Int,
    val cicdScore: Int,
    val observabilityScore: Int,
    val recommendations: List<String>
)

enum class AuthState {
    LANDING,
    LOGIN,
    SIGN_UP,
    ONBOARDING,
    AUTHENTICATED
}

enum class WorkflowStage(val label: String, val shortDesc: String) {
    UNDERSTAND("Understand", "Architecture & stack"),
    PLAN("Plan", "Feature decomposition"),
    BUILD("Build", "Execution & code"),
    DEBUG("Debug", "Error root-cause analysis"),
    REVIEW("Review", "Quality & security gate"),
    IMPROVE("Improve", "Targeted refactors")
}

data class ProjectPlanStep(
    val id: String,
    val stepNumber: Int,
    val title: String,
    val description: String,
    val estimatedMinutes: Int = 45,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isAddedToTasks: Boolean = false
)

data class ProjectImprovement(
    val id: String,
    val title: String,
    val category: String,
    val priority: TaskPriority,
    val estimatedEffort: String,
    val description: String,
    val impactedFiles: List<String>,
    val isConvertedToTask: Boolean = false
)


