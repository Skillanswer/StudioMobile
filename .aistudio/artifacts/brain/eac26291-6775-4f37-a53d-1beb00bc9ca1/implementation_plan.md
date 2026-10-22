# Project Selector & Multi-Workspace Manager (Android Studio Mobile)

Integrates a seamless multi-project workspace manager into Android Studio Mobile. Developers can instantly switch projects via an intuitive top app bar dropdown or a dedicated recent projects section in the slide-over drawer, with full support for project switching, cloning, renaming, deleting, and template instantiation.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user confirmed the following choices during Phase 1 clarification:
> - **Access Points**: Dropdown switcher embedded directly in the top app bar (tapping the project title) alongside a Recent Projects list in the side drawer.
> - **Supported Operations**: Seamlessly switch active projects, clone existing projects, rename project metadata, and delete unwanted projects with confirmation safeguards.
> - **Clean Mobile Aesthetics**: Uncluttered, touch-optimized dialogs and dropdown menus adhering to the dark minimalist IDE theme with 48dp+ touch targets.

---

## 1. Overview & Core Concept

Currently, Android Studio Mobile allows creating new projects, but managing multiple existing workspaces simultaneously requires a centralized Project Selector. This update elevates Android Studio Mobile into a multi-workspace IDE where users can maintain and switch between several projects (e.g., Compose apps, pure Kotlin libraries, 2D game experiments) with isolated file trees, active tabs, and debugger states.

### Key Value Pillars
1. **Top Bar Quick Switcher**: Tapping the project title in the top app bar opens an anchor dropdown displaying all projects, their template badges, and active status.
2. **Drawer Workspace Explorer**: The side drawer features a "Projects & Workspaces" section showing recent projects, a "New Project" shortcut, and quick management triggers.
3. **Comprehensive Project Lifecycle**:
   - **Switch**: Instant switch of project files, active editor tab, and tree view.
   - **Clone**: Duplicate a project with a new name and package namespace.
   - **Rename**: In-place project renaming without file corruption.
   - **Delete**: Safe removal of inactive projects with confirmation guard.
4. **Persistent Multi-Project State**: Manages an in-memory & repository-backed list of active projects without extra resource overhead.

---

## 2. User Experience & Visual Design

### User Interaction Flows
1. **Top App Bar Dropdown Flow**:
   - The user taps the project title in the top bar (accompanied by a subtle `ExpandMore` chevron).
   - An anchor dropdown displays available projects with template type chips (`Compose`, `MVVM`, `2D Game`, etc.) and a checkmark on the active project.
   - Selecting a project smoothly updates the file tree, tabs, and editor.
2. **Side Drawer Projects Section**:
   - Below the header, a "Workspaces" accordion or list displays current projects with count badge.
   - Each project item includes an overflow menu (`⋮`) offering **Clone**, **Rename**, and **Delete**.
3. **Project Management Dialogs**:
   - **Rename Dialog**: Text input with existing name pre-filled and validation.
   - **Clone Dialog**: Clones all project files into a new project entity with custom name.
   - **Delete Confirmation Dialog**: Prevents accidental deletion of the last project or active workspace.

---

## 3. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Top App Bar & Header                            │
│  [☰]  [📁 ProjectName ▼ (Dropdown Switcher)]    [RAM Pill] [▶] [📦] [⛶]│
├────────────────────────────────────────────────────────────────────────┤
│ Dropdown Menu:                                                         │
│  - ✔ MyAwesomeApp (Compose)                                            │
│  -   GameEngine2D (2D Game)          [+ New Project]                   │
│  -   RestApiDemo (MVVM)              [⚙ Manage Projects]               │
├────────────────────────────────────────────────────────────────────────┤
│ ┌──────────────────────────┐  ┌──────────────────────────────────────┐ │
│ │ Slide-Over Drawer        │  │ Active Code Editor (Compose)         │ │
│ │  - Workspaces Manager    │  │  - Active File Tabs                  │ │
│ │  - Collapsible File Tree │  │  - Syntax Highlighting               │ │
│ └──────────────────────────┘  └──────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         IdeViewModel & State                           │
│  - projects: StateFlow<List<Project>>                                  │
│  - activeProjectId: StateFlow<String>                                  │
│  - switchProject(id) / cloneProject(id) / renameProject() / delete()   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Implementation Roadmap & Verification

1. **State & Model Enhancements**:
   - Update `IdeViewModel` to hold `projects: StateFlow<List<Project>>` and `activeProjectId: StateFlow<String>`.
   - Add methods: `switchProject(projectId)`, `cloneProject(projectId, newName)`, `renameProject(projectId, newName)`, `deleteProject(projectId)`.
2. **UI Components**:
   - Create `ProjectDropdownMenu` in the top app bar with template chips and active indicators.
   - Update `DrawerContent` with a clean "Workspaces" section and project action buttons.
   - Add `RenameProjectDialog` and `CloneProjectDialog` in `Dialogs.kt`.
3. **Verification**:
   - Run `compile_applet` to confirm clean compilation and zero regressions.
