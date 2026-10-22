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
import com.example.interpreter.ProfilerEngine
import com.example.interpreter.TerminalEngine
import com.example.model.DebuggerState
import com.example.model.LogcatEntry
import com.example.model.LogLevel
import com.example.model.Project
import com.example.ui.theme.*

enum class BottomTab { LOGCAT, TERMINAL, PROFILER, DEBUGGER, APP_PREVIEW }

@Composable
fun BottomConsoleSheet(
    project: Project,
    debuggerState: DebuggerState,
    logcatLogs: List<LogcatEntry>,
    onClearLogs: () -> Unit,
    onResumeDebugger: () -> Unit,
    onStepOver: () -> Unit,
    onStopDebugger: () -> Unit,
    activeTabOverride: BottomTab? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BottomTab.LOGCAT) }

    LaunchedEffect(activeTabOverride) {
        if (activeTabOverride != null) {
            selectedTab = activeTabOverride
        }
    }

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
                Row(modifier = Modifier.weight(1f)) {
                    TabPill(
                        title = "LOGCAT (${logcatLogs.size})",
                        isSelected = selectedTab == BottomTab.LOGCAT,
                        onClick = { selectedTab = BottomTab.LOGCAT }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TabPill(
                        title = "SHELL",
                        isSelected = selectedTab == BottomTab.TERMINAL,
                        onClick = { selectedTab = BottomTab.TERMINAL }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TabPill(
                        title = "PROFILER",
                        isSelected = selectedTab == BottomTab.PROFILER,
                        onClick = { selectedTab = BottomTab.PROFILER }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TabPill(
                        title = if (debuggerState.isPaused) "DEBUG (PAUSED)" else "DEBUG",
                        isSelected = selectedTab == BottomTab.DEBUGGER,
                        isHighlight = debuggerState.isPaused,
                        onClick = { selectedTab = BottomTab.DEBUGGER }
                    )
                }

                // Debug Controls if running
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (debuggerState.isRunning) {
                        IconButton(onClick = onResumeDebugger, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = IdeRunGreen)
                        }
                        IconButton(onClick = onStepOver, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Redo, contentDescription = "Step Over", tint = IdePrimary)
                        }
                        IconButton(onClick = onStopDebugger, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = IdeDebugRed)
                        }
                    }
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    BottomTab.LOGCAT -> LogcatView(
                        logs = logcatLogs,
                        selectedLevel = null,
                        searchQuery = "",
                        onLevelSelect = {},
                        onQueryChange = {},
                        onClear = onClearLogs
                    )
                    BottomTab.TERMINAL -> TerminalView(project = project)
                    BottomTab.PROFILER -> ProfilerView()
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
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("System Logcat Stream", fontSize = 11.sp, color = IdeComment, fontWeight = FontWeight.Bold)
            IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ClearAll, contentDescription = "Clear", tint = IdeComment)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(IdeBackground, shape = RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            items(logs) { log ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(log.timestamp, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = IdeComment)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${log.level.label}/${log.tag}:", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(log.level.colorHex))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(log.message, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TokenIdentifier, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TerminalView(project: Project) {
    var commandInput by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf("StudioMobile Terminal Shell [ARM64]\nType 'gradle assembleDebug' or 'git status'")) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(IdeBackground, shape = RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            items(history) { line ->
                Text(line, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TokenIdentifier)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$ ", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = IdeRunGreen)
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier.weight(1f).height(44.dp),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White),
                placeholder = { Text("Command (e.g. gradle assembleDebug)", fontSize = 10.sp, color = IdeComment) },
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        val output = TerminalEngine.executeCommand(commandInput, project)
                        history = history + "$ $commandInput" + output
                        commandInput = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Run Command", tint = IdePrimary)
            }
        }
    }
}

@Composable
fun ProfilerView() {
    val metrics = remember { ProfilerEngine.getLiveMetrics() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IdeBackground, shape = RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Text("ARM SOC PERFORMANCE MONITOR", fontSize = 10.sp, color = IdeWarnYellow, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricCard("RAM HEAP", "${metrics.ramUsageMb} MB / ${metrics.maxRamMb} MB", IdePrimary)
            MetricCard("ARM CPU LOAD", "${metrics.cpuPercent}% (Octa-Core)", IdeRunGreen)
            MetricCard("FRAME STABILITY", "${metrics.fps} FPS", TokenKeyword)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = IdeSurfaceVariant,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontSize = 9.sp, color = IdeComment)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
        }
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
                Text("Debugger idle. Tap Run or Debug.", color = IdeComment, fontSize = 11.sp)
            }
            return
        }

        Text("VARIABLES WATCH INSPECTOR", style = MaterialTheme.typography.labelSmall, color = IdeComment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.variables) { variable ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${variable.name}: ${variable.type}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TokenFunction)
                    Text(variable.value, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TokenString)
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
        Text("📱 SIMULATED RUNTIME PREVIEW", style = MaterialTheme.typography.labelSmall, color = IdeRunGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            color = IdeSurfaceVariant,
            border = BorderStroke(1.dp, IdePrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(project.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("Package: ${project.packageName}", style = MaterialTheme.typography.bodySmall, color = IdeComment, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { previewCounter++ }, colors = ButtonDefaults.buttonColors(containerColor = IdePrimary)) {
                    Text("Interactive Tap ($previewCounter)")
                }
            }
        }
    }
}
