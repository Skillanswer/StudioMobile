package com.example.repository

import com.example.model.FileTreeNode
import com.example.model.Project
import com.example.model.ProjectFile
import com.example.model.TemplateType
import java.util.UUID

class ProjectRepository {

    fun createProjectFromTemplate(name: String, packageName: String, templateType: TemplateType): Project {
        val projectId = UUID.randomUUID().toString()
        val filesMap = mutableMapOf<String, ProjectFile>()

        when (templateType) {
            TemplateType.COMPOSE -> createComposeTemplateFiles(projectId, packageName, filesMap)
            TemplateType.FULL_STACK_COMPOSE -> createFullStackComposeFiles(projectId, packageName, filesMap)
            TemplateType.MVVM_ARCHITECTURE -> createMvvmArchitectureFiles(projectId, packageName, filesMap)
            TemplateType.GAME_CANVAS -> createGameCanvasFiles(projectId, packageName, filesMap)
            TemplateType.MULTI_MODULE -> createMultiModuleFiles(projectId, packageName, filesMap)
            TemplateType.VIEWS -> createViewsTemplateFiles(projectId, packageName, filesMap)
            TemplateType.KOTLIN_PURE -> createKotlinPureFiles(projectId, packageName, filesMap)
            TemplateType.SECURITY_SANDBOX -> createSecuritySandboxFiles(projectId, packageName, filesMap)
        }

        val mainFile = filesMap.values.firstOrNull {
            it.name == "MainActivity.kt" || it.name == "Main.kt" || it.name == "MainActivity.java" || it.name == "GameActivity.kt"
        }

        // Collect all directory paths to pre-expand them
        val allExpandedDirs = mutableSetOf<String>()
        filesMap.values.forEach { file ->
            val parts = file.relativePath.split('/')
            var currentPath = ""
            for (i in 0 until parts.size - 1) {
                currentPath = if (currentPath.isEmpty()) parts[i] else "$currentPath/${parts[i]}"
                allExpandedDirs.add(currentPath)
            }
        }

        return Project(
            id = projectId,
            name = name,
            packageName = packageName,
            templateType = templateType,
            files = filesMap,
            activeFileId = mainFile?.id ?: filesMap.keys.firstOrNull(),
            expandedFolderPaths = allExpandedDirs
        )
    }

    fun buildTreeNodes(project: Project): List<FileTreeNode> {
        val allDirectories = mutableSetOf<String>()
        val filesByParent = mutableMapOf<String, MutableList<ProjectFile>>()

        // Extract all directory paths and group files by parent directory
        project.files.values.forEach { file ->
            val parts = file.relativePath.split('/')
            val parentPath = if (parts.size > 1) parts.dropLast(1).joinToString("/") else ""
            filesByParent.getOrPut(parentPath) { mutableListOf() }.add(file)

            // Register intermediate directories
            var pathAcc = ""
            for (i in 0 until parts.size - 1) {
                pathAcc = if (pathAcc.isEmpty()) parts[i] else "$pathAcc/${parts[i]}"
                allDirectories.add(pathAcc)
            }
        }

        // Helper to recursively build tree nodes for a directory
        fun buildDirectory(parentDir: String, depth: Int): List<FileTreeNode> {
            val result = mutableListOf<FileTreeNode>()

            // Find child directories whose parent is parentDir
            val childDirs = allDirectories.filter { dir ->
                if (parentDir.isEmpty()) {
                    !dir.contains('/')
                } else {
                    dir.startsWith("$parentDir/") && !dir.substring(parentDir.length + 1).contains('/')
                }
            }.sorted()

            childDirs.forEach { dirPath ->
                val dirName = dirPath.split('/').last()
                val isExpanded = project.expandedFolderPaths.contains(dirPath)
                val children = if (isExpanded) buildDirectory(dirPath, depth + 1) else emptyList()

                result.add(
                    FileTreeNode(
                        id = "dir_$dirPath",
                        name = dirName,
                        relativePath = dirPath,
                        isDirectory = true,
                        isExpanded = isExpanded,
                        depth = depth,
                        children = children
                    )
                )
            }

            // Find child files whose parent is parentDir
            val childFiles = filesByParent[parentDir] ?: emptyList()
            childFiles.sortedBy { it.name }.forEach { file ->
                result.add(
                    FileTreeNode(
                        id = file.id,
                        name = file.name,
                        relativePath = file.relativePath,
                        isDirectory = false,
                        depth = depth,
                        fileId = file.id,
                        extension = file.extension,
                        breakpoints = file.breakpoints
                    )
                )
            }

            return result
        }

        return buildDirectory("", 0)
    }

    private fun createComposeTemplateFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "MainActivity.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/MainActivity.kt",
            extension = "kt",
            content = """
                package $pkg

                import androidx.activity.ComponentActivity
                import androidx.compose.foundation.layout.*
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.unit.dp

                class MainActivity : ComponentActivity() {
                    override fun onCreate() {
                        println("MainActivity initialized on StudioMobile")
                    }
                }

                @Composable
                fun MainComposeScreen() {
                    var counter by remember { mutableStateOf(0) }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Hello from Jetpack Compose!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Counter value: ${'$'}counter",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { counter++ }) {
                            Text("Increment Counter")
                        }
                    }
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }

    private fun createFullStackComposeFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "MainActivity.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/MainActivity.kt",
            extension = "kt",
            content = """
                package $pkg

                import androidx.activity.ComponentActivity
                import androidx.compose.foundation.layout.*
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.unit.dp

                class MainActivity : ComponentActivity() {
                    override fun onCreate() {
                        println("Full Stack Compose Activity Running")
                    }
                }

                @Composable
                fun FullStackMainScreen(viewModel: UserViewModel = UserViewModel()) {
                    val users by viewModel.users.collectAsState()

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Full Stack User Directory", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.addUser("Developer " + (users.size + 1)) }) {
                            Text("Fetch & Add User")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        users.forEach { user ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(text = "User #${'$'}{user.id}: ${'$'}{user.name} (${'$'}{user.role})", modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                }
            """.trimIndent()
        )

        val f2 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "UserViewModel.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/UserViewModel.kt",
            extension = "kt",
            content = """
                package $pkg

                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow
                import kotlinx.coroutines.flow.asStateFlow

                data class UserEntity(val id: Int, val name: String, val role: String)

                class UserViewModel {
                    private val _users = MutableStateFlow<List<UserEntity>>(
                        listOf(
                            UserEntity(1, "Alice Chen", "Mobile Architect"),
                            UserEntity(2, "Bob Smith", "Backend Engineer")
                        )
                    )
                    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

                    fun addUser(name: String) {
                        val currentList = _users.value.toMutableList()
                        val newId = currentList.size + 1
                        currentList.add(UserEntity(newId, name, "Full Stack Contributor"))
                        _users.value = currentList
                    }
                }
            """.trimIndent()
        )

        val f3 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "build.gradle.kts",
            relativePath = "app/build.gradle.kts",
            extension = "kts",
            content = """
                plugins {
                    alias(libs.plugins.android.application)
                    alias(libs.plugins.kotlin.compose)
                }

                android {
                    namespace = "$pkg"
                    compileSdk = 36
                }
            """.trimIndent()
        )

        files[f1.id] = f1
        files[f2.id] = f2
        files[f3.id] = f3
    }

    private fun createMvvmArchitectureFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "MainActivity.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/MainActivity.kt",
            extension = "kt",
            content = """
                package $pkg

                class MainActivity {
                    fun onCreate() {
                        val repo = TaskRepository()
                        val viewModel = TaskViewModel(repo)
                        viewModel.loadTasks()
                        println("MVVM Architecture Initialized with ${'$'}{viewModel.getTaskCount()} tasks")
                    }
                }
            """.trimIndent()
        )

        val f2 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "TaskRepository.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/TaskRepository.kt",
            extension = "kt",
            content = """
                package $pkg

                data class Task(val id: String, val title: String, val isCompleted: Boolean)

                class TaskRepository {
                    private val tasks = mutableListOf(
                        Task("1", "Design Mobile IDE UI", true),
                        Task("2", "Implement AST Interpreter", true),
                        Task("3", "Integrate Logcat Stream", true)
                    )

                    fun getAllTasks(): List<Task> = tasks.toList()
                    fun addTask(task: Task) { tasks.add(task) }
                }

                class TaskViewModel(private val repository: TaskRepository) {
                    private var cachedTasks = emptyList<Task>()

                    fun loadTasks() {
                        cachedTasks = repository.getAllTasks()
                    }

                    fun getTaskCount(): Int = cachedTasks.size
                }
            """.trimIndent()
        )

        files[f1.id] = f1
        files[f2.id] = f2
    }

    private fun createGameCanvasFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "GameActivity.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/GameActivity.kt",
            extension = "kt",
            content = """
                package $pkg

                class GameActivity {
                    private val engine = 2DGameLoopEngine()

                    fun onCreate() {
                        println("2D Canvas Game Engine starting...")
                        engine.startGameLoop()
                    }
                }

                class 2DGameLoopEngine {
                    private var isRunning = false
                    private var score = 0
                    private var playerX = 100f
                    private var playerY = 200f

                    fun startGameLoop() {
                        isRunning = true
                        update(16.6f) // 60 FPS update simulation
                        render()
                        println("Game Engine Status: Active. Player Position: (${'$'}playerX, ${'$'}playerY), Score: ${'$'}score")
                    }

                    private fun update(deltaTimeMs: Float) {
                        playerX += 5.0f * (deltaTimeMs / 16.6f)
                        score += 10
                    }

                    private fun render() {
                        println("Rendering frame at (${'$'}playerX, ${'$'}playerY)")
                    }
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }

    private fun createMultiModuleFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "AppModule.kt",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/AppModule.kt",
            extension = "kt",
            content = """
                package $pkg

                class AppModule {
                    fun initialize() {
                        println("Initializing Multi-Module Architecture App")
                        val core = CoreModule()
                        val data = DataModule()
                        println("Modules Loaded: ${'$'}{core.getModuleName()} & ${'$'}{data.getModuleName()}")
                    }
                }

                class CoreModule {
                    fun getModuleName(): String = ":core-domain"
                }

                class DataModule {
                    fun getModuleName(): String = ":data-network"
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }

    private fun createViewsTemplateFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "MainActivity.java",
            relativePath = "app/src/main/java/${pkg.replace('.', '/')}/MainActivity.java",
            extension = "java",
            content = """
                package $pkg;

                public class MainActivity {

                    private int count = 0;

                    public void onCreate() {
                        System.out.println("Android Views Activity created cleanly.");
                    }

                    public void onButtonClick() {
                        count++;
                        System.out.println("Button Clicked. Current Count: " + count);
                    }
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }

    private fun createKotlinPureFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "Main.kt",
            relativePath = "src/Main.kt",
            extension = "kt",
            content = """
                package $pkg

                fun main() {
                    println("=== Pure Kotlin & Java Runner ===")
                    val numbers = listOf(1, 2, 3, 4, 5)
                    val doubled = numbers.map { it * 2 }
                    println("Doubled values: ${'$'}doubled")
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }

    private fun createSecuritySandboxFiles(projectId: String, pkg: String, files: MutableMap<String, ProjectFile>) {
        val f1 = ProjectFile(
            id = UUID.randomUUID().toString(),
            name = "SecuritySandboxApp.kt",
            relativePath = "src/SecuritySandboxApp.kt",
            extension = "kt",
            content = """
                package $pkg

                fun main() {
                    println("=== Security Sandbox Active ===")
                    val isEncrypted = true
                    println("Data Isolation State: Encrypted = ${'$'}isEncrypted")
                }
            """.trimIndent()
        )
        files[f1.id] = f1
    }
}
