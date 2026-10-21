package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FileTreeNode
import com.example.model.Project
import com.example.ui.theme.*

@Composable
fun DrawerContent(
    project: Project,
    treeNodes: List<FileTreeNode>,
    onFileSelect: (String) -> Unit,
    onFolderToggle: (String) -> Unit,
    onDeleteNode: (FileTreeNode) -> Unit,
    onRenameNode: (FileTreeNode) -> Unit,
    onAddFileInFolder: (String) -> Unit,
    onToggleBreakpoint: (String) -> Unit,
    onNewFileClick: () -> Unit,
    onNewProjectClick: () -> Unit,
    onBuildSignClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSecurityReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .testTag("drawer_content"),
        color = IdeSurface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: App Logo & Project Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "IDE Logo",
                    tint = IdePrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Android Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = IdePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row: New File, New Project, Build & Sign, Search, Security Audit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNewFileClick,
                    modifier = Modifier.testTag("new_file_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.NoteAdd,
                        contentDescription = "New File",
                        tint = TokenKeyword
                    )
                }

                IconButton(
                    onClick = onNewProjectClick,
                    modifier = Modifier.testTag("new_project_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "New Project",
                        tint = IdeRunGreen
                    )
                }

                IconButton(
                    onClick = onBuildSignClick,
                    modifier = Modifier.testTag("build_sign_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Build APK/AAB",
                        tint = IdePrimary
                    )
                }

                IconButton(
                    onClick = onSecurityReportClick,
                    modifier = Modifier.testTag("security_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Audit",
                        tint = IdeWarnYellow
                    )
                }
            }

            HorizontalDivider(color = IdeBorder, modifier = Modifier.padding(vertical = 12.dp))

            // Project Files Tree Header
            Text(
                text = "PROJECT STRUCTURE",
                style = MaterialTheme.typography.labelSmall,
                color = IdeComment,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Collapsible File Tree View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                CollapsibleFileTree(
                    nodes = treeNodes,
                    activeFileId = project.activeFileId,
                    onNodeSelect = { node ->
                        if (node.fileId != null) onFileSelect(node.fileId)
                    },
                    onFolderToggle = onFolderToggle,
                    onDeleteNode = onDeleteNode,
                    onRenameNode = onRenameNode,
                    onAddFileInFolder = onAddFileInFolder,
                    onToggleBreakpoint = onToggleBreakpoint
                )
            }

            HorizontalDivider(color = IdeBorder, modifier = Modifier.padding(vertical = 12.dp))

            // Build & Sign Quick Launch Bar
            Surface(
                onClick = onBuildSignClick,
                shape = RoundedCornerShape(8.dp),
                color = IdePrimary.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.BuildCircle, contentDescription = null, tint = IdePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Build APK / AAB & Sign", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Active Template Footer Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = IdeSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Template",
                        tint = TokenAnnotation,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = project.templateType.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                        Text(
                            text = project.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = IdeComment,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
