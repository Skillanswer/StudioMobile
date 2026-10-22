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
    projects: List<Project>,
    onProjectSelect: (String) -> Unit,
    onRenameProjectClick: (Project) -> Unit,
    onCloneProjectClick: (Project) -> Unit,
    onDeleteProjectClick: (Project) -> Unit,
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
    var isWorkspacesExpanded by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "StudioMobile",
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

            // Action Buttons Row: New File, New Project, Build & Sign, Security Audit
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

            HorizontalDivider(color = IdeBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Scrollable Content: Workspaces Selector & File Tree
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Section: Projects & Workspaces Accordion
                Surface(
                    onClick = { isWorkspacesExpanded = !isWorkspacesExpanded },
                    shape = RoundedCornerShape(6.dp),
                    color = IdeSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FolderSpecial,
                                contentDescription = null,
                                tint = IdePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PROJECT WORKSPACES (${projects.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (isWorkspacesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Workspaces",
                            tint = IdeComment,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isWorkspacesExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        projects.forEach { p ->
                            val isActive = p.id == project.id
                            Surface(
                                onClick = { onProjectSelect(p.id) },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isActive) IdePrimary.copy(alpha = 0.2f) else IdeBackground,
                                border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, IdePrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isActive) IdeRunGreen else IdeComment,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isActive) Color.White else IdeComment,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = p.templateType.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TokenKeyword,
                                            fontSize = 9.sp
                                        )
                                    }

                                    // Action icons for each project: Clone, Rename, Delete
                                    IconButton(
                                        onClick = { onCloneProjectClick(p) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Clone Project",
                                            tint = IdeComment,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRenameProjectClick(p) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Rename Project",
                                            tint = TokenFunction,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    if (projects.size > 1) {
                                        IconButton(
                                            onClick = { onDeleteProjectClick(p) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Project",
                                                tint = IdeDebugRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Add Project Button inside workspace list
                        TextButton(
                            onClick = onNewProjectClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = IdeRunGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Create New Workspace", fontSize = 11.sp, color = IdeRunGreen)
                        }
                    }
                }

                HorizontalDivider(color = IdeBorder, modifier = Modifier.padding(vertical = 10.dp))

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

            HorizontalDivider(color = IdeBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Build & Sign Quick Launch Bar
            Surface(
                onClick = onBuildSignClick,
                shape = RoundedCornerShape(8.dp),
                color = IdePrimary.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
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
                    modifier = Modifier.padding(10.dp),
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
