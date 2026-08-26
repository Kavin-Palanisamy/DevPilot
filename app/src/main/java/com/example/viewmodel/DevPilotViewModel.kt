package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DevPilotDatabase
import com.example.data.model.*
import com.example.data.repository.DevPilotRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuickAction(
    val title: String,
    val description: String,
    val iconName: String,
    val actionType: String
)

class DevPilotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DevPilotRepository

    init {
        val db = DevPilotDatabase.getDatabase(application)
        repository = DevPilotRepository(db.devPilotDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // --- State Streams ---
    val user: StateFlow<UserEntity?> = repository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val repositories: StateFlow<List<RepositoryEntity>> = repository.repositories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.focusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalFocusMinutes: StateFlow<Int> = repository.totalFocusMinutes
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentActivities: StateFlow<List<DeveloperActivityEntity>> = repository.recentActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyPlan: StateFlow<DailyPlanEntity?> = repository.getTodayPlan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val conversations: StateFlow<List<AIConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRisks: StateFlow<List<EngineeringRiskEntity>> = repository.allRisks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRisks: StateFlow<List<EngineeringRiskEntity>> = repository.activeRisks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<ProjectGoalEntity>> = repository.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val organization: StateFlow<OrganizationWorkspaceEntity?> = repository.organization
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val teamMembers: StateFlow<List<TeamMemberEntity>> = repository.teamMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Theme & Appearance State ---
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    // --- Dialogs & Sheets State ---
    private val _isNotificationSheetOpen = MutableStateFlow(false)
    val isNotificationSheetOpen: StateFlow<Boolean> = _isNotificationSheetOpen.asStateFlow()

    private val _isOrgModalOpen = MutableStateFlow(false)
    val isOrgModalOpen: StateFlow<Boolean> = _isOrgModalOpen.asStateFlow()

    private val _isUpgradeModalOpen = MutableStateFlow(false)
    val isUpgradeModalOpen: StateFlow<Boolean> = _isUpgradeModalOpen.asStateFlow()

    private val _selectedCitation = MutableStateFlow<SourceCitation?>(null)
    val selectedCitation: StateFlow<SourceCitation?> = _selectedCitation.asStateFlow()

    fun toggleNotificationSheet() { _isNotificationSheetOpen.value = !_isNotificationSheetOpen.value }
    fun toggleOrgModal() { _isOrgModalOpen.value = !_isOrgModalOpen.value }
    fun toggleUpgradeModal() { _isUpgradeModalOpen.value = !_isUpgradeModalOpen.value }

    fun inspectCitation(citation: SourceCitation) {
        _selectedCitation.value = citation
    }

    fun closeCitationInspector() {
        _selectedCitation.value = null
    }

    // --- Engineering Risk Actions ---
    fun resolveRisk(riskId: String) {
        viewModelScope.launch {
            repository.resolveRisk(riskId)
        }
    }

    fun convertRiskToTask(risk: EngineeringRiskEntity) {
        viewModelScope.launch {
            repository.convertRiskToTask(risk)
        }
    }

    // --- Goal & Milestone Actions ---
    fun toggleMilestone(milestone: MilestoneEntity) {
        viewModelScope.launch {
            repository.toggleMilestone(milestone)
        }
    }

    fun getMilestonesForGoal(goalId: String): Flow<List<MilestoneEntity>> = repository.getMilestonesForGoal(goalId)

    fun addGoal(title: String, description: String, category: String, targetDays: Int = 30) {
        viewModelScope.launch {
            repository.addProjectGoal(title, description, category, targetDays)
        }
    }

    // --- Next Best Action & Architecture Inspection ---
    fun getNextBestAction(): NextBestAction {
        return repository.calculateNextBestAction(tasks.value, activeRisks.value, repositories.value)
    }

    fun getProductionReadinessAudit(repoId: String): ProductionReadinessAudit {
        return repository.getProductionReadinessAudit(repoId)
    }

    fun getCodebaseModules(repoId: String): List<CodebaseModuleNode> {
        return repository.getCodebaseModules(repoId)
    }

    // --- Notification Actions ---
    fun markNotificationRead(id: String) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllNotificationsRead() }
    }

    fun dismissNotification(id: String) {
        viewModelScope.launch { repository.dismissNotification(id) }
    }

    // --- Organization & Plan Actions ---
    fun upgradePlan(planType: String) {
        viewModelScope.launch {
            repository.updateOrganizationPlan(planType)
            _isUpgradeModalOpen.value = false
        }
    }

    fun inviteTeamMember(name: String, email: String, role: String) {
        viewModelScope.launch {
            repository.inviteTeamMember(name, email, role)
        }
    }


    // --- Auth & Onboarding State ---
    private val _authState = MutableStateFlow(AuthState.AUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun setAuthState(state: AuthState) {
        _authState.value = state
    }

    fun completeOnboarding(selectedRepoId: String) {
        _selectedRepoId.value = selectedRepoId
        _currentWorkflowStage.value = WorkflowStage.UNDERSTAND
        _currentScreen.value = "workspace"
        _authState.value = AuthState.AUTHENTICATED
    }

    // --- UI Navigation & Tab State ---
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _currentWorkflowStage = MutableStateFlow(WorkflowStage.UNDERSTAND)
    val currentWorkflowStage: StateFlow<WorkflowStage> = _currentWorkflowStage.asStateFlow()

    fun setWorkflowStage(stage: WorkflowStage) {
        _currentWorkflowStage.value = stage
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun navigateToStage(repoId: String, stage: WorkflowStage) {
        _selectedRepoId.value = repoId
        _currentWorkflowStage.value = stage
        _currentScreen.value = "workspace"
    }

    // --- Selected Repo for Detail/Analysis ---
    private val _selectedRepoId = MutableStateFlow<String?>("repo_1")
    val selectedRepoId: StateFlow<String?> = _selectedRepoId.asStateFlow()

    fun selectRepo(repoId: String) {
        _selectedRepoId.value = repoId
        _currentScreen.value = "workspace"
    }

    fun addProject(name: String, description: String, language: String) {
        viewModelScope.launch {
            val newRepo = repository.addProject(name, description, language)
            _selectedRepoId.value = newRepo.id
            _currentWorkflowStage.value = WorkflowStage.UNDERSTAND
            _currentScreen.value = "workspace"
        }
    }

    fun createTaskFromAction(action: NextBestAction) {
        viewModelScope.launch {
            val task = TaskEntity(
                id = "task_${System.currentTimeMillis()}",
                title = action.title,
                description = action.impact,
                repositoryId = action.targetRepoId,
                repositoryName = action.targetRepoName,
                priority = action.priority,
                status = TaskStatus.TODO,
                estimatedMinutes = 45,
                labelsCsv = "ai-recommendation"
            )
            repository.saveTask(task)
        }
    }

    fun askProjectQuestion(question: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val currentRepo = repositories.value.find { it.id == _selectedRepoId.value }
            val answer = repository.askProjectQuestion(currentRepo?.name ?: "devpilot-core", question)
            _aiAssistantResult.value = answer
            _isAiLoading.value = false
        }
    }

    fun getSelectedRepo(): Flow<RepositoryEntity?> = _selectedRepoId.flatMapLatest { id ->
        if (id != null) repository.getRepoById(id) else flowOf(null)
    }

    fun getSelectedRepoAnalysis(): Flow<RepositoryAnalysisEntity?> = _selectedRepoId.flatMapLatest { id ->
        if (id != null) repository.getRepoAnalysis(id) else flowOf(null)
    }


    // --- Focus Mode Timer State ---
    private val _focusTotalSeconds = MutableStateFlow(25 * 60)
    val focusTotalSeconds: StateFlow<Int> = _focusTotalSeconds.asStateFlow()

    private val _focusRemainingSeconds = MutableStateFlow(25 * 60)
    val focusRemainingSeconds: StateFlow<Int> = _focusRemainingSeconds.asStateFlow()

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private val _selectedFocusTask = MutableStateFlow<TaskEntity?>(null)
    val selectedFocusTask: StateFlow<TaskEntity?> = _selectedFocusTask.asStateFlow()

    private var focusTimerJob: Job? = null

    fun selectFocusTask(task: TaskEntity?) {
        _selectedFocusTask.value = task
    }

    fun setFocusDuration(minutes: Int) {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
        _focusTotalSeconds.value = minutes * 60
        _focusRemainingSeconds.value = minutes * 60
    }

    fun toggleFocusTimer() {
        if (_isFocusRunning.value) {
            pauseFocusTimer()
        } else {
            startFocusTimer()
        }
    }

    private fun startFocusTimer() {
        _isFocusRunning.value = true
        focusTimerJob?.cancel()
        focusTimerJob = viewModelScope.launch {
            while (_isFocusRunning.value && _focusRemainingSeconds.value > 0) {
                delay(1000L)
                _focusRemainingSeconds.value -= 1
            }
            if (_focusRemainingSeconds.value <= 0) {
                _isFocusRunning.value = false
                val durationMin = _focusTotalSeconds.value / 60
                repository.recordFocusSession(
                    taskId = _selectedFocusTask.value?.id,
                    taskTitle = _selectedFocusTask.value?.title ?: "Deep Work Focus Session",
                    durationMinutes = durationMin,
                    notes = "Completed focus session on ${selectedFocusTask.value?.repositoryName ?: "Core"}"
                )
            }
        }
    }

    fun pauseFocusTimer() {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
    }

    fun resetFocusTimer() {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
        _focusRemainingSeconds.value = _focusTotalSeconds.value
    }

    fun finishFocusEarly() {
        val elapsed = (_focusTotalSeconds.value - _focusRemainingSeconds.value) / 60
        if (elapsed > 0) {
            viewModelScope.launch {
                repository.recordFocusSession(
                    taskId = _selectedFocusTask.value?.id,
                    taskTitle = _selectedFocusTask.value?.title ?: "Deep Work Focus Session",
                    durationMinutes = elapsed,
                    notes = "Finished focus session ($elapsed mins logged)"
                )
            }
        }
        resetFocusTimer()
    }

    // --- Task Manager Actions ---
    fun updateTaskStatus(task: TaskEntity, newStatus: TaskStatus) {
        viewModelScope.launch {
            repository.updateTaskStatus(task, newStatus)
        }
    }

    fun saveTask(
        title: String,
        description: String,
        repoId: String?,
        repoName: String?,
        priority: TaskPriority,
        status: TaskStatus,
        estimatedMinutes: Int
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                id = "task_${System.currentTimeMillis()}",
                title = title,
                description = description,
                repositoryId = repoId,
                repositoryName = repoName,
                priority = priority,
                status = status,
                estimatedMinutes = estimatedMinutes,
                createdAt = System.currentTimeMillis()
            )
            repository.saveTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun toggleSubtask(subtask: SubTaskEntity) {
        viewModelScope.launch {
            repository.toggleSubtask(subtask)
        }
    }

    // --- AI Code Assistant State ---
    private val _aiInputCode = MutableStateFlow("fun authenticateUser(token: String): User? {\n    val decoded = jwtDecoder.decode(token)\n    return if (decoded.isExpired) null else userRepository.findById(decoded.userId)\n}")
    val aiInputCode: StateFlow<String> = _aiInputCode.asStateFlow()

    private val _aiCodeLanguage = MutableStateFlow("Kotlin")
    val aiCodeLanguage: StateFlow<String> = _aiCodeLanguage.asStateFlow()

    private val _aiAssistantResult = MutableStateFlow<String?>(null)
    val aiAssistantResult: StateFlow<String?> = _aiAssistantResult.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun updateAiInputCode(code: String) {
        _aiInputCode.value = code
    }

    fun updateAiLanguage(lang: String) {
        _aiCodeLanguage.value = lang
    }

    fun executeAiCodeAction(actionType: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = when (actionType) {
                "EXPLAIN" -> "Explain this ${_aiCodeLanguage.value} code clearly with execution steps and time/space complexity:\n```${_aiCodeLanguage.value}\n${_aiInputCode.value}\n```"
                "REFACTOR" -> "Refactor this ${_aiCodeLanguage.value} code for optimal clean architecture, separation of concerns, and readability:\n```${_aiCodeLanguage.value}\n${_aiInputCode.value}\n```"
                "OPTIMIZE" -> "Optimize this ${_aiCodeLanguage.value} code for maximum throughput, low memory allocations, and concurrency:\n```${_aiCodeLanguage.value}\n${_aiInputCode.value}\n```"
                "TESTS" -> "Generate comprehensive unit tests with edge cases and mocks for this ${_aiCodeLanguage.value} code:\n```${_aiCodeLanguage.value}\n${_aiInputCode.value}\n```"
                "CONVERT" -> "Convert this ${_aiCodeLanguage.value} code to modern idiomatic TypeScript / Python with full type annotations:\n```${_aiCodeLanguage.value}\n${_aiInputCode.value}\n```"
                else -> "Review and improve this code:\n${_aiInputCode.value}"
            }
            _aiAssistantResult.value = repository.askAssistant(prompt)
            _isAiLoading.value = false
        }
    }

    // --- AI Debugger State ---
    private val _debugErrorMessage = MutableStateFlow("NullPointerException: Parameter 'userToken' cannot be null")
    val debugErrorMessage: StateFlow<String> = _debugErrorMessage.asStateFlow()

    private val _debugStackTrace = MutableStateFlow("at com.example.auth.AuthHandler.validate(AuthHandler.kt:42)\nat com.example.api.Gateway.handleRequest(Gateway.kt:18)")
    val debugStackTrace: StateFlow<String> = _debugStackTrace.asStateFlow()

    private val _debugResult = MutableStateFlow<DebugAnalysisResult?>(null)
    val debugResult: StateFlow<DebugAnalysisResult?> = _debugResult.asStateFlow()

    private val _isDebugging = MutableStateFlow(false)
    val isDebugging: StateFlow<Boolean> = _isDebugging.asStateFlow()

    fun updateDebugError(error: String) { _debugErrorMessage.value = error }
    fun updateDebugStackTrace(trace: String) { _debugStackTrace.value = trace }

    fun runAiDebugger() {
        viewModelScope.launch {
            _isDebugging.value = true
            val result = repository.analyzeErrorWithAi(
                error = _debugErrorMessage.value,
                stackTrace = _debugStackTrace.value,
                code = _aiInputCode.value
            )
            _debugResult.value = result
            _isDebugging.value = false
        }
    }

    // --- AI Code Review State ---
    private val _reviewResult = MutableStateFlow<CodeReviewResult?>(null)
    val reviewResult: StateFlow<CodeReviewResult?> = _reviewResult.asStateFlow()

    private val _isReviewing = MutableStateFlow(false)
    val isReviewing: StateFlow<Boolean> = _isReviewing.asStateFlow()

    fun runCodeReview() {
        viewModelScope.launch {
            _isReviewing.value = true
            val result = repository.reviewCodeWithAi(_aiInputCode.value, _aiCodeLanguage.value)
            _reviewResult.value = result
            _isReviewing.value = false
        }
    }

    // --- AI Task Decomposition State ---
    private val _decompositionGoal = MutableStateFlow("Implement GitHub Webhook Event Sync with HMAC verification")
    val decompositionGoal: StateFlow<String> = _decompositionGoal.asStateFlow()

    private val _decomposedSubtasks = MutableStateFlow<List<DecomposedSubtask>>(emptyList())
    val decomposedSubtasks: StateFlow<List<DecomposedSubtask>> = _decomposedSubtasks.asStateFlow()

    private val _isDecomposing = MutableStateFlow(false)
    val isDecomposing: StateFlow<Boolean> = _isDecomposing.asStateFlow()

    fun updateDecompositionGoal(goal: String) { _decompositionGoal.value = goal }

    fun decomposeTaskGoal() {
        viewModelScope.launch {
            _isDecomposing.value = true
            val list = repository.decomposeTaskWithAi(_decompositionGoal.value)
            _decomposedSubtasks.value = list
            _isDecomposing.value = false
        }
    }

    fun convertDecomposedToTasks(repoId: String?, repoName: String?) {
        viewModelScope.launch {
            _decomposedSubtasks.value.forEach { sub ->
                val task = TaskEntity(
                    id = "task_${System.currentTimeMillis()}_${(0..999).random()}",
                    title = sub.title,
                    description = "Decomposed from goal: ${_decompositionGoal.value}",
                    repositoryId = repoId,
                    repositoryName = repoName ?: "devpilot-core",
                    priority = sub.priority,
                    status = TaskStatus.TODO,
                    estimatedMinutes = sub.estimatedMinutes,
                    labelsCsv = "ai-decomposed"
                )
                repository.saveTask(task)
            }
            _decomposedSubtasks.value = emptyList()
        }
    }

    // --- Documentation Generator State ---
    private val _selectedDocType = MutableStateFlow("README")
    val selectedDocType: StateFlow<String> = _selectedDocType.asStateFlow()

    private val _generatedDocContent = MutableStateFlow<String?>(null)
    val generatedDocContent: StateFlow<String?> = _generatedDocContent.asStateFlow()

    private val _isDocLoading = MutableStateFlow(false)
    val isDocLoading: StateFlow<Boolean> = _isDocLoading.asStateFlow()

    fun updateDocType(type: String) { _selectedDocType.value = type }

    fun generateDocumentation(repoName: String, repoDesc: String) {
        viewModelScope.launch {
            _isDocLoading.value = true
            val doc = repository.generateDocWithAi(repoName, _selectedDocType.value, repoDesc)
            _generatedDocContent.value = doc
            _isDocLoading.value = false
        }
    }

    // --- Daily Planner State ---
    private val _plannerAvailableHours = MutableStateFlow(6.0f)
    val plannerAvailableHours: StateFlow<Float> = _plannerAvailableHours.asStateFlow()

    private val _isPlanningLoading = MutableStateFlow(false)
    val isPlanningLoading: StateFlow<Boolean> = _isPlanningLoading.asStateFlow()

    fun setPlannerHours(hours: Float) { _plannerAvailableHours.value = hours }

    fun generateOptimizedDailyPlan() {
        viewModelScope.launch {
            _isPlanningLoading.value = true
            repository.generateDailyPlanWithAi(_plannerAvailableHours.value, tasks.value)
            _isPlanningLoading.value = false
        }
    }

    // --- Global Search (Ctrl+K Command Palette) ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    fun openSearch() { _isSearchOpen.value = true }
    fun closeSearch() { _isSearchOpen.value = false; _searchQuery.value = "" }
    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    // --- GitHub Sync / Settings ---
    private val _githubUsername = MutableStateFlow("alex-developer")
    val githubUsername: StateFlow<String> = _githubUsername.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun updateGithubUsername(name: String) { _githubUsername.value = name }

    fun syncGitHubAccount(token: String? = null) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncGitHub(_githubUsername.value, token)
            _isSyncing.value = false
        }
    }
}
