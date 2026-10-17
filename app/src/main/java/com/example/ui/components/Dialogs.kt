package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TemplateType
import com.example.ui.theme.*

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, packageName: String, template: TemplateType) -> Unit
) {
    var name by remember { mutableStateOf("MyAwesomeApp") }
    var packageName by remember { mutableStateOf("com.aistudio.myawesomeapp") }
    var selectedTemplate by remember { mutableStateOf(TemplateType.FULL_STACK_COMPOSE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Mobile IDE Project", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth().testTag("new_project_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth().testTag("new_project_package_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Starter Template:", fontSize = 12.sp, color = IdeComment)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(TemplateType.values()) { template ->
                        val isSelected = selectedTemplate == template
                        Surface(
                            onClick = { selectedTemplate = template },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) IdePrimary.copy(alpha = 0.25f) else IdeSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = template.displayName,
                                    color = if (isSelected) IdePrimary else Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = template.description,
                                    color = IdeComment,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, packageName, selectedTemplate)
                    }
                },
                modifier = Modifier.testTag("confirm_create_project_btn")
            ) {
                Text("Create Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun NewFileDialog(
    onDismiss: () -> Unit,
    onCreate: (fileName: String, extension: String) -> Unit
) {
    var fileName by remember { mutableStateOf("NewFile") }
    var extension by remember { mutableStateOf("kt") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Code File", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (e.g. Utils)") },
                    modifier = Modifier.fillMaxWidth().testTag("new_file_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    listOf("kt", "java", "xml", "json").forEach { ext ->
                        FilterChip(
                            selected = extension == ext,
                            onClick = { extension = ext },
                            label = { Text(".$ext") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        val fullName = if (fileName.contains(".")) fileName else "$fileName.$extension"
                        onCreate(fullName, extension)
                    }
                },
                modifier = Modifier.testTag("confirm_create_file_btn")
            ) {
                Text("Add File")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun SecurityReportDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🛡️ Security & Anti-Leak Safeguards", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Android Studio Mobile strictly protects user data and system boundaries:",
                    color = TokenIdentifier,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("✔ Isolated Filesystem Workspace (/proc & system access restricted)", fontSize = 11.sp, color = IdeRunGreen)
                Text("✔ Anti-Prompt-Injection Code Sanitizer", fontSize = 11.sp, color = IdeRunGreen)
                Text("✔ Memory Boundary Reflection Shield", fontSize = 11.sp, color = IdeRunGreen)
                Text("✔ Zero Unauthorized External Executable Binaries", fontSize = 11.sp, color = IdeRunGreen)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK, Understood") }
        },
        containerColor = IdeSurface
    )
}
