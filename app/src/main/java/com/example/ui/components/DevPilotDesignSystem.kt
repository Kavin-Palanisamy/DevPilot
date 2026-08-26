package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ============================================================================
// 1. BUTTON VARIANTS & DIMENSIONS
// Standard SaaS Buttons (Height: 36px, Small: 32px, Large: 42px, Radius: 6-8px)
// ============================================================================

enum class DevPilotButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    GHOST,
    DESTRUCTIVE
}

enum class DevPilotButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun DevPilotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DevPilotButtonVariant = DevPilotButtonVariant.PRIMARY,
    size: DevPilotButtonSize = DevPilotButtonSize.MEDIUM,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String? = null
) {
    val height = when (size) {
        DevPilotButtonSize.SMALL -> 32.dp
        DevPilotButtonSize.MEDIUM -> 36.dp
        DevPilotButtonSize.LARGE -> 42.dp
    }

    val horizontalPadding = when (size) {
        DevPilotButtonSize.SMALL -> 10.dp
        DevPilotButtonSize.MEDIUM -> 14.dp
        DevPilotButtonSize.LARGE -> 18.dp
    }

    val fontSize = when (size) {
        DevPilotButtonSize.SMALL -> 12.sp
        DevPilotButtonSize.MEDIUM -> 13.sp
        DevPilotButtonSize.LARGE -> 14.sp
    }

    val iconSize = when (size) {
        DevPilotButtonSize.SMALL -> 14.dp
        DevPilotButtonSize.MEDIUM -> 16.dp
        DevPilotButtonSize.LARGE -> 18.dp
    }

    val shape = RoundedCornerShape(6.dp)

    val (containerColor, contentColor, borderStroke) = when (variant) {
        DevPilotButtonVariant.PRIMARY -> Triple(
            DevPilotCyan,
            Color(0xFF090D16),
            null
        )
        DevPilotButtonVariant.SECONDARY -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurface,
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        )
        DevPilotButtonVariant.OUTLINE -> Triple(
            Color.Transparent,
            MaterialTheme.colorScheme.onSurface,
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        )
        DevPilotButtonVariant.GHOST -> Triple(
            Color.Transparent,
            MaterialTheme.colorScheme.onSurfaceVariant,
            null
        )
        DevPilotButtonVariant.DESTRUCTIVE -> Triple(
            DevPilotDanger,
            Color.White,
            null
        )
    }

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = if (enabled) contentColor else contentColor.copy(alpha = 0.5f),
        border = borderStroke,
        modifier = modifier
            .height(height)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontSize = fontSize,
                fontWeight = if (variant == DevPilotButtonVariant.PRIMARY) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
            )

            if (trailingIcon != null && !isLoading) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun DevPilotIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    iconSize: Dp = 16.dp,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    hasBorder: Boolean = true,
    testTag: String? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = containerColor,
        border = if (hasBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        modifier = modifier
            .size(size)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

// ============================================================================
// 2. CARDS & ELEVATED SURFACES
// Subtle 1px borders, restrained 8-12dp radius, high contrast
// ============================================================================

@Composable
fun DevPilotCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// ============================================================================
// 3. STATUS & PRIORITY BADGES
// Subtle dots with muted backgrounds and crisp text
// ============================================================================

@Composable
fun DevPilotStatusDotBadge(
    label: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    bgColor: Color = dotColor.copy(alpha = 0.12f),
    textColor: Color = dotColor
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================================================
// 4. STATISTIC & METRIC CARDS (Context + Metric + Trend)
// ============================================================================

@Composable
fun DevPilotMetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    trendText: String? = null,
    isTrendPositive: Boolean = true
) {
    DevPilotCard(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (trendText != null) {
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isTrendPositive) DevPilotSuccess else DevPilotDanger,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// 5. PAGE HEADERS & BREADCRUMBS
// ============================================================================

@Composable
fun DevPilotPageHeader(
    title: String,
    subtitle: String? = null,
    breadcrumb: String? = null,
    modifier: Modifier = Modifier,
    actionSlot: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (breadcrumb != null) {
            Text(
                text = breadcrumb,
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (actionSlot != null) {
                Spacer(modifier = Modifier.width(12.dp))
                actionSlot()
            }
        }
    }
}

@Composable
fun DevPilotSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    actionSlot: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        if (actionSlot != null) {
            actionSlot()
        }
    }
}

// ============================================================================
// 6. CODE & FILE REFERENCES
// JetBrains Mono interactive path badge
// ============================================================================

@Composable
fun DevPilotFileBadge(
    path: String,
    line: Int? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.InsertDriveFile,
                contentDescription = null,
                tint = DevPilotCyan,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (line != null) "$path:$line" else path,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ============================================================================
// 7. SEGMENTED TAB GROUP (Linear / Raycast Style)
// ============================================================================

@Composable
fun <T> DevPilotSegmentedTabGroup(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier,
    iconProvider: ((T) -> ImageVector)? = null
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                Surface(
                    onClick = { onItemSelected(item) },
                    shape = RoundedCornerShape(4.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                    border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (iconProvider != null) {
                            Icon(
                                imageVector = iconProvider(item),
                                contentDescription = null,
                                tint = if (isSelected) DevPilotCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(
                            text = labelProvider(item),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
