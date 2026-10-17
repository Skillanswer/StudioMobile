# Collapsible File Tree Explorer (Android Studio Mobile)

An interactive, touch-optimized, hierarchical Collapsible File Tree Explorer component for managing project folder structures in Android Studio Mobile. Integrates smooth chevron animations, 48dp touch targets, subtle vertical indentation guidelines, and an inline contextual action bar for file/folder manipulation (New File, Delete, Rename, Add Breakpoint) without visual clutter.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the following File Tree features during Phase 1 clarification:
> - **Touch-Optimized Collapsible Tree**: Animated chevrons (`ExpandMore` / `ChevronRight`), subtle vertical indent guide lines, and 48dp+ interactive touch targets per folder/file node.
> - **Hierarchical Folder Structure**: Displays nested directories (`app/`, `src/`, `main/`, `java/`, `res/`, `values/`) with automatic depth indentation.
> - **Contextual File Actions Bar**: Selected files or folders reveal inline quick-action buttons for **New File inside Folder**, **Delete Node**, **Rename Node**, and **Toggle Breakpoint**.
> - **Visual Minimalism**: Clean dark surfaces (`#1B1C20`), distinct file extension icons, active selection highlight, and zero clutter.

---

## 1. Overview & Core Concept

The **Collapsible File Tree Explorer** provides a true IDE folder hierarchy experience on mobile screens. It organizes complex Android projects (`:app`, `:core`, `:data`, `res/layout`, `values/strings.xml`) into collapsible directory nodes with smooth touch navigation.

### Key Features
1. **Collapsible Directory Nodes**: Expand and collapse nested folders with rotation animations on chevron icons.
2. **Indentation Guidelines**: Faint vertical guidelines visually connect parent folders to their child files/folders.
3. **Contextual Node Actions Bar**: Tapping a folder or file displays an inline touch toolbar with options to:
   - ➕ Add new file directly inside the target folder
   - ✏️ Rename file/folder
   - 🗑️ Delete file/folder
   - 🔴 Toggle Debug Breakpoint
4. **Touch Ergonomics**: All node rows enforce 48dp height to prevent accidental mis-taps on phone touchscreens.

---

## 2. User Experience & Visual Design

### Visual Identity & Layout
- **Tree Canvas**: Dark IDE sidebar canvas (`#1B1C20`) with `#2E3036` indent guidelines.
- **Node Highlighting**:
  - Folders: Amber/Gold folder icons (`#E58A5C`) with animated rotation chevrons.
  - Kotlin (`.kt`): Coral/Orange icon (`#E58A5C`)
  - Java (`.java`): Azure/Blue icon (`#62A8F3`)
  - XML (`.xml`): Gold/Yellow icon (`#D4C95A`)
  - JSON (`.json`): Cyan/Green icon (`#36C5D3`)
- **Active Node Pill**: Selected file or folder receives a `#3574F0` blue tint with rounded corners.

---

## 3. Technical Architecture & Component Structure

### Node Hierarchy & Model Strategy
- `FileTreeNode`: `id`, `name`, `relativePath`, `isDirectory`, `isExpanded`, `depthLevel`, `children`, `extension`, `breakpoints`.
- `FileTreeViewModel` / `ProjectRepository`: Transforms flat project files into a nested tree structure and updates expansion/collapse state.

```
┌────────────────────────────────────────────────────────────────────────┐
│               Collapsible File Tree Explorer Component                 │
├────────────────────────────────────────────────────────────────────────┤
│ 📂 app/                                                        [+] [⚙]│
│ │── 📂 src/                                                           │
│ │   │── 📂 main/                                                       │
│ │   │   │── 📂 java/com/example/                                       │
│ │   │   │   │── 📄 MainActivity.kt                        [+] [✏] [🗑] │
│ │   │   │   └── 📄 UserViewModel.kt                                   │
│ │   │   └── 📂 res/                                                    │
│ │   │       └── 📂 layout/                                             │
│ │   │           └── 📄 activity_main.xml                              │
│ │   └── 📄 AndroidManifest.xml                                         │
│ └── 📄 build.gradle.kts                                                │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Implementation Roadmap & Verification

1. **Tree Data Model (`FileTreeNode`)**:
   - Create recursive tree node builder in `IdeModels.kt` and `ProjectRepository.kt`.
2. **`CollapsibleFileTree` Composable**:
   - Build recursive node renderer with animated chevrons, vertical depth guides, and 48dp rows.
   - Add inline contextual touch action bar for New File, Rename, Delete, Breakpoint.
3. **Integration in `DrawerContent.kt`**:
   - Replace flat list with `CollapsibleFileTree` in the slide-over navigation drawer.
4. **Verification**:
   - Run `compile_applet` to verify zero build or syntax errors.
