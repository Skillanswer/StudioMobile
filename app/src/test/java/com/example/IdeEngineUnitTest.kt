package com.example

import com.example.interpreter.AstEngine
import com.example.interpreter.SecuritySandbox
import com.example.lexer.CodeLexer
import com.example.model.FileType
import com.example.model.LogLevel
import com.example.model.TemplateType
import com.example.model.TokenType
import com.example.repository.ProjectRepository
import org.junit.Assert.*
import org.junit.Test

class IdeEngineUnitTest {

    @Test
    fun testCodeLexerKotlinTokenization() {
        val code = "val name = \"Android Studio\"\nfun hello() {}"
        val tokens = CodeLexer.tokenize(code, FileType.KOTLIN)

        assertTrue(tokens.any { it.text == "val" && it.type == TokenType.KEYWORD })
        assertTrue(tokens.any { it.text == "fun" && it.type == TokenType.KEYWORD })
        assertTrue(tokens.any { it.text == "\"Android Studio\"" && it.type == TokenType.STRING })
    }

    @Test
    fun testCodeLexerJavaTokenization() {
        val code = "public class Main { int count = 10; }"
        val tokens = CodeLexer.tokenize(code, FileType.JAVA)

        assertTrue(tokens.any { it.text == "public" && it.type == TokenType.KEYWORD })
        assertTrue(tokens.any { it.text == "class" && it.type == TokenType.KEYWORD })
        assertTrue(tokens.any { it.text == "10" && it.type == TokenType.NUMBER })
    }

    @Test
    fun testProjectRepositoryTemplates() {
        val repo = ProjectRepository()
        val composeProj = repo.createProjectFromTemplate("TestCompose", "com.test.app", TemplateType.COMPOSE)

        assertEquals("TestCompose", composeProj.name)
        assertTrue(composeProj.files.values.any { it.name == "MainActivity.kt" })

        val viewsProj = repo.createProjectFromTemplate("TestViews", "com.test.views", TemplateType.VIEWS)
        assertTrue(viewsProj.files.values.any { it.name == "MainActivity.java" })
    }

    @Test
    fun testSecuritySandboxValidation() {
        assertTrue(SecuritySandbox.validateFileAccess("/workspace/project1"))
        assertFalse(SecuritySandbox.validateFileAccess("/proc/sys/kernel"))

        val auditLogs = SecuritySandbox.auditCodeSafety("java.lang.reflect.Method m = null;")
        assertTrue(auditLogs.any { it.level == LogLevel.WARN })
    }

    @Test
    fun testAstEngineExecution() {
        val repo = ProjectRepository()
        val proj = repo.createProjectFromTemplate("TestApp", "com.test.app", TemplateType.KOTLIN_PURE)
        val engine = AstEngine()

        var loggedCount = 0
        engine.executeProject(
            project = proj,
            onLog = { loggedCount++ },
            onDebuggerUpdate = { state ->
                assertTrue(state.isBuildSuccess)
            }
        )
        assertTrue(loggedCount > 0)
    }
}
