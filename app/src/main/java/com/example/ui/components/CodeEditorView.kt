package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexer.CodeLexer
import com.example.model.ProjectFile
import com.example.model.TokenType
import com.example.ui.theme.*

@Composable
fun CodeEditorView(
    file: ProjectFile,
    activeLine: Int?,
    onContentChange: (String) -> Unit,
    onBreakpointToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lines = remember(file.content) { file.content.lines() }
    val tokens = remember(file.content, file.fileType) {
        CodeLexer.tokenize(file.content, file.fileType)
    }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    var selectedLine by remember { mutableStateOf(1) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IdeBackground)
            .testTag("code_editor_view")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
        ) {
            // Line Numbers & Breakpoint Gutter Column
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .background(IdeSurface)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                lines.forEachIndexed { idx, _ ->
                    val lineNum = idx + 1
                    val hasBreakpoint = file.breakpoints.contains(lineNum)
                    val isPausedLine = activeLine == lineNum

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clickable {
                                onBreakpointToggle(lineNum)
                                selectedLine = lineNum
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Breakpoint Dot or Line Number
                        if (hasBreakpoint || isPausedLine) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (isPausedLine) IdeWarnYellow else IdeDebugRed,
                                        shape = CircleShape
                                    )
                            )
                        } else {
                            Text(
                                text = lineNum.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = if (selectedLine == lineNum) Color.White else IdeComment
                            )
                        }
                    }
                }
            }

            // Code Content & Editing Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScroll)
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            ) {
                lines.forEachIndexed { idx, lineText ->
                    val lineNum = idx + 1
                    val isPaused = activeLine == lineNum
                    val isSelected = selectedLine == lineNum

                    val lineTokens = remember(tokens, lineNum) {
                        tokens.filter { it.line == lineNum }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                when {
                                    isPaused -> IdeWarnYellow.copy(alpha = 0.25f)
                                    isSelected -> IdeSurfaceVariant.copy(alpha = 0.6f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { selectedLine = lineNum }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        val annotated = buildAnnotatedString {
                            if (lineTokens.isEmpty()) {
                                append(lineText)
                            } else {
                                lineTokens.forEach { token ->
                                    val tokenColor = when (token.type) {
                                        TokenType.KEYWORD -> TokenKeyword
                                        TokenType.TYPE -> TokenTypeColor
                                        TokenType.STRING -> TokenString
                                        TokenType.FUNCTION -> TokenFunction
                                        TokenType.NUMBER -> TokenNumber
                                        TokenType.COMMENT -> TokenComment
                                        TokenType.ANNOTATION -> TokenAnnotation
                                        TokenType.OPERATOR -> TokenOperator
                                        else -> TokenIdentifier
                                    }
                                    withStyle(SpanStyle(color = tokenColor)) {
                                        append(token.text)
                                    }
                                    append(" ")
                                }
                            }
                        }

                        Text(
                            text = annotated,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = TokenIdentifier,
                            maxLines = 1
                        )
                    }
                }

                // Interactive Text Input Field for Editing Code
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = file.content,
                    onValueChange = onContentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("code_text_field"),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = TokenIdentifier
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IdePrimary,
                        unfocusedBorderColor = IdeBorder,
                        focusedContainerColor = IdeSurface.copy(alpha = 0.5f),
                        unfocusedContainerColor = IdeSurface.copy(alpha = 0.2f)
                    ),
                    minLines = 4,
                    label = { Text("Edit Code (${file.name})", fontSize = 11.sp, color = IdeComment) }
                )
            }
        }
    }
}
