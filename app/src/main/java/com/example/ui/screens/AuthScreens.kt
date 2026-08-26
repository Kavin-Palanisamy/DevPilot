package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkflowStage
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Modern, high-conversion Developer Landing Page.
 * Hero: "Understand your code. Build better software."
 */
@Composable
fun LandingScreen(
    onStartFree: () -> Unit,
    onSignIn: () -> Unit,
    onContinueWithDemo: () -> Unit,
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // Top Nav Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DevPilotWordmark(iconSize = 30.dp, showTagline = true)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Toggle theme",
                            tint = DevPilotWarning,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DevPilotButton(
                        text = "Sign In",
                        variant = DevPilotButtonVariant.GHOST,
                        size = DevPilotButtonSize.SMALL,
                        onClick = onSignIn,
                        testTag = "landing_signin_button"
                    )

                    DevPilotButton(
                        text = "Start Free",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.SMALL,
                        onClick = onStartFree,
                        testTag = "landing_start_free_button"
                    )
                }
            }
        }

        // Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = DevPilotCyan.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, DevPilotCyan.copy(alpha = 0.35f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(DevPilotCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEV PILOT 2.0 • THE COMPLETE DEVELOPER WORKSPACE",
                            style = MaterialTheme.typography.labelSmall,
                            color = DevPilotCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = "Understand your code.\nBuild better software.",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 40.sp,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "DevPilot helps developers understand, plan, build, debug, review, and improve their software from one unified, intelligent workspace.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    modifier = Modifier.widthIn(max = 560.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Hero CTA Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DevPilotButton(
                        text = "Get Started Free →",
                        variant = DevPilotButtonVariant.PRIMARY,
                        size = DevPilotButtonSize.LARGE,
                        onClick = onStartFree,
                        testTag = "hero_cta_start_free"
                    )

                    DevPilotButton(
                        text = "Explore Demo",
                        variant = DevPilotButtonVariant.SECONDARY,
                        size = DevPilotButtonSize.LARGE,
                        onClick = onContinueWithDemo,
                        testTag = "hero_cta_demo"
                    )
                }
            }
        }

        // The 6-Stage Core Workflow Visualizer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "ONE UNIFIED WORKFLOW",
                    style = MaterialTheme.typography.labelSmall,
                    color = DevPilotCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Text(
                    text = "From understanding architecture to continuous improvement.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                val stages = listOf(
                    Triple(WorkflowStage.UNDERSTAND, Icons.Filled.AccountTree, "Map tech stack, API routes, data flow & repo dependencies in seconds."),
                    Triple(WorkflowStage.PLAN, Icons.Filled.AutoAwesome, "Decompose feature goals into prioritized implementation plans with estimates."),
                    Triple(WorkflowStage.BUILD, Icons.Filled.Code, "Focused task execution with contextual AI code generation and test suites."),
                    Triple(WorkflowStage.DEBUG, Icons.Filled.BugReport, "Instant root-cause analysis from stack traces with validated fix tasks."),
                    Triple(WorkflowStage.REVIEW, Icons.Filled.RateReview, "Automated pull request security gating, performance checks & diff audit."),
                    Triple(WorkflowStage.IMPROVE, Icons.Filled.TrendingUp, "Actionable, prioritized technical debt refactors and coverage boosts.")
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    stages.forEachIndexed { index, (stage, icon, desc) ->
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (index % 2 == 0) DevPilotCyan.copy(alpha = 0.14f)
                                            else DevPilotViolet.copy(alpha = 0.14f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = stage.label,
                                        tint = if (index % 2 == 0) DevPilotCyan else DevPilotViolet,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "0${index + 1}. ${stage.label.uppercase()}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "• ${stage.shortDesc}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                    }
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
        }
    }
}

/**
 * Professional, minimal Login Page.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginWithGitHub: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onBackToLanding: () -> Unit
) {
    var email by remember { mutableStateOf("alex.chen@devpilot.io") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Brand
            DevPilotLogoIcon(size = 46.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "DEV PILOT",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your AI software workspace",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Understand. Plan. Build. Debug. Review. Improve.",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Auth Card
            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                borderColor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Continue with GitHub Button
                Surface(
                    onClick = {
                        isLoading = true
                        onLoginWithGitHub()
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("github_login_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = "GitHub",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with GitHub",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = " or ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message if any
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = DevPilotDanger.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DevPilotDanger.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = DevPilotDanger,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Email Input
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    singleLine = true,
                    placeholder = { Text("developer@work.com", color = TextMutedDark, fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = TextMutedDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter valid credentials."
                        } else {
                            onLoginSuccess()
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DevPilotCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Sign In Button
                DevPilotButton(
                    text = "Sign In",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.LARGE,
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter both email and password."
                        } else {
                            onLoginSuccess()
                        }
                    },
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_submit_button"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Switch to Sign Up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DevPilotCyan,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .testTag("switch_to_signup")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "← Back to home",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                modifier = Modifier.clickable { onBackToLanding() }
            )
        }
    }
}

/**
 * Matching Sign-Up Page with validation.
 */
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onLoginWithGitHub: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackToLanding: () -> Unit
) {
    var name by remember { mutableStateOf("Alex Chen") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DevPilotLogoIcon(size = 46.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Create your DevPilot account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Join thousands of developers moving software forward.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            DevPilotCard(
                shape = RoundedCornerShape(10.dp),
                borderColor = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Continue with GitHub Button
                Surface(
                    onClick = {
                        isLoading = true
                        onLoginWithGitHub()
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("github_signup_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = "GitHub",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with GitHub",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = " or ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error message
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        color = DevPilotDanger.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, DevPilotDanger.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = DevPilotDanger,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Name
                Text("Full Name", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    singleLine = true,
                    placeholder = { Text("Alex Chen", color = TextMutedDark, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email
                Text("Work Email", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    singleLine = true,
                    placeholder = { Text("alex@company.com", color = TextMutedDark, fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password
                Text("Password", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("signup_password_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confirm Password
                Text("Confirm Password", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                DevPilotButton(
                    text = "Create Account",
                    variant = DevPilotButtonVariant.PRIMARY,
                    size = DevPilotButtonSize.LARGE,
                    onClick = {
                        when {
                            name.isBlank() -> errorMessage = "Please enter your name."
                            email.isBlank() || !email.contains("@") -> errorMessage = "Please enter a valid email."
                            password.length < 6 -> errorMessage = "Password must be at least 6 characters."
                            password != confirmPassword -> errorMessage = "Passwords do not match."
                            else -> onSignUpSuccess()
                        }
                    },
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "signup_submit_button"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign in",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DevPilotCyan,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .testTag("switch_to_login")
                )
            }
        }
    }
}
