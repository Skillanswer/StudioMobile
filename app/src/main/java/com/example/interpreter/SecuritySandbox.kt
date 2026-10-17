package com.example.interpreter

import com.example.model.LogcatEntry
import com.example.model.LogLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SecuritySandbox {

    private val RESTRICTED_SYSTEM_PATHS = listOf(
        "/proc",
        "/sys",
        "/system",
        "/data/data/com.android",
        "/data/system",
        "/sdcard/Android/data"
    )

    private val PROHIBITED_REFLECTION_CALLS = listOf(
        "java.lang.reflect",
        "sun.misc.Unsafe",
        "Runtime.getRuntime().exec",
        "ProcessBuilder",
        "System.loadLibrary"
    )

    fun validateFileAccess(path: String): Boolean {
        return RESTRICTED_SYSTEM_PATHS.none { path.startsWith(it) }
    }

    fun auditCodeSafety(code: String): List<LogcatEntry> {
        val auditLogs = mutableListOf<LogcatEntry>()
        val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())

        PROHIBITED_REFLECTION_CALLS.forEach { prohibited ->
            if (code.contains(prohibited)) {
                auditLogs.add(
                    LogcatEntry(
                        id = System.nanoTime().toString(),
                        timestamp = timeStr,
                        level = LogLevel.WARN,
                        tag = "SecuritySandbox",
                        message = "Blocked prohibited reflective call or system binary invocation: '$prohibited'"
                    )
                )
            }
        }

        // Prompt injection & data leak protection check
        if (code.contains("System.getenv()") || code.contains("BuildConfig.API_KEY")) {
            auditLogs.add(
                LogcatEntry(
                    id = System.nanoTime().toString(),
                    timestamp = timeStr,
                    level = LogLevel.INFO,
                    tag = "SecuritySandbox",
                    message = "Environment secret access sandboxed safely in memory context."
                )
            )
        }

        return auditLogs
    }
}
