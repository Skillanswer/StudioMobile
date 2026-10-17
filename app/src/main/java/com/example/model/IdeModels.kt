package com.example.model

enum class FileType {
    KOTLIN, JAVA, GRADLE, XML, JSON, TEXT
}

enum class TokenType {
    KEYWORD, TYPE, STRING, FUNCTION, NUMBER, COMMENT, ANNOTATION, OPERATOR, IDENTIFIER, DEFAULT
}

data class SyntaxToken(
    val text: String,
    val type: TokenType,
    val startOffset: Int,
    val endOffset: Int,
    val line: Int
)

enum class TemplateType(val displayName: String, val description: String) {
    COMPOSE("Jetpack Compose App", "Modern Kotlin UI with Compose & Material 3"),
    FULL_STACK_COMPOSE("Full Stack Compose", "Navigation, Room DB, Ktor Networking & ViewModel"),
    MVVM_ARCHITECTURE("Modern MVVM Architecture", "Repository pattern, StateFlow streams & Room Cache"),
    GAME_CANVAS("2D Canvas Game Engine", "Custom Game Loop, sprite rendering & touch physics"),
    MULTI_MODULE("Multi-Module Architecture", "Modular layout with App, Core, Data & UI packages"),
    VIEWS("Android Views App", "Classic Java/XML layout app with Activity & Views"),
    KOTLIN_PURE("Pure Kotlin/Java App", "On-device algorithm & console app runner"),
    SECURITY_SANDBOX("Security Sandbox App", "Encrypted data isolation & prompt-injection defense demo")
}

data class ProjectFile(
    val id: String,
    val name: String,
    val relativePath: String,
    val extension: String,
    val content: String,
    val isDirectory: Boolean = false,
    val parentPath: String? = null,
    val childrenIds: List<String> = emptyList(),
    val breakpoints: Set<Int> = emptySet(),
    val isModified: Boolean = false
) {
    val fileType: FileType
        get() = when (extension.lowercase()) {
            "kt", "kts" -> FileType.KOTLIN
            "java" -> FileType.JAVA
            "xml" -> FileType.XML
            "json" -> FileType.JSON
            "gradle" -> FileType.GRADLE
            else -> FileType.TEXT
        }
}

data class FileTreeNode(
    val id: String,
    val name: String,
    val relativePath: String,
    val isDirectory: Boolean,
    val isExpanded: Boolean = true,
    val depth: Int = 0,
    val fileId: String? = null,
    val extension: String = "",
    val breakpoints: Set<Int> = emptySet(),
    val children: List<FileTreeNode> = emptyList()
)

data class Project(
    val id: String,
    val name: String,
    val packageName: String,
    val templateType: TemplateType,
    val rootPath: String = "/",
    val files: Map<String, ProjectFile> = emptyMap(),
    val activeFileId: String? = null,
    val expandedFolderPaths: Set<String> = setOf("app", "app/src", "app/src/main", "app/src/main/java", "src")
)

enum class LogLevel(val label: String, val colorHex: Long) {
    VERBOSE("V", 0xFF6C707E),
    DEBUG("D", 0xFF62A8F3),
    INFO("I", 0xFF34A853),
    WARN("W", 0xFFF4B400),
    ERROR("E", 0xFFEA4335)
}

data class LogcatEntry(
    val id: String,
    val timestamp: String,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val sourceFile: String? = null,
    val lineNumber: Int? = null
)

data class VariableItem(
    val name: String,
    val type: String,
    val value: String,
    val scope: String = "Local"
)

data class DebuggerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val currentFileId: String? = null,
    val currentLine: Int? = null,
    val variables: List<VariableItem> = emptyList(),
    val callStack: List<String> = emptyList(),
    val isBuildSuccess: Boolean = true,
    val executionOutput: String = ""
)
