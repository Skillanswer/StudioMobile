package com.example.interpreter

import com.example.model.FileType

data class SuggestionItem(
    val text: String,
    val detail: String,
    val isKeyword: Boolean = false,
    val isComposable: Boolean = false
)

object AutocompleteEngine {

    private val KOTLIN_SUGGESTIONS = listOf(
        SuggestionItem("val", "Variable declaration (read-only)", isKeyword = true),
        SuggestionItem("var", "Variable declaration (mutable)", isKeyword = true),
        SuggestionItem("fun", "Function declaration", isKeyword = true),
        SuggestionItem("class", "Class definition", isKeyword = true),
        SuggestionItem("interface", "Interface definition", isKeyword = true),
        SuggestionItem("@Composable", "Jetpack Compose UI component", isComposable = true),
        SuggestionItem("Modifier", "Compose layout modifier", isComposable = true),
        SuggestionItem("Column", "Vertical layout container", isComposable = true),
        SuggestionItem("Row", "Horizontal layout container", isComposable = true),
        SuggestionItem("Text", "Text display composable", isComposable = true),
        SuggestionItem("Button", "Interactive button composable", isComposable = true),
        SuggestionItem("remember", "Remember state across recompositions", isComposable = true),
        SuggestionItem("mutableStateOf", "Mutable state holder for Compose", isComposable = true),
        SuggestionItem("StateFlow", "Reactive state stream", isKeyword = true),
        SuggestionItem("println", "Print to standard log output"),
        SuggestionItem("Log.d", "Debug Logcat log entry"),
        SuggestionItem("Log.e", "Error Logcat log entry"),
        SuggestionItem("import", "Package import statement", isKeyword = true),
        SuggestionItem("package", "Package declaration", isKeyword = true)
    )

    private val JAVA_SUGGESTIONS = listOf(
        SuggestionItem("public", "Access modifier", isKeyword = true),
        SuggestionItem("private", "Access modifier", isKeyword = true),
        SuggestionItem("protected", "Access modifier", isKeyword = true),
        SuggestionItem("class", "Class definition", isKeyword = true),
        SuggestionItem("static", "Static member", isKeyword = true),
        SuggestionItem("void", "Return type void", isKeyword = true),
        SuggestionItem("String", "String object type", isKeyword = true),
        SuggestionItem("int", "Primitive integer type", isKeyword = true),
        SuggestionItem("boolean", "Primitive boolean type", isKeyword = true),
        SuggestionItem("System.out.println", "Print to console output"),
        SuggestionItem("Log.d", "Debug Logcat statement")
    )

    fun getSuggestions(query: String, fileType: FileType): List<SuggestionItem> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        val sourceList = when (fileType) {
            FileType.JAVA -> JAVA_SUGGESTIONS
            else -> KOTLIN_SUGGESTIONS
        }

        return sourceList.filter {
            it.text.contains(trimmed, ignoreCase = true)
        }.take(5)
    }
}
