package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DebuggerState
import com.example.model.LogcatEntry
import com.example.model.LogLevel
import com.example.model.Project
import com.example.ui.theme.*

enum class BottomTab { LOGCAT, DEBUGGER, APP_PREVIEW }

@Composable
fun BottomConsoleSheet(
    project: Project,
    debuggerState: DebuggerState,
    logcatLogs: List<LogcatEntry>,
    onClearLogs: () -> Unit,
    onResumeDebugger: () -> Unit,
    onStepOver: () -> Unit,
    onStopDebugger: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BottomTab.LOGCAT) }
    var selectedLogLevel by remember { mutableStateOf<LogLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_console_sheet"),
        color = IdeSurface,
        tonalElevation = 8.dp,
        border = BorderStroke(0.5.dp, IdeBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Tab Header Bar & Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IdeSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tabs
                Row {
                    TabPill(
                        title = "LOGCAT (${logcatLogs.size})",
                        isSelected = selectedTab == BottomTab.LOGCAT,
                        onClick = { selectedTab = BottomTab.LOGCAT }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TabPill(
                        title = if (debuggerState.isPaused) "DEBUGGER (PAUSED)" else "DEBUGGER",
                        isSelected = selectedTab == BottomTab.DEBUGGER,
                        isHighlight = debuggerState.isPaused,
                        onClick = { selectedTab = BottomTab.DEBUGGER }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TabPill(
                        title = "APP PREVIEW",
                        isSelected = selectedTab == BottomTab.APP_PREVIEW,
                        onClick = { selectedTab = BottomTab.APP_PREVIEW }
                    )
                }

                // Debug Controls if paused or running
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (debuggerState.isRunning) {
                        IconButton(onClick = onResumeDebugger, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = IdeRunGreen)
                        }
                        IconButton(onClick = onStepOver, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Redo, contentDescription = "Step Over", tint = IdePrimary)
                        }
                        IconButton(onClick = onStopDebugger, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = IdeDebugRed)
                        }
                    }
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    BottomTab.LOGCAT -> LogcatView(
                        logs = logcatLogs,
                        selectedLevel = selectedLogLevel,
                        searchQuery = searchQuery,
                        onLevelSelect = { selectedLogLevel = it },
                        onQueryChange = { searchQuery = it },
                        onClear = onClearLogs
                    )
                    BottomTab.DEBUGGER -> DebuggerView(
                        state = debuggerState,
                        onResume = onResumeDebugger,
                        onStep = onStepOver
                    )
                    BottomTab.APP_PREVIEW -> AppPreviewView(project = project)
                }
            }
        }
    }
}

@Composable
fun TabPill(
    title: String,
    isSelected: Boolean,
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = when {
            isHighlight -> IdeWarnYellow.copy(alpha = 0.3f)
            isSelected -> IdePrimary
            else -> Color.Transparent
        },
        modifier = Modifier.minimumInteractiveComponentSize()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else IdeComment,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun LogcatView(
    logs: List<LogcatEntry>,
    selectedLevel: LogLevel?,
    searchQuery: String,
    onLevelSelect: (LogLevel?) -> Unit,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val filteredLogs = remember(logs, selectedLevel, searchQuery) {
        logs.filter { entry ->
            (selectedLevel == null || entry.level == selectedLevel) &&
                    (searchQuery.isEmpty() || entry.message.contains(searchQuery, ignoreCase = true) || entry.tag.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Selector Pills
            Row(modifier = Modifier.weight(1f)) {
                LogLevelPill("ALL", selectedLevel == null) { onLevelSelect(null) }
                LogLevel.values().forEach { level ->
                    LogLevelPill(level.label, selectedLevel == level) { onLevelSelect(level) }
                }
            }

            IconButton(onClick = onClear, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ClearAll, contentDescription = "Clear", tint = IdeComment)
            }
        }

        // Log Entries List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(IdeBackground, shape = RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            items(filteredLogs) { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = log.timestamp,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = IdeComment
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${log.level.label}/${log.tag}:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(log.level.colorHex)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.message,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TokenIdentifier,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun LogLevelPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = if (isSelected) IdePrimary.copy(alpha = 0.3f) else Color.Transparent,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .minimumInteractiveComponentSize()
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) IdePrimary else IdeComment,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DebuggerView(
    state: DebuggerState,
    onResume: () -> Unit,
    onStep: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IdeBackground, shape = RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        if (!state.isRunning) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Debugger is idle. Tap Run or Debug to execute project.",
                    color = IdeComment,
                    fontSize = 12.sp
                )
            }
            return
        }

        Text(
            text = "VARIABLE WATCH INSPECTOR",
            style = MaterialTheme.typography.labelSmall,
            color = IdeComment,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.variables) { variable ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${variable.name}: ${variable.type}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TokenFunction
                    )
                    Text(
                        text = variable.value,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TokenString
                    )
                }
            }
        }
    }
}

@Composable
fun AppPreviewView(project: Project) {
    var previewCounter by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IdeBackground, shape = RoundedCornerShape(6.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📱 SIMULATED RUNTIME PREVIEW",
            style = MaterialTheme.typography.labelSmall,
            color = IdeRunGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            color = IdeSurfaceVariant,
            border = BorderStroke(1.dp, IdePrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Package: ${project.packageName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IdeComment,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { previewCounter++ },
                    colors = ButtonDefaults.buttonColors(containerColor = IdePrimary)
                ) {
                    Text("Interactive Tap ($previewCounter)")
                }
            }
        }
    }
}
