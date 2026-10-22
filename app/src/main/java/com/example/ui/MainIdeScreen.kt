package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.interpreter.ProfilerEngine
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainIdeScreen(
    viewModel: IdeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val treeNodes by viewModel.treeNodes.collectAsStateWithLifecycle()
    val debuggerState by viewModel.debuggerState.collectAsStateWithLifecycle()
    val logcatLogs by viewModel.logcatLogs.collectAsStateWithLifecycle()
    val isFocusMode by viewModel.isFocusMode.collectAsStateWithLifecycle()

    val showNewProjectDialog by viewModel.showNewProjectDialog.collectAsStateWithLifecycle()
    val showNewFileDialog by viewModel.showNewFileDialog.collectAsStateWithLifecycle()
    val showBuildSignDialog by viewModel.showBuildSignDialog.collectAsStateWithLifecycle()
    val showKeystoreDialog by viewModel.showKeystoreDialog.collectAsStateWithLifecycle()
    val buildResult by viewModel.buildResult.collectAsStateWithLifecycle()
    val activeKeystore by viewModel.activeKeystore.collectAsStateWithLifecycle()
    val showSecurityDialog by viewModel.showSecurityDialog.collectAsStateWithLifecycle()

    // Multi-project dialogs
    val projectToRename by viewModel.projectToRename.collectAsStateWithLifecycle()
    val projectToClone by viewModel.projectToClone.collectAsStateWithLifecycle()
    val projectToDelete by viewModel.projectToDelete.collectAsStateWithLifecycle()

    var consoleOverrideTab by remember { mutableStateOf<BottomTab?>(null) }
    var isProjectDropdownOpen by remember { mutableStateOf(false) }
    val profilerMetrics = remember { ProfilerEngine.getLiveMetrics() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val activeFile = project.files[project.activeFileId] ?: project.files.values.firstOrNull()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = IdeSurface
            ) {
                DrawerContent(
                    project = project,
                    projects = projects,
                    onProjectSelect = { projectId ->
                        viewModel.switchProject(projectId)
                        scope.launch { drawerState.close() }
                    },
                    onRenameProjectClick = { p -> viewModel.openRenameDialog(p) },
                    onCloneProjectClick = { p -> viewModel.openCloneDialog(p) },
                    onDeleteProjectClick = { p -> viewModel.openDeleteDialog(p) },
                    treeNodes = treeNodes,
                    onFileSelect = { fileId ->
                        viewModel.selectFile(fileId)
                        scope.launch { drawerState.close() }
                    },
                    onFolderToggle = { folderPath -> viewModel.toggleFolder(folderPath) },
                    onDeleteNode = { node -> viewModel.deleteNode(node) },
                    onRenameNode = { node -> viewModel.renameNode(node, node.name) },
                    onAddFileInFolder = { folderPath -> viewModel.openNewFileDialog(folderPath) },
                    onToggleBreakpoint = { fileId -> viewModel.toggleBreakpoint(fileId) },
                    onNewFileClick = {
                        viewModel.openNewFileDialog()
                        scope.launch { drawerState.close() }
                    },
                    onNewProjectClick = {
                        viewModel.openNewProjectDialog()
                        scope.launch { drawerState.close() }
                    },
                    onBuildSignClick = {
                        viewModel.openBuildSignDialog()
                        scope.launch { drawerState.close() }
                    },
                    onSearchClick = {
                        viewModel.openSecurityDialog()
                        scope.launch { drawerState.close() }
                    },
                    onSecurityReportClick = {
                        viewModel.openSecurityDialog()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (!isFocusMode) {
                    TopAppBar(
                        title = {
                            Box {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { isProjectDropdownOpen = true }
                                        .padding(vertical = 4.dp, horizontal = 2.dp)
                                        .testTag("project_selector_header")
                                ) {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Project",
                                        tint = IdePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Git Branch Badge
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = IdeSurfaceVariant
                                    ) {
                                        Text(
                                            text = "git: main",
                                            fontSize = 9.sp,
                                            color = TokenKeyword,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Project Selector Dropdown Menu
                                DropdownMenu(
                                    expanded = isProjectDropdownOpen,
                                    onDismissRequest = { isProjectDropdownOpen = false },
                                    modifier = Modifier
                                        .background(IdeSurface)
                                        .widthIn(min = 240.dp)
                                ) {
                                    Text(
                                        text = "SWITCH PROJECT WORKSPACE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IdeComment,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )

                                    HorizontalDivider(color = IdeBorder)

                                    projects.forEach { p ->
                                        val isActive = p.id == project.id
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(
                                                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = null,
                                                        tint = if (isActive) IdeRunGreen else IdeComment,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = p.name,
                                                            color = if (isActive) Color.White else IdeComment,
                                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 13.sp
                                                        )
                                                        Text(
                                                            text = p.templateType.displayName,
                                                            color = TokenKeyword,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.switchProject(p.id)
                                                isProjectDropdownOpen = false
                                            }
                                        )
                                    }

                                    HorizontalDivider(color = IdeBorder)

                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = IdeRunGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("+ Create New Project", color = IdeRunGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        },
                                        onClick = {
                                            isProjectDropdownOpen = false
                                            viewModel.openNewProjectDialog()
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = null,
                                                    tint = IdePrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Clone Current Project", color = IdePrimary, fontSize = 12.sp)
                                            }
                                        },
                                        onClick = {
                                            isProjectDropdownOpen = false
                                            viewModel.openCloneDialog(project)
                                        }
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("open_drawer_btn")
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Drawer", tint = Color.White)
                            }
                        },
                        actions = {
                            // ARM SoC Metric Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = IdePrimary.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = "${profilerMetrics.ramUsageMb.toInt()}MB | 60 FPS",
                                    fontSize = 10.sp,
                                    color = IdeRunGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Run Action
                            IconButton(
                                onClick = { viewModel.runProject() },
                                modifier = Modifier.testTag("run_app_btn")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Run App", tint = IdeRunGreen)
                            }
                            // Build & Sign Action
                            IconButton(
                                onClick = { viewModel.openBuildSignDialog() },
                                modifier = Modifier.testTag("top_build_sign_btn")
                            ) {
                                Icon(Icons.Default.Archive, contentDescription = "Build & Sign APK/AAB", tint = IdePrimary)
                            }
                            // Focus Mode Toggle Action
                            IconButton(
                                onClick = { viewModel.toggleFocusMode() },
                                modifier = Modifier.testTag("toggle_focus_mode_btn")
                            ) {
                                Icon(Icons.Default.Fullscreen, contentDescription = "Focus Mode", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = IdeSurface
                        )
                    )
                }
            },
            bottomBar = {
                if (!isFocusMode) {
                    Column {
                        // Quick Symbol Strip above bottom sheets
                        QuickSymbolBar(
                            onSymbolClick = { symbol -> viewModel.insertSymbolAtActiveFile(symbol) },
                            onFormatClick = { viewModel.formatActiveCode() }
                        )

                        // Resizable Bottom Sheet (Logcat, Shell, Profiler & Debugger)
                        BottomConsoleSheet(
                            project = project,
                            debuggerState = debuggerState,
                            logcatLogs = logcatLogs,
                            onClearLogs = { viewModel.clearLogs() },
                            onResumeDebugger = { viewModel.resumeDebugger() },
                            onStepOver = { viewModel.stepOverDebugger() },
                            onStopDebugger = { viewModel.stopDebugger() },
                            activeTabOverride = consoleOverrideTab
                        )
                    }
                }
            },
            floatingActionButton = {
                if (!isFocusMode) {
                    FloatingSpeedDialWheel(
                        onRunClick = { viewModel.runProject() },
                        onDebugClick = { viewModel.runProject() },
                        onBuildSignClick = { viewModel.openBuildSignDialog() },
                        onTerminalClick = { consoleOverrideTab = BottomTab.TERMINAL },
                        onProfilerClick = { consoleOverrideTab = BottomTab.PROFILER },
                        onSearchClick = { viewModel.openSecurityDialog() }
                    )
                }
            },
            containerColor = IdeBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // File Tabs Bar
                    if (!isFocusMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IdeSurfaceVariant)
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            project.files.values.forEach { file ->
                                val isActive = file.id == project.activeFileId
                                Surface(
                                    onClick = { viewModel.selectFile(file.id) },
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                                    color = if (isActive) IdeBackground else Color.Transparent,
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = file.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isActive) IdePrimary else IdeComment,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                        if (file.isModified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("•", color = IdeWarnYellow, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Active Code Editor
                    if (activeFile != null) {
                        CodeEditorView(
                            file = activeFile,
                            activeLine = if (debuggerState.currentFileId == activeFile.id) debuggerState.currentLine else null,
                            onContentChange = { newContent ->
                                viewModel.updateFileContent(activeFile.id, newContent)
                            },
                            onBreakpointToggle = { line ->
                                viewModel.toggleBreakpoint(activeFile.id, line)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No File Open. Select a file from the drawer.", color = IdeComment)
                        }
                    }
                }

                // Focus Mode Floating Exit Button
                if (isFocusMode) {
                    FloatingActionButton(
                        onClick = { viewModel.toggleFocusMode() },
                        containerColor = IdePrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .testTag("exit_focus_mode_btn")
                    ) {
                        Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Focus Mode", tint = Color.White)
                    }
                }
            }
        }
    }

    // Dialogs
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { viewModel.closeNewProjectDialog() },
            onCreate = { name, pkg, template -> viewModel.createProject(name, pkg, template) }
        )
    }

    projectToRename?.let { p ->
        RenameProjectDialog(
            project = p,
            onDismiss = { viewModel.closeRenameDialog() },
            onRename = { newName -> viewModel.renameProject(p.id, newName) }
        )
    }

    projectToClone?.let { p ->
        CloneProjectDialog(
            project = p,
            onDismiss = { viewModel.closeCloneDialog() },
            onClone = { clonedName -> viewModel.cloneProject(p.id, clonedName) }
        )
    }

    projectToDelete?.let { p ->
        DeleteProjectConfirmDialog(
            project = p,
            onDismiss = { viewModel.closeDeleteDialog() },
            onConfirmDelete = { viewModel.deleteProject(p.id) }
        )
    }

    if (showNewFileDialog) {
        NewFileDialog(
            onDismiss = { viewModel.closeNewFileDialog() },
            onCreate = { fileName, extension -> viewModel.createFile(fileName, extension) }
        )
    }

    if (showBuildSignDialog) {
        BuildSignDialog(
            onDismiss = { viewModel.closeBuildSignDialog() },
            onBuildTrigger = { target, keystore -> viewModel.triggerBuildAndSign(target, keystore) },
            onOpenKeystoreManager = { viewModel.openKeystoreDialog() },
            activeKeystore = activeKeystore
        )
    }

    if (showKeystoreDialog) {
        KeystoreManagerDialog(
            onDismiss = { viewModel.closeKeystoreDialog() },
            onCreateKeystore = { alias, storePass, keyPass, validity, name, org ->
                viewModel.createKeystore(alias, storePass, keyPass, validity, name, org)
            }
        )
    }

    buildResult?.let { result ->
        BuildResultDialog(
            result = result,
            onDismiss = { viewModel.closeBuildResultDialog() }
        )
    }

    if (showSecurityDialog) {
        SecurityReportDialog(
            onDismiss = { viewModel.closeSecurityDialog() }
        )
    }
}
