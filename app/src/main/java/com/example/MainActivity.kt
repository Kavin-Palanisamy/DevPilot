package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.model.AuthState
import com.example.data.model.NextBestAction
import com.example.data.model.WorkflowStage
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.DevPilotTheme
import com.example.viewmodel.DevPilotViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DevPilotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            DevPilotTheme(darkTheme = isDarkMode) {
                DevPilotApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DevPilotApp(viewModel: DevPilotViewModel) {
    val authState by viewModel.authState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentWorkflowStage by viewModel.currentWorkflowStage.collectAsState()

    val user by viewModel.user.collectAsState()
    val repositories by viewModel.repositories.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()
    val allRisks by viewModel.allRisks.collectAsState()

    val selectedRepoId by viewModel.selectedRepoId.collectAsState()
    val selectedRepoAnalysis by viewModel.getSelectedRepoAnalysis().collectAsState(initial = null)
    val selectedRepo by viewModel.getSelectedRepo().collectAsState(initial = null)

    // AI and stage outputs
    val aiAssistantResult by viewModel.aiAssistantResult.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    val decomposedSubtasks by viewModel.decomposedSubtasks.collectAsState()
    val isPlanningLoading by viewModel.isPlanningLoading.collectAsState()

    val debugResult by viewModel.debugResult.collectAsState()
    val isDebugging by viewModel.isDebugging.collectAsState()

    val reviewResult by viewModel.reviewResult.collectAsState()
    val isReviewing by viewModel.isReviewing.collectAsState()

    // Search dialog state
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()

    // GitHub integration state
    val githubUsername by viewModel.githubUsername.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val nextBestAction = remember(tasks, allRisks, repositories) {
        viewModel.getNextBestAction()
    }

    when (authState) {
        AuthState.LANDING -> {
            LandingScreen(
                onStartFree = { viewModel.setAuthState(AuthState.SIGN_UP) },
                onSignIn = { viewModel.setAuthState(AuthState.LOGIN) },
                onContinueWithDemo = { viewModel.setAuthState(AuthState.AUTHENTICATED) },
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                isDarkMode = isDarkMode
            )
        }

        AuthState.LOGIN -> {
            LoginScreen(
                onLoginSuccess = { viewModel.setAuthState(AuthState.AUTHENTICATED) },
                onLoginWithGitHub = { viewModel.setAuthState(AuthState.AUTHENTICATED) },
                onNavigateToSignUp = { viewModel.setAuthState(AuthState.SIGN_UP) },
                onBackToLanding = { viewModel.setAuthState(AuthState.LANDING) }
            )
        }

        AuthState.SIGN_UP -> {
            SignUpScreen(
                onSignUpSuccess = { viewModel.setAuthState(AuthState.ONBOARDING) },
                onLoginWithGitHub = { viewModel.setAuthState(AuthState.ONBOARDING) },
                onNavigateToLogin = { viewModel.setAuthState(AuthState.LOGIN) },
                onBackToLanding = { viewModel.setAuthState(AuthState.LANDING) }
            )
        }

        AuthState.ONBOARDING -> {
            OnboardingScreen(
                repositories = repositories,
                onCompleteOnboarding = { repoId ->
                    viewModel.completeOnboarding(repoId)
                },
                onSkipOnboarding = {
                    viewModel.setAuthState(AuthState.AUTHENTICATED)
                }
            )
        }

        AuthState.AUTHENTICATED -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    DevPilotTopBar(
                        user = user,
                        currentScreen = currentScreen,
                        currentProjectName = if (currentScreen == "workspace") selectedRepo?.name else null,
                        isDarkMode = isDarkMode,
                        onOpenSearch = { viewModel.openSearch() },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                },
                bottomBar = {
                    DevPilotBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                user = user,
                                repositories = repositories,
                                nextBestAction = nextBestAction,
                                recentActivities = recentActivities,
                                tasks = tasks,
                                onSelectProject = { repoId ->
                                    viewModel.selectRepo(repoId)
                                    viewModel.navigateTo("workspace")
                                },
                                onNavigateToStage = { repoId, stage ->
                                    viewModel.navigateToStage(repoId, stage)
                                },
                                onNavigateToProjects = {
                                    viewModel.navigateTo("projects")
                                },
                                onCreateTaskFromAction = { action ->
                                    viewModel.createTaskFromAction(action)
                                    viewModel.navigateToStage(action.targetRepoId, WorkflowStage.BUILD)
                                }
                            )
                        }

                        "projects" -> {
                            ProjectsScreen(
                                repositories = repositories,
                                onSelectProject = { repoId ->
                                    viewModel.selectRepo(repoId)
                                    viewModel.navigateTo("workspace")
                                },
                                onNavigateToStage = { repoId, stage ->
                                    viewModel.navigateToStage(repoId, stage)
                                },
                                onAddProject = { name, desc, lang ->
                                    viewModel.addProject(name, desc, lang)
                                }
                            )
                        }

                        "workspace" -> {
                            ProjectWorkspaceScreen(
                                repository = selectedRepo ?: repositories.firstOrNull(),
                                analysis = selectedRepoAnalysis,
                                tasks = tasks,
                                activeStage = currentWorkflowStage,
                                onStageSelect = { viewModel.setWorkflowStage(it) },
                                onBackToProjects = { viewModel.navigateTo("projects") },
                                // AI Workspace delegates
                                onAskAiQuestion = { viewModel.askProjectQuestion(it) },
                                aiAnswerResult = aiAssistantResult,
                                isAiLoading = isAiLoading,
                                // Plan delegates
                                onGeneratePlan = {
                                    viewModel.updateDecompositionGoal(it)
                                    viewModel.decomposeTaskGoal()
                                },
                                decomposedSteps = decomposedSubtasks,
                                isPlanningLoading = isPlanningLoading,
                                onConvertPlanToTasks = {
                                    val repoId = selectedRepo?.id ?: "repo_1"
                                    val repoName = selectedRepo?.name ?: "devpilot-core"
                                    viewModel.convertDecomposedToTasks(repoId, repoName)
                                    viewModel.setWorkflowStage(WorkflowStage.BUILD)
                                },
                                // Build delegates
                                onUpdateTaskStatus = { task, status ->
                                    viewModel.updateTaskStatus(task, status)
                                },
                                onSaveTask = { title, desc, prio, status, est ->
                                    val repoId = selectedRepo?.id ?: "repo_1"
                                    val repoName = selectedRepo?.name ?: "devpilot-core"
                                    viewModel.saveTask(title, desc, repoId, repoName, prio, status, est)
                                },
                                // Debug delegates
                                onRunDebugger = { err, trace ->
                                    viewModel.updateDebugError(err)
                                    viewModel.updateDebugStackTrace(trace)
                                    viewModel.runAiDebugger()
                                },
                                debugResult = debugResult,
                                isDebugging = isDebugging,
                                // Review delegates
                                onRunReview = { viewModel.runCodeReview() },
                                reviewResult = reviewResult,
                                isReviewing = isReviewing,
                                // Improve delegates
                                risks = allRisks,
                                onConvertRiskToTask = { viewModel.convertRiskToTask(it) },
                                onResolveRisk = { viewModel.resolveRisk(it) }
                            )
                        }

                        "settings" -> {
                            SettingsAndHelpScreen(
                                user = user,
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.setDarkMode(it) },
                                githubUsername = githubUsername,
                                onUpdateGithubUsername = { viewModel.updateGithubUsername(it) },
                                onSyncGitHub = { viewModel.syncGitHubAccount(githubUsername) },
                                isSyncing = isSyncing,
                                onSignOut = { viewModel.setAuthState(AuthState.LANDING) }
                            )
                        }

                        else -> {
                            HomeScreen(
                                user = user,
                                repositories = repositories,
                                nextBestAction = nextBestAction,
                                recentActivities = recentActivities,
                                tasks = tasks,
                                onSelectProject = { repoId ->
                                    viewModel.selectRepo(repoId)
                                    viewModel.navigateTo("workspace")
                                },
                                onNavigateToStage = { repoId, stage ->
                                    viewModel.navigateToStage(repoId, stage)
                                },
                                onNavigateToProjects = {
                                    viewModel.navigateTo("projects")
                                },
                                onCreateTaskFromAction = { action ->
                                    viewModel.createTaskFromAction(action)
                                    viewModel.navigateToStage(action.targetRepoId, WorkflowStage.BUILD)
                                }
                            )
                        }
                    }
                }
            }

            // Global Search Modal (⌘K Command Palette)
            if (isSearchOpen) {
                GlobalSearchDialog(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    tasks = tasks,
                    repositories = repositories,
                    onDismiss = { viewModel.closeSearch() },
                    onNavigate = { viewModel.navigateTo(it) },
                    onSelectRepo = { repoId ->
                        viewModel.selectRepo(repoId)
                        viewModel.navigateTo("workspace")
                    },
                    onNavigateToStage = { stage ->
                        viewModel.setWorkflowStage(stage)
                        viewModel.navigateTo("workspace")
                    }
                )
            }
        }
    }
}
