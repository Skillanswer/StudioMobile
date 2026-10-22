package com.example.interpreter

import com.example.model.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TerminalEngine {

    fun executeCommand(command: String, project: Project): String {
        val trimmed = command.trim()
        if (trimmed.isEmpty()) return ""

        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

        return when {
            trimmed.startsWith("gradle assembleDebug") || trimmed == "gradle build" -> """
                [${timeStr}] $ gradle assembleDebug
                > Task :app:preBuild UP-TO-DATE
                > Task :app:compileDebugKotlin SUCCESS
                > Task :app:mergeDebugResources SUCCESS
                > Task :app:assembleDebug SUCCESS
                BUILD SUCCESSFUL in 1.42s (ARM SoC Accelerated)
            """.trimIndent()

            trimmed.startsWith("gradle bundleRelease") -> """
                [${timeStr}] $ gradle bundleRelease
                > Task :app:preBuild UP-TO-DATE
                > Task :app:bundleRelease SUCCESS
                Android App Bundle (.aab) created at /sdcard/Download/builds/${project.name.lowercase()}-release.aab
                BUILD SUCCESSFUL in 2.15s
            """.trimIndent()

            trimmed == "git status" -> """
                [${timeStr}] $ git status
                On branch main
                Your branch is up to date with 'origin/main'.

                Changes not staged for commit:
                  modified:   ${project.files.values.firstOrNull { it.isModified }?.relativePath ?: "app/src/main/java/MainActivity.kt"}

                no changes added to commit (use "git add" and/or "git commit")
            """.trimIndent()

            trimmed == "git branch" -> """
                [${timeStr}] $ git branch
                * main
                  feature/compose-ui
                  release/v1.0
            """.trimIndent()

            trimmed == "ls" || trimmed == "ls -la" -> """
                [${timeStr}] $ ls -la
                drwxr-xr-x 4 dev dev 4096 Oct 21 09:50 app/
                drwxr-xr-x 2 dev dev 4096 Oct 21 09:50 gradle/
                -rw-r--r-- 1 dev dev  554 Oct 21 09:50 build.gradle.kts
                -rw-r--r-- 1 dev dev  120 Oct 21 09:50 settings.gradle.kts
                -rw-r--r-- 1 dev dev  140 Oct 21 09:50 .gitignore
            """.trimIndent()

            trimmed == "pwd" -> "[${timeStr}] $ pwd\n/workspace/${project.name}"
            trimmed == "whoami" -> "[${timeStr}] $ whoami\nandroid-developer@arm64-soc"
            trimmed == "date" -> "[${timeStr}] $ date\n${Date()}"
            trimmed.startsWith("echo ") -> "[${timeStr}] $ $trimmed\n${trimmed.removePrefix("echo ")}"
            trimmed == "clear" -> ""

            else -> """
                [${timeStr}] $ $trimmed
                Command executed: '$trimmed'
                Output: OK (ARM SoC Shell)
            """.trimIndent()
        }
    }
}
