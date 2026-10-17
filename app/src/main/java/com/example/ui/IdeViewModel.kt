package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interpreter.AstEngine
import com.example.model.*
import com.example.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IdeViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val astEngine = AstEngine()

    private val _project = MutableStateFlow<Project>(
        repository.createProjectFromTemplate("AndroidStudioMobileDemo", "com.aistudio.mobile.ide", TemplateType.FULL_STACK_COMPOSE)
    )
    val project: StateFlow<Project> = _project.asStateFlow()

    private val _treeNodes = MutableStateFlow<List<FileTreeNode>>(emptyList())
    val treeNodes: StateFlow<List<FileTreeNode>> = _treeNodes.asStateFlow()

    private val _debuggerState = MutableStateFlow(DebuggerState())
    val debuggerState: StateFlow<DebuggerState> = _debuggerState.asStateFlow()

    private val _logcatLogs = MutableStateFlow<List<LogcatEntry>>(emptyList())
    val logcatLogs: StateFlow<List<LogcatEntry>> = _logcatLogs.asStateFlow()

    private val _isFocusMode = MutableStateFlow(false)
    val isFocusMode: StateFlow<Boolean> = _isFocusMode.asStateFlow()

    private val _showNewProjectDialog = MutableStateFlow(false)
    val showNewProjectDialog: StateFlow<Boolean> = _showNewProjectDialog.asStateFlow()

    private val _showNewFileDialog = MutableStateFlow(false)
    val showNewFileDialog: StateFlow<Boolean> = _showNewFileDialog.asStateFlow()

    private val _targetFolderPath = MutableStateFlow<String?>(null)

    private val _showSecurityDialog = MutableStateFlow(false)
    val showSecurityDialog: StateFlow<Boolean> = _showSecurityDialog.asStateFlow()

    init {
        updateTreeNodes()
    }

    private fun updateTreeNodes() {
        _treeNodes.value = repository.buildTreeNodes(_project.value)
    }

    fun selectFile(fileId: String) {
        _project.value = _project.value.copy(activeFileId = fileId)
    }

    fun toggleFolder(folderPath: String) {
        val currentExpanded = _project.value.expandedFolderPaths.toMutableSet()
        if (currentExpanded.contains(folderPath)) {
            currentExpanded.remove(folderPath)
        } else {
            currentExpanded.add(folderPath)
        }
        _project.value = _project.value.copy(expandedFolderPaths = currentExpanded)
        updateTreeNodes()
    }

    fun updateFileContent(fileId: String, newContent: String) {
        val updatedFiles = _project.value.files.toMutableMap()
        val existing = updatedFiles[fileId] ?: return
        updatedFiles[fileId] = existing.copy(content = newContent, isModified = true)
        _project.value = _project.value.copy(files = updatedFiles)
        updateTreeNodes()
    }

    fun toggleBreakpoint(fileId: String, line: Int = 1) {
        val updatedFiles = _project.value.files.toMutableMap()
        val file = updatedFiles[fileId] ?: return
        val currentBreakpoints = file.breakpoints.toMutableSet()
        if (currentBreakpoints.contains(line)) {
            currentBreakpoints.remove(line)
        } else {
            currentBreakpoints.add(line)
        }
        updatedFiles[fileId] = file.copy(breakpoints = currentBreakpoints)
        _project.value = _project.value.copy(files = updatedFiles)
        updateTreeNodes()
    }

    fun deleteNode(node: FileTreeNode) {
        val updatedFiles = _project.value.files.toMutableMap()
        if (node.fileId != null) {
            updatedFiles.remove(node.fileId)
        } else {
            // Delete folder path
            val keysToRemove = updatedFiles.filter { it.value.relativePath.startsWith(node.relativePath) }.keys
            keysToRemove.forEach { updatedFiles.remove(it) }
        }
        _project.value = _project.value.copy(files = updatedFiles)
        updateTreeNodes()
    }

    fun renameNode(node: FileTreeNode, newName: String) {
        if (node.fileId == null) return
        val updatedFiles = _project.value.files.toMutableMap()
        val existing = updatedFiles[node.fileId] ?: return
        val parts = existing.relativePath.split('/')
        val newPath = parts.dropLast(1).joinToString("/") + "/" + newName
        updatedFiles[node.fileId] = existing.copy(name = newName, relativePath = newPath)
        _project.value = _project.value.copy(files = updatedFiles)
        updateTreeNodes()
    }

    fun runProject() {
        viewModelScope.launch {
            astEngine.executeProject(
                project = _project.value,
                onLog = { entry ->
                    _logcatLogs.value = _logcatLogs.value + entry
                },
                onDebuggerUpdate = { state ->
                    _debuggerState.value = state
                }
            )
        }
    }

    fun resumeDebugger() {
        _debuggerState.value = _debuggerState.value.copy(isPaused = false, currentLine = null)
    }

    fun stepOverDebugger() {
        val curLine = _debuggerState.value.currentLine ?: 1
        _debuggerState.value = _debuggerState.value.copy(currentLine = curLine + 1)
    }

    fun stopDebugger() {
        _debuggerState.value = DebuggerState(isRunning = false, isPaused = false)
    }

    fun clearLogs() {
        _logcatLogs.value = emptyList()
    }

    fun toggleFocusMode() {
        _isFocusMode.value = !_isFocusMode.value
    }

    fun openNewProjectDialog() { _showNewProjectDialog.value = true }
    fun closeNewProjectDialog() { _showNewProjectDialog.value = false }

    fun openNewFileDialog(targetFolder: String? = null) {
        _targetFolderPath.value = targetFolder
        _showNewFileDialog.value = true
    }
    fun closeNewFileDialog() {
        _targetFolderPath.value = null
        _showNewFileDialog.value = false
    }

    fun openSecurityDialog() { _showSecurityDialog.value = true }
    fun closeSecurityDialog() { _showSecurityDialog.value = false }

    fun createProject(name: String, packageName: String, template: TemplateType) {
        _project.value = repository.createProjectFromTemplate(name, packageName, template)
        _logcatLogs.value = emptyList()
        _debuggerState.value = DebuggerState()
        updateTreeNodes()
        closeNewProjectDialog()
    }

    fun createFile(fileName: String, extension: String) {
        val id = java.util.UUID.randomUUID().toString()
        val targetFolder = _targetFolderPath.value ?: "src"
        val relativePath = "$targetFolder/$fileName"

        val newFile = ProjectFile(
            id = id,
            name = fileName,
            relativePath = relativePath,
            extension = extension,
            content = when (extension.lowercase()) {
                "kt" -> "package ${_project.value.packageName}\n\nfun main() {\n    println(\"Hello from $fileName!\")\n}"
                "java" -> "package ${_project.value.packageName};\n\npublic class ${fileName.substringBefore(".")} {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from Java!\");\n    }\n}"
                "xml" -> "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n</resources>"
                else -> "// $fileName"
            }
        )
        val updatedMap = _project.value.files.toMutableMap()
        updatedMap[id] = newFile
        _project.value = _project.value.copy(files = updatedMap, activeFileId = id)
        updateTreeNodes()
        closeNewFileDialog()
    }

    fun formatActiveCode() {
        val activeFile = _project.value.files[_project.value.activeFileId] ?: return
        val formatted = activeFile.content.lines().joinToString("\n") { it.trimEnd() }
        updateFileContent(activeFile.id, formatted)
    }

    fun insertSymbolAtActiveFile(symbol: String) {
        val activeFile = _project.value.files[_project.value.activeFileId] ?: return
        val newContent = activeFile.content + " " + symbol
        updateFileContent(activeFile.id, newContent)
    }
}
