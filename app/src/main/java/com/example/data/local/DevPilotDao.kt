package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DevPilotDao {

    // --- User ---
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Repositories ---
    @Query("SELECT * FROM repositories ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllRepositories(): Flow<List<RepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE id = :repoId LIMIT 1")
    fun getRepositoryById(repoId: String): Flow<RepositoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: RepositoryEntity)

    @Update
    suspend fun updateRepository(repository: RepositoryEntity)

    @Query("DELETE FROM repositories WHERE id = :repoId")
    suspend fun deleteRepository(repoId: String)

    // --- Repository Analyses ---
    @Query("SELECT * FROM repository_analyses WHERE repositoryId = :repoId LIMIT 1")
    fun getAnalysisForRepository(repoId: String): Flow<RepositoryAnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: RepositoryAnalysisEntity)

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY priority DESC, createdAt DESC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE repositoryId = :repoId ORDER BY createdAt DESC")
    fun getTasksForRepo(repoId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: String): Flow<TaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    // --- Subtasks ---
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY orderIndex ASC")
    fun getSubtasksForTask(taskId: String): Flow<List<SubTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<SubTaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubTaskEntity)

    @Update
    suspend fun updateSubtask(subtask: SubTaskEntity)

    @Query("DELETE FROM subtasks WHERE id = :subtaskId")
    suspend fun deleteSubtask(subtaskId: String)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)

    // --- AI Conversations & Messages ---
    @Query("SELECT * FROM ai_conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<AIConversationEntity>>

    @Query("SELECT * FROM ai_messages WHERE conversationId = :convoId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convoId: String): Flow<List<AIMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: AIConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AIMessageEntity)

    @Query("DELETE FROM ai_conversations WHERE id = :convoId")
    suspend fun deleteConversation(convoId: String)

    // --- Daily Plans ---
    @Query("SELECT * FROM daily_plans WHERE dateKey = :dateKey LIMIT 1")
    fun getDailyPlan(dateKey: String): Flow<DailyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyPlan(plan: DailyPlanEntity)

    // --- Developer Activities ---
    @Query("SELECT * FROM developer_activities ORDER BY timestamp DESC LIMIT 30")
    fun getRecentActivities(): Flow<List<DeveloperActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: DeveloperActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<DeveloperActivityEntity>)

    // --- Engineering Risks & Tech Debt ---
    @Query("SELECT * FROM engineering_risks ORDER BY isResolved ASC, severity ASC, detectedAt DESC")
    fun getAllRisks(): Flow<List<EngineeringRiskEntity>>

    @Query("SELECT * FROM engineering_risks WHERE isResolved = 0 ORDER BY severity ASC")
    fun getActiveRisks(): Flow<List<EngineeringRiskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisks(risks: List<EngineeringRiskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisk(risk: EngineeringRiskEntity)

    @Update
    suspend fun updateRisk(risk: EngineeringRiskEntity)

    @Query("DELETE FROM engineering_risks WHERE id = :riskId")
    suspend fun deleteRisk(riskId: String)

    // --- Project Goals & Milestones ---
    @Query("SELECT * FROM project_goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<ProjectGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<ProjectGoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: ProjectGoalEntity)

    @Update
    suspend fun updateGoal(goal: ProjectGoalEntity)

    @Query("SELECT * FROM milestones WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getMilestonesForGoal(goalId: String): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)

    // --- Organizations & Team Members ---
    @Query("SELECT * FROM organizations LIMIT 1")
    fun getOrganization(): Flow<OrganizationWorkspaceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(org: OrganizationWorkspaceEntity)

    @Update
    suspend fun updateOrganization(org: OrganizationWorkspaceEntity)

    @Query("SELECT * FROM team_members")
    fun getTeamMembers(): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMembers(members: List<TeamMemberEntity>)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT 20")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun dismissNotification(id: String)
}

