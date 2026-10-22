package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun FloatingSpeedDialWheel(
    onRunClick: () -> Unit,
    onDebugClick: () -> Unit,
    onBuildSignClick: () -> Unit,
    onTerminalClick: () -> Unit,
    onProfilerClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotateDegree by animateFloatAsState(targetValue = if (isExpanded) 135f else 0f, label = "fab_rotate")

    Column(
        modifier = modifier.testTag("floating_speed_dial"),
        horizontalAlignment = Alignment.End
    ) {
        // Expanded Speed Dial Options
        AnimatedVisibility(visible = isExpanded) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                SpeedDialItem("Run Project", Icons.Default.PlayArrow, IdeRunGreen) {
                    isExpanded = false
                    onRunClick()
                }
                Spacer(modifier = Modifier.height(8.dp))
                SpeedDialItem("Debug Step", Icons.Default.BugReport, IdeDebugRed) {
                    isExpanded = false
                    onDebugClick()
                }
                Spacer(modifier = Modifier.height(8.dp))
                SpeedDialItem("Build APK / AAB", Icons.Default.Archive, IdePrimary) {
                    isExpanded = false
                    onBuildSignClick()
                }
                Spacer(modifier = Modifier.height(8.dp))
                SpeedDialItem("Terminal Shell", Icons.Default.Terminal, TokenFunction) {
                    isExpanded = false
                    onTerminalClick()
                }
                Spacer(modifier = Modifier.height(8.dp))
                SpeedDialItem("ARM Profiler", Icons.Default.Memory, IdeWarnYellow) {
                    isExpanded = false
                    onProfilerClick()
                }
            }
        }

        // Main Speed Dial Trigger
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = IdePrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(56.dp).testTag("speed_dial_main_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Quick Actions Wheel",
                modifier = Modifier.rotate(rotateDegree)
            )
        }
    }
}

@Composable
fun SpeedDialItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = IdeSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = IdeSurface,
            contentColor = tint,
            shape = CircleShape
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint)
        }
    }
}
