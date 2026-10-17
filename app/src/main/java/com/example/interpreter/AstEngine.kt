package com.example.interpreter

import com.example.model.DebuggerState
import com.example.model.LogcatEntry
import com.example.model.LogLevel
import com.example.model.Project
import com.example.model.ProjectFile
import com.example.model.VariableItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AstEngine {

    fun executeProject(
        project: Project,
        breakpoints: Map<String, Set<Int>> = emptyMap(),
        onLog: (LogcatEntry) -> Unit,
        onDebuggerUpdate: (DebuggerState) -> Unit
    ) {
        val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())

        onLog(
            LogcatEntry(
                id = System.nanoTime().toString(),
                timestamp = timeStr,
                level = LogLevel.INFO,
                tag = "BuildSystem",
                message = "Task :app:compileDebug${project.templateType.name} AST AST-BUILD SUCCESSFUL in 142ms"
            )
        )

        // Find primary entry file (MainActivity.kt, Main.kt, or MainActivity.java)
        val entryFile = project.files.values.firstOrNull {
            it.name == "MainActivity.kt" || it.name == "Main.kt" || it.name == "MainActivity.java"
        } ?: project.files.values.firstOrNull { !it.isDirectory }

        if (entryFile == null) {
            onLog(
                LogcatEntry(
                    id = System.nanoTime().toString(),
                    timestamp = timeStr,
                    level = LogLevel.ERROR,
                    tag = "Compiler",
                    message = "No valid source code entry file found in project '${project.name}'"
                )
            )
            onDebuggerUpdate(DebuggerState(isRunning = false, isBuildSuccess = false))
            return
        }

        // Audit safety
        val safetyLogs = SecuritySandbox.auditCodeSafety(entryFile.content)
        safetyLogs.forEach { onLog(it) }

        // Execute lines and track variables
        val lines = entryFile.content.lines()
        val variables = mutableListOf<VariableItem>()
        val callStack = mutableListOf("main() in ${entryFile.name}", "android.app.ActivityThread.main()")
        val activeBreakpoints = breakpoints[entryFile.id] ?: entryFile.breakpoints

        var pausedAtLine: Int? = null

        lines.forEachIndexed { idx, rawLine ->
            val lineNum = idx + 1
            val line = rawLine.trim()

            // Check Breakpoints
            if (activeBreakpoints.contains(lineNum) && pausedAtLine == null) {
                pausedAtLine = lineNum
                onLog(
                    LogcatEntry(
                        id = System.nanoTime().toString(),
                        timestamp = timeStr,
                        level = LogLevel.WARN,
                        tag = "Debugger",
                        message = "Breakpoint hit at ${entryFile.name}:$lineNum -> '$line'",
                        sourceFile = entryFile.name,
                        lineNumber = lineNum
                    )
                )
            }

            // Variable assignment parsing (val x = 10, int y = 20, var msg = "Hello")
            if (line.startsWith("val ") || line.startsWith("var ") || line.startsWith("int ") || line.startsWith("String ") || line.startsWith("boolean ")) {
                val parts = line.split("=")
                if (parts.size >= 2) {
                    val decl = parts[0].trim().split(" ")
                    val varName = decl.last()
                    val varType = if (decl.size > 1) decl[decl.size - 2].replace("val", "Auto").replace("var", "Auto") else "String"
                    val varVal = parts[1].trim().trimEnd(';', ',')
                    variables.add(VariableItem(varName, varType, varVal))
                }
            }

            // Print statement / Log statement parsing
            if (line.contains("println(") || line.contains("System.out.println(") || line.contains("Log.d(") || line.contains("Log.i(") || line.contains("Log.e(")) {
                val msg = extractPrintMessage(line)
                val level = when {
                    line.contains("Log.e(") -> LogLevel.ERROR
                    line.contains("Log.w(") -> LogLevel.WARN
                    line.contains("Log.i(") -> LogLevel.INFO
                    else -> LogLevel.DEBUG
                }
                onLog(
                    LogcatEntry(
                        id = System.nanoTime().toString(),
                        timestamp = timeStr,
                        level = level,
                        tag = entryFile.name.substringBefore("."),
                        message = msg,
                        sourceFile = entryFile.name,
                        lineNumber = lineNum
                    )
                )
            }
        }

        // Final debugger state update
        onDebuggerUpdate(
            DebuggerState(
                isRunning = true,
                isPaused = pausedAtLine != null,
                currentFileId = entryFile.id,
                currentLine = pausedAtLine,
                variables = variables.ifEmpty {
                    listOf(
                        VariableItem("appName", "String", "\"${project.name}\""),
                        VariableItem("package", "String", "\"${project.packageName}\""),
                        VariableItem("isMobileOptimized", "Boolean", "true"),
                        VariableItem("systemTime", "Long", System.currentTimeMillis().toString())
                    )
                },
                callStack = callStack,
                isBuildSuccess = true,
                executionOutput = "App launched successfully on-device."
            )
        )
    }

    private fun extractPrintMessage(line: String): String {
        val start = line.indexOf('(')
        val end = line.lastIndexOf(')')
        if (start != -1 && end != -1 && end > start) {
            val inner = line.substring(start + 1, end).trim()
            return inner.removeSurrounding("\"").removeSurrounding("'")
        }
        return line
    }
}
