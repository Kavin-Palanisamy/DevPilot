package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// DevPilot Unified Design System Tokens (Linear / Vercel / GitHub / Raycast)
// ============================================================================

// Brand Accent Colors
val DevPilotIndigo = Color(0xFF6366F1)
val DevPilotIndigoDark = Color(0xFF4F46E5)
val DevPilotIndigoLight = Color(0xFF818CF8)

val DevPilotCyan = Color(0xFF06B6D4)
val DevPilotCyanDark = Color(0xFF0891B2)
val DevPilotCyanLight = Color(0xFF22D3EE)

val DevPilotViolet = Color(0xFF8B5CF6)
val DevPilotVioletDark = Color(0xFF7C3AED)
val DevPilotVioletLight = Color(0xFFA78BFA)

// Semantic State Colors
val DevPilotSuccess = Color(0xFF10B981)
val DevPilotSuccessBg = Color(0xFF064E3B)
val DevPilotSuccessText = Color(0xFF34D399)

val DevPilotWarning = Color(0xFFF59E0B)
val DevPilotWarningBg = Color(0xFF451A03)
val DevPilotWarningText = Color(0xFFFBBF24)

val DevPilotDanger = Color(0xFFEF4444)
val DevPilotDangerBg = Color(0xFF450A0A)
val DevPilotDangerText = Color(0xFFF87171)

val DevPilotInfo = Color(0xFF38BDF8)
val DevPilotInfoBg = Color(0xFF0C4A6E)
val DevPilotInfoText = Color(0xFF7DD3FC)

// Dark Theme Surfaces (Obsidian / Deep Charcoal)
val NeutralBgDark = Color(0xFF090D16)          // Canvas Background
val NeutralSurfaceDark = Color(0xFF0F172A)     // TopBar, BottomBar, Panels
val NeutralCardDark = Color(0xFF141E33)        // Elevated Cards, Rows
val NeutralElevatedDark = Color(0xFF1E293B)    // Popovers, Modals, Hover States
val NeutralBorderDark = Color(0xFF27354A)      // Subtle 1px borders
val NeutralBorderSubtleDark = Color(0xFF1C2739)

val TextPrimaryDark = Color(0xFFF8FAFC)        // 900
val TextSecondaryDark = Color(0xFF94A3B8)      // 400
val TextMutedDark = Color(0xFF64748B)          // 500

// Light Theme Surfaces (Soft Off-White / Crisp Slate)
val NeutralBgLight = Color(0xFFF8FAFC)
val NeutralSurfaceLight = Color(0xFFFFFFFF)
val NeutralCardLight = Color(0xFFF1F5F9)
val NeutralElevatedLight = Color(0xFFE2E8F0)
val NeutralBorderLight = Color(0xFFCBD5E1)
val NeutralBorderSubtleLight = Color(0xFFE2E8F0)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF64748B)

// Code & Terminal Dark Surfaces
val CodeEditorBg = Color(0xFF0A0E17)
val CodeSyntaxKeyword = Color(0xFFFF7B72)
val CodeSyntaxString = Color(0xFFA5D6FF)
val CodeSyntaxFunction = Color(0xFFD2A8FF)
val CodeSyntaxComment = Color(0xFF8B949E)
val CodeSyntaxNumber = Color(0xFF79C0FF)

// Backward compatibility aliases
val CyanPrimary = DevPilotCyan
val CyanDark = DevPilotCyanDark
val CyanLight = DevPilotCyanLight

val VioletSecondary = DevPilotViolet
val VioletDark = DevPilotVioletDark
val VioletLight = DevPilotVioletLight

val EmeraldSuccess = DevPilotSuccess
val EmeraldDark = Color(0xFF059669)
val EmeraldLight = DevPilotSuccessText

val AmberWarning = DevPilotWarning
val AmberDark = Color(0xFFD97706)
val AmberLight = DevPilotWarningText

val RoseError = DevPilotDanger
val RoseDark = Color(0xFFDC2626)
val RoseLight = DevPilotDangerText

val BackgroundDark = NeutralBgDark
val SurfaceDark = NeutralSurfaceDark
val SurfaceCardDark = NeutralCardDark
val SurfaceElevatedDark = NeutralElevatedDark
val BorderDark = NeutralBorderDark

val BackgroundLight = NeutralBgLight
val SurfaceLight = NeutralSurfaceLight
val SurfaceCardLight = NeutralCardLight
val SurfaceElevatedLight = NeutralElevatedLight
val BorderLight = NeutralBorderLight

val CodeBg = CodeEditorBg
val CodeKeyword = CodeSyntaxKeyword
val CodeString = CodeSyntaxString
val CodeFunction = CodeSyntaxFunction
val CodeComment = CodeSyntaxComment
