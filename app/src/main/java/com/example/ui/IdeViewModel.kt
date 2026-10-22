package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interpreter.AstEngine
import com.example.interpreter.KeystoreManager
import com.example.model.*
import com.example.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class IdeViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val astEngine = AstEngine()

    private val initialProject = repository.createProjectFromTemplate(
        "StudioMobileDemo",
        "com.aistudio.mobile.ide",
        TemplateType.FULL_STACK_COMPOSE
    )
    private val secondaryProject = repository.createProjectFromTemplate(
        "GameEngineDemo",
        "com.aistudio.gameengine",
        TemplateType.GAME_CANVAS
    )

    private val _projects = MutableStateFlow<List<Project>>(listOf(initialProject, secondaryProject))
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _activeProjectId = MutableStateFlow<String>(initialProject.id)
    val activeProjectId: StateFlow<String> = _activeProjectId.asStateFlow()

    private val _project = MutableStateFlow<Project>(initialProject)
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

    private val _showBuildSignDialog = MutableStateFlow(false)
    val showBuildSignDialog: StateFlow<Boolean> = _showBuildSignDialog.asStateFlow()

    private val _showKeystoreDialog = MutableStateFlow(false)
    val showKeystoreDialog: StateFlow<Boolean> = _showKeystoreDialog.asStateFlow()

    private val _buildResult = MutableStateFlow<BuildResult?>(null)
    val buildResult: StateFlow<BuildResult?> = _buildResult.asStateFlow()

    private val _activeKeystore = MutableStateFlow<KeystoreConfig?>(KeystoreManager.getActiveKeystore())
    val activeKeystore: StateFlow<KeystoreConfig?> = _activeKeystore.asStateFlow()

    private val _targetFolderPath = MutableStateFlow<String?>(null)

    private val _showSecurityDialog = MutableStateFlow(false)
    val showSecurityDialog: StateFlow<Boolean> = _showSecurityDialog.asStateFlow()

    // Multi-project dialog states
    private val _projectToRename = MutableStateFlow<Project?>(null)
    val projectToRename: StateFlow<Project?> = _projectToRename.asStateFlow()

    private val _projectToClone = MutableStateFlow<Project?>(null)
    val projectToClone: StateFlow<Project?> = _projectToClone.asStateFlow()

    private val _projectToDelete = MutableStateFlow<Project?>(null)
    val projectToDelete: StateFlow<Project?> = _projectToDelete.asStateFlow()

    init {
        updateTreeNodes()
    }

    private fun updateTreeNodes() {
        _treeNodes.value = repository.buildTreeNodes(_project.value)
    }

    fun switchProject(projectId: String) {
        val target = _projects.value.find { it.id == projectId } ?: return
        _activeProjectId.value = projectId
        _project.value = target
        _logcatLogs.value = emptyList()
        _debuggerState.value = DebuggerState()
        updateTreeNodes()
    }

    fun selectFile(fileId: String) {
        val updated = _project.value.copy(activeFileId = fileId)
        _project.value = updated
        updateProjectInList(updated)
    }

    fun toggleFolder(folderPath: String) {
        val currentExpanded = _project.value.expandedFolderPaths.toMutableSet()
        if (currentExpanded.contains(folderPath)) {
            currentExpanded.remove(folderPath)
        } else {
            currentExpanded.add(folderPath)
        }
        val updated = _project.value.copy(expandedFolderPaths = currentExpanded)
        _project.value = updated
        updateProjectInList(updated)
        updateTreeNodes()
    }

    fun updateFileContent(fileId: String, newContent: String) {
        val updatedFiles = _project.value.files.toMutableMap()
        val existing = updatedFiles[fileId] ?: return
        updatedFiles[fileId] = existing.copy(content = newContent, isModified = true)
        val updated = _project.value.copy(files = updatedFiles)
        _project.value = updated
        updateProjectInList(updated)
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
        val updated = _project.value.copy(files = updatedFiles)
        _project.value = updated
        updateProjectInList(updated)
        updateTreeNodes()
    }

    fun deleteNode(node: FileTreeNode) {
        val updatedFiles = _project.value.files.toMutableMap()
        if (node.fileId != null) {
            updatedFiles.remove(node.fileId)
        } else {
            val keysToRemove = updatedFiles.filter { it.value.relativePath.startsWith(node.relativePath) }.keys
            keysToRemove.forEach { updatedFiles.remove(it) }
        }
        val updated = _project.value.copy(files = updatedFiles)
        _project.value = updated
        updateProjectInList(updated)
        updateTreeNodes()
    }

    fun renameNode(node: FileTreeNode, newName: String) {
        if (node.fileId == null) return
        val updatedFiles = _project.value.files.toMutableMap()
        val existing = updatedFiles[node.fileId] ?: return
        val parts = existing.relativePath.split('/')
        val newPath = parts.dropLast(1).joinToString("/") + "/" + newName
        updatedFiles[node.fileId] = existing.copy(name = newName, relativePath = newPath)
        val updated = _project.value.copy(files = updatedFiles)
        _project.value = updated
        updateProjectInList(updated)
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

    fun triggerBuildAndSign(target: BuildTarget, keystore: KeystoreConfig?) {
        closeBuildSignDialog()
        viewModelScope.launch {
            val result = KeystoreManager.compileAndSignBinary(_project.value, target, keystore)
            _buildResult.value = result
        }
    }

    fun createKeystore(alias: String, storePass: String, keyPass: String, validityYears: Int, name: String, org: String) {
        val ks = KeystoreManager.createKeystore(alias, storePass, keyPass, validityYears, name, org)
        _activeKeystore.value = ks
        closeKeystoreDialog()
    }

    fun closeBuildResultDialog() {
        _buildResult.value = null
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

    fun openBuildSignDialog() { _showBuildSignDialog.value = true }
    fun closeBuildSignDialog() { _showBuildSignDialog.value = false }

    fun openKeystoreDialog() { _showKeystoreDialog.value = true }
    fun closeKeystoreDialog() { _showKeystoreDialog.value = false }

    fun openSecurityDialog() { _showSecurityDialog.value = true }
    fun closeSecurityDialog() { _showSecurityDialog.value = false }

    // Project management dialog triggers
    fun openRenameDialog(project: Project) { _projectToRename.value = project }
    fun closeRenameDialog() { _projectToRename.value = null }

    fun openCloneDialog(project: Project) { _projectToClone.value = project }
    fun closeCloneDialog() { _projectToClone.value = null }

    fun openDeleteDialog(project: Project) { _projectToDelete.value = project }
    fun closeDeleteDialog() { _projectToDelete.value = null }

    fun createProject(name: String, packageName: String, template: TemplateType) {
        val newProj = repository.createProjectFromTemplate(name, packageName, template)
        _projects.value = listOf(newProj) + _projects.value
        _activeProjectId.value = newProj.id
        _project.value = newProj
        _logcatLogs.value = emptyList()
        _debuggerState.value = DebuggerState()
        updateTreeNodes()
        closeNewProjectDialog()
    }

    fun cloneProject(projectId: String, newName: String) {
        val source = _projects.value.find { it.id == projectId } ?: return
        val newId = UUID.randomUUID().toString()
        val clonedFiles = source.files.mapValues { (_, file) ->
            file.copy(id = UUID.randomUUID().toString(), isModified = false)
        }
        val clonedProj = source.copy(
            id = newId,
            name = newName,
            files = clonedFiles,
            activeFileId = clonedFiles.keys.firstOrNull()
        )
        _projects.value = listOf(clonedProj) + _projects.value
        _activeProjectId.value = newId
        _project.value = clonedProj
        _logcatLogs.value = emptyList()
        _debuggerState.value = DebuggerState()
        updateTreeNodes()
        closeCloneDialog()
    }

    fun renameProject(projectId: String, newName: String) {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val updated = currentList[index].copy(name = newName)
            currentList[index] = updated
            _projects.value = currentList
            if (_activeProjectId.value == projectId) {
                _project.value = updated
            }
        }
        closeRenameDialog()
    }

    fun deleteProject(projectId: String) {
        if (_projects.value.size <= 1) {
            closeDeleteDialog()
            return // Keep at least one project
        }
        val currentList = _projects.value.toMutableList()
        currentList.removeAll { it.id == projectId }
        _projects.value = currentList

        if (_activeProjectId.value == projectId) {
            val nextProject = currentList.first()
            _activeProjectId.value = nextProject.id
            _project.value = nextProject
            _logcatLogs.value = emptyList()
            _debuggerState.value = DebuggerState()
            updateTreeNodes()
        }
        closeDeleteDialog()
    }

    private fun updateProjectInList(updated: Project) {
        val list = _projects.value.toMutableList()
        val idx = list.indexOfFirst { it.id == updated.id }
        if (idx != -1) {
            list[idx] = updated
            _projects.value = list
        }
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
        val updated = _project.value.copy(files = updatedMap, activeFileId = id)
        _project.value = updated
        updateProjectInList(updated)
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
