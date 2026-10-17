package com.example.lexer

import com.example.model.FileType
import com.example.model.SyntaxToken
import com.example.model.TokenType
import java.util.regex.Pattern

object CodeLexer {

    private val KOTLIN_KEYWORDS = setOf(
        "package", "import", "class", "interface", "object", "val", "var", "fun",
        "return", "if", "else", "when", "for", "while", "do", "break", "continue",
        "try", "catch", "finally", "throw", "null", "true", "false", "this", "super",
        "is", "as", "in", "by", "get", "set", "private", "protected", "public",
        "internal", "open", "final", "abstract", "sealed", "override", "lateinit",
        "companion", "data", "enum", "suspend", "typealias", "inline", "value"
    )

    private val JAVA_KEYWORDS = setOf(
        "package", "import", "public", "private", "protected", "class", "interface",
        "enum", "extends", "implements", "static", "final", "abstract", "void",
        "int", "double", "float", "long", "short", "byte", "boolean", "char",
        "return", "if", "else", "switch", "case", "default", "for", "while", "do",
        "break", "continue", "try", "catch", "finally", "throw", "throws", "new",
        "this", "super", "null", "true", "false", "instanceof", "synchronized"
    )

    private val COMMON_TYPES = setOf(
        "String", "Int", "Boolean", "Double", "Float", "Long", "List", "Map", "Set",
        "Modifier", "State", "MutableState", "ViewModel", "StateFlow", "Flow", "Context",
        "Bundle", "Activity", "View", "Log", "Integer", "Object", "Exception"
    )

    fun tokenize(content: String, fileType: FileType): List<SyntaxToken> {
        if (content.isEmpty()) return emptyList()

        return when (fileType) {
            FileType.KOTLIN, FileType.GRADLE -> tokenizeKotlinJava(content, isKotlin = true)
            FileType.JAVA -> tokenizeKotlinJava(content, isKotlin = false)
            FileType.XML -> tokenizeXml(content)
            FileType.JSON -> tokenizeJson(content)
            else -> tokenizePlainText(content)
        }
    }

    private fun tokenizeKotlinJava(content: String, isKotlin: Boolean): List<SyntaxToken> {
        val keywords = if (isKotlin) KOTLIN_KEYWORDS else JAVA_KEYWORDS
        val tokens = mutableListOf<SyntaxToken>()
        val lines = content.lines()

        var currentOffset = 0
        lines.forEachIndexed { lineIdx, line ->
            val lineNumber = lineIdx + 1
            var col = 0
            while (col < line.length) {
                val ch = line[col]

                // Line Comments
                if (col + 1 < line.length && ch == '/' && line[col + 1] == '/') {
                    val commentText = line.substring(col)
                    tokens.add(
                        SyntaxToken(
                            commentText,
                            TokenType.COMMENT,
                            currentOffset + col,
                            currentOffset + line.length,
                            lineNumber
                        )
                    )
                    break
                }

                // Annotations (@Composable, @Override)
                if (ch == '@') {
                    val startCol = col
                    col++
                    while (col < line.length && (line[col].isLetterOrDigit() || line[col] == '_')) {
                        col++
                    }
                    val annText = line.substring(startCol, col)
                    tokens.add(
                        SyntaxToken(
                            annText,
                            TokenType.ANNOTATION,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                    continue
                }

                // Strings
                if (ch == '"' || ch == '\'') {
                    val quote = ch
                    val startCol = col
                    col++
                    while (col < line.length) {
                        if (line[col] == quote && (col == 0 || line[col - 1] != '\\')) {
                            col++
                            break
                        }
                        col++
                    }
                    val strText = line.substring(startCol, col)
                    tokens.add(
                        SyntaxToken(
                            strText,
                            TokenType.STRING,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                    continue
                }

                // Numbers
                if (ch.isDigit()) {
                    val startCol = col
                    while (col < line.length && (line[col].isLetterOrDigit() || line[col] == '.')) {
                        col++
                    }
                    val numText = line.substring(startCol, col)
                    tokens.add(
                        SyntaxToken(
                            numText,
                            TokenType.NUMBER,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                    continue
                }

                // Identifiers / Keywords / Types / Functions
                if (ch.isLetter() || ch == '_') {
                    val startCol = col
                    while (col < line.length && (line[col].isLetterOrDigit() || line[col] == '_')) {
                        col++
                    }
                    val word = line.substring(startCol, col)
                    val tokenType = when {
                        keywords.contains(word) -> TokenType.KEYWORD
                        COMMON_TYPES.contains(word) -> TokenType.TYPE
                        col < line.length && line[col] == '(' -> TokenType.FUNCTION
                        word.first().isUpperCase() -> TokenType.TYPE
                        else -> TokenType.IDENTIFIER
                    }
                    tokens.add(
                        SyntaxToken(
                            word,
                            tokenType,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                    continue
                }

                // Operators & Punctuation
                if (!ch.isWhitespace()) {
                    tokens.add(
                        SyntaxToken(
                            ch.toString(),
                            TokenType.OPERATOR,
                            currentOffset + col,
                            currentOffset + col + 1,
                            lineNumber
                        )
                    )
                }

                col++
            }
            currentOffset += line.length + 1 // +1 for newline character
        }

        return tokens
    }

    private fun tokenizeXml(content: String): List<SyntaxToken> {
        val tokens = mutableListOf<SyntaxToken>()
        val lines = content.lines()
        var currentOffset = 0

        lines.forEachIndexed { lineIdx, line ->
            val lineNumber = lineIdx + 1
            var col = 0
            while (col < line.length) {
                val ch = line[col]
                if (ch == '<' || ch == '>' || ch == '/' || ch == '=') {
                    tokens.add(
                        SyntaxToken(
                            ch.toString(),
                            TokenType.OPERATOR,
                            currentOffset + col,
                            currentOffset + col + 1,
                            lineNumber
                        )
                    )
                    col++
                } else if (ch == '"') {
                    val startCol = col
                    col++
                    while (col < line.length && line[col] != '"') col++
                    if (col < line.length) col++
                    tokens.add(
                        SyntaxToken(
                            line.substring(startCol, col),
                            TokenType.STRING,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                } else if (ch.isLetterOrDigit() || ch == ':' || ch == '_') {
                    val startCol = col
                    while (col < line.length && (line[col].isLetterOrDigit() || line[col] == ':' || line[col] == '_' || line[col] == '-')) col++
                    val word = line.substring(startCol, col)
                    val type = if (word.contains(":")) TokenType.KEYWORD else TokenType.TYPE
                    tokens.add(
                        SyntaxToken(
                            word,
                            type,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                } else {
                    col++
                }
            }
            currentOffset += line.length + 1
        }
        return tokens
    }

    private fun tokenizeJson(content: String): List<SyntaxToken> {
        val tokens = mutableListOf<SyntaxToken>()
        val lines = content.lines()
        var currentOffset = 0

        lines.forEachIndexed { lineIdx, line ->
            val lineNumber = lineIdx + 1
            var col = 0
            while (col < line.length) {
                val ch = line[col]
                if (ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == ':' || ch == ',') {
                    tokens.add(
                        SyntaxToken(
                            ch.toString(),
                            TokenType.OPERATOR,
                            currentOffset + col,
                            currentOffset + col + 1,
                            lineNumber
                        )
                    )
                    col++
                } else if (ch == '"') {
                    val startCol = col
                    col++
                    while (col < line.length && line[col] != '"') col++
                    if (col < line.length) col++
                    val str = line.substring(startCol, col)
                    val isKey = col < line.length && line.substring(col).trimStart().startsWith(":")
                    tokens.add(
                        SyntaxToken(
                            str,
                            if (isKey) TokenType.KEYWORD else TokenType.STRING,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                } else if (ch.isDigit() || ch == '-') {
                    val startCol = col
                    while (col < line.length && (line[col].isDigit() || line[col] == '.')) col++
                    tokens.add(
                        SyntaxToken(
                            line.substring(startCol, col),
                            TokenType.NUMBER,
                            currentOffset + startCol,
                            currentOffset + col,
                            lineNumber
                        )
                    )
                } else {
                    col++
                }
            }
            currentOffset += line.length + 1
        }
        return tokens
    }

    private fun tokenizePlainText(content: String): List<SyntaxToken> {
        return content.lines().flatMapIndexed { lineIdx, line ->
            listOf(
                SyntaxToken(
                    line,
                    TokenType.DEFAULT,
                    0,
                    line.length,
                    lineIdx + 1
                )
            )
        }
    }
}
