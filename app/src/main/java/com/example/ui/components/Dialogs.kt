package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interpreter.KeystoreManager
import com.example.model.BuildResult
import com.example.model.BuildTarget
import com.example.model.KeystoreConfig
import com.example.model.Project
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
fun RenameProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onRename: (newName: String) -> Unit
) {
    var name by remember { mutableStateOf(project.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Project", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onRename(name)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun CloneProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onClone: (clonedName: String) -> Unit
) {
    var name by remember { mutableStateOf("${project.name}_Copy") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clone Project Workspace", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Create an exact duplicate of '${project.name}' with all files preserved:",
                    fontSize = 12.sp,
                    color = IdeComment
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("New Cloned Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onClone(name)
                }
            ) {
                Text("Clone")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun DeleteProjectConfirmDialog(
    project: Project,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Project?", color = IdeDebugRed) },
        text = {
            Text(
                text = "Are you sure you want to delete '${project.name}'? All files in this project workspace will be permanently removed.",
                fontSize = 13.sp,
                color = TokenIdentifier
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = IdeDebugRed)
            ) {
                Text("Delete")
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
fun BuildSignDialog(
    onDismiss: () -> Unit,
    onBuildTrigger: (target: BuildTarget, keystore: KeystoreConfig?) -> Unit,
    onOpenKeystoreManager: () -> Unit,
    activeKeystore: KeystoreConfig?
) {
    var selectedTarget by remember { mutableStateOf(BuildTarget.SIGNED_RELEASE_APK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📦 Build & Sign Binary (APK / AAB)", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select Output Target:", fontSize = 12.sp, color = IdeComment)
                Spacer(modifier = Modifier.height(6.dp))

                BuildTarget.values().forEach { target ->
                    val isSelected = selectedTarget == target
                    Surface(
                        onClick = { selectedTarget = target },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) IdePrimary.copy(alpha = 0.25f) else IdeSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedTarget = target })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(target.displayName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(".${target.extension} binary output", color = IdeComment, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keystore Signing Information
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IdeSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔑 Signing Certificate:", fontSize = 11.sp, color = IdeComment)
                            TextButton(onClick = onOpenKeystoreManager) {
                                Text("Manage JKS Keystore", fontSize = 11.sp, color = IdePrimary)
                            }
                        }

                        if (activeKeystore != null) {
                            Text("Alias: ${activeKeystore.alias}", fontSize = 12.sp, color = IdeRunGreen, fontWeight = FontWeight.Bold)
                            Text("Validity: ${activeKeystore.validityYears} years (${activeKeystore.commonName})", fontSize = 10.sp, color = TokenIdentifier)
                            Text("Fingerprint: ${activeKeystore.sha256Fingerprint.take(30)}...", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = IdeComment)
                        } else {
                            Text("No Keystore Configured. Default Debug Keystore will be generated.", fontSize = 11.sp, color = IdeWarnYellow)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onBuildTrigger(selectedTarget, activeKeystore)
                },
                modifier = Modifier.testTag("trigger_build_btn")
            ) {
                Text("Build & Sign (${selectedTarget.extension.uppercase()})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun KeystoreManagerDialog(
    onDismiss: () -> Unit,
    onCreateKeystore: (alias: String, storePass: String, keyPass: String, validityYears: Int, name: String, org: String) -> Unit
) {
    var alias by remember { mutableStateOf("release_key") }
    var storePass by remember { mutableStateOf("storepass123") }
    var keyPass by remember { mutableStateOf("keypass123") }
    var validity by remember { mutableStateOf("25") }
    var commonName by remember { mutableStateOf("StudioMobile") }
    var organization by remember { mutableStateOf("Mobile App Developer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔑 Create & Configure JKS Keystore", color = Color.White) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Key Alias") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = storePass,
                    onValueChange = { storePass = it },
                    label = { Text("Keystore Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = keyPass,
                    onValueChange = { keyPass = it },
                    label = { Text("Key Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = commonName,
                    onValueChange = { commonName = it },
                    label = { Text("Certificate Owner Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val years = validity.toIntOrNull() ?: 25
                    onCreateKeystore(alias, storePass, keyPass, years, commonName, organization)
                }
            ) {
                Text("Generate Keystore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = IdeComment) }
        },
        containerColor = IdeSurface
    )
}

@Composable
fun BuildResultDialog(
    result: BuildResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (result.isSuccess) "🎉 Build & Signing Successful!" else "❌ Build Failed",
                color = if (result.isSuccess) IdeRunGreen else IdeDebugRed
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IdeSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Output Binary: ${result.fileName}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text("Size: ${result.fileSizeKb / 1024} MB (${result.fileSizeKb} KB)", color = IdeComment, fontSize = 11.sp)
                        Text("Path: ${result.filePath}", color = TokenFunction, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("SHA-256 Signature:", fontSize = 10.sp, color = IdeComment)
                        Text(result.signatureSha256, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = IdeWarnYellow)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Build Terminal Output:", fontSize = 11.sp, color = IdeComment, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .height(120.dp)
                        .background(IdeBackground, shape = RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    items(result.logs) { log ->
                        Text(log, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TokenIdentifier)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
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
                    text = "StudioMobile strictly protects user data and system boundaries:",
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
