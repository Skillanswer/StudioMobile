package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FileTreeNode
import com.example.ui.theme.*

@Composable
fun CollapsibleFileTree(
    nodes: List<FileTreeNode>,
    activeFileId: String?,
    onNodeSelect: (FileTreeNode) -> Unit,
    onFolderToggle: (String) -> Unit,
    onDeleteNode: (FileTreeNode) -> Unit,
    onRenameNode: (FileTreeNode) -> Unit,
    onAddFileInFolder: (String) -> Unit,
    onToggleBreakpoint: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedNodeId by remember { mutableStateOf<String?>(activeFileId) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("collapsible_file_tree")
    ) {
        nodes.forEach { node ->
            TreeNodeItem(
                node = node,
                selectedNodeId = selectedNodeId,
                activeFileId = activeFileId,
                onNodeClick = { selectedNode ->
                    selectedNodeId = selectedNode.id
                    if (selectedNode.isDirectory) {
                        onFolderToggle(selectedNode.relativePath)
                    } else if (selectedNode.fileId != null) {
                        onNodeSelect(selectedNode)
                    }
                },
                onDeleteNode = onDeleteNode,
                onRenameNode = onRenameNode,
                onAddFileInFolder = onAddFileInFolder,
                onToggleBreakpoint = onToggleBreakpoint
            )
        }
    }
}

@Composable
fun TreeNodeItem(
    node: FileTreeNode,
    selectedNodeId: String?,
    activeFileId: String?,
    onNodeClick: (FileTreeNode) -> Unit,
    onDeleteNode: (FileTreeNode) -> Unit,
    onRenameNode: (FileTreeNode) -> Unit,
    onAddFileInFolder: (String) -> Unit,
    onToggleBreakpoint: (String) -> Unit
) {
    val isSelected = selectedNodeId == node.id || (node.fileId != null && node.fileId == activeFileId)
    val rotateDegree by animateFloatAsState(targetValue = if (node.isExpanded) 90f else 0f, label = "chevron")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Node Touch Row (48dp Touch Target)
        Surface(
            onClick = { onNodeClick(node) },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) IdePrimary.copy(alpha = 0.22f) else Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(vertical = 2.dp)
                .minimumInteractiveComponentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indent spacing & subtle depth guideline
                if (node.depth > 0) {
                    repeat(node.depth) {
                        Row(
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight(0.7f)
                                    .background(IdeBorder)
                            )
                        }
                    }
                }

                // Chevron for folders / Spacer for files
                if (node.isDirectory) {
                    IconButton(
                        onClick = { onNodeClick(node) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Expand Folder",
                            tint = IdeComment,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotateDegree)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }

                // File/Folder Icon
                val icon = when {
                    node.isDirectory -> if (node.isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder
                    node.extension == "kt" || node.extension == "kts" -> Icons.Default.Code
                    node.extension == "java" -> Icons.Default.DeveloperMode
                    node.extension == "xml" -> Icons.Default.DataObject
                    node.extension == "json" -> Icons.Default.DataObject
                    else -> Icons.Default.Description
                }

                val tint = when {
                    node.isDirectory -> TokenKeyword
                    node.extension == "kt" || node.extension == "kts" -> TokenKeyword
                    node.extension == "java" -> TokenFunction
                    node.extension == "xml" -> TokenAnnotation
                    node.extension == "json" -> TokenNumber
                    else -> TokenIdentifier
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // File/Folder Name
                Text(
                    text = node.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) IdePrimary else Color.White,
                    fontWeight = if (isSelected || node.isDirectory) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                // Breakpoint Dot
                if (node.breakpoints.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(IdeDebugRed, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        // Inline Contextual Touch Action Bar for Selected Node
        AnimatedVisibility(visible = isSelected) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = ((node.depth * 16) + 36).dp, end = 8.dp, top = 2.dp, bottom = 6.dp),
                shape = RoundedCornerShape(6.dp),
                color = IdeSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, IdeBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (node.isDirectory) {
                        IconButton(
                            onClick = { onAddFileInFolder(node.relativePath) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add File in Folder", tint = IdeRunGreen)
                        }
                    } else if (node.fileId != null) {
                        IconButton(
                            onClick = { onToggleBreakpoint(node.fileId) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = "Toggle Breakpoint", tint = IdeDebugRed)
                        }
                    }

                    IconButton(
                        onClick = { onRenameNode(node) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TokenFunction)
                    }

                    IconButton(
                        onClick = { onDeleteNode(node) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = IdeDebugRed)
                    }
                }
            }
        }

        // Render Children Recursively if Expanded
        if (node.isDirectory && node.isExpanded) {
            node.children.forEach { child ->
                TreeNodeItem(
                    node = child,
                    selectedNodeId = selectedNodeId,
                    activeFileId = activeFileId,
                    onNodeClick = onNodeClick,
                    onDeleteNode = onDeleteNode,
                    onRenameNode = onRenameNode,
                    onAddFileInFolder = onAddFileInFolder,
                    onToggleBreakpoint = onToggleBreakpoint
                )
            }
        }
    }
}
