# Android Studio Mobile (ARM SoC & Low Resource Optimized)

A mobile-first Android Studio clone built natively with Jetpack Compose for Android smartphones. Designed for maximum efficiency on low-resource ARM SoC processors (ARMv7, ARM64, Snapdragon 400/600/700 series, MediaTek Dimensity/Helio) with lightweight RAM memory management, fast lazy syntax tokenization, battery-friendly rendering, and a clean minimalist mobile UI.

## User Review & Critical Decisions

> [!IMPORTANT]
> The user requested explicit low-resource & ARM SoC processor optimization:
> - **ARM SoC Architecture Optimization**: Zero native binary overhead (`.so`), pure Kotlin memory management tuned for ARM NEON/Vector instructions and low-end octa-core/quad-core CPUs.
> - **Low Resource Memory Management**:
>   - **Lazy Line Tokenization**: Tokenizes only currently visible editor lines rather than entire 10,000-line files to keep RAM under 60 MB.
>   - **Memory Heap Conservation**: Garbage collection (GC) friendly data structures preventing frame drops or lag spikes.
>   - **Battery & Thermal Guard**: Pauses background execution when idle to prevent phone thermal throttling.
> - **Essential Mobile IDE Toolset**:
>   - Live IntelliSense Code Autocomplete popup.
>   - Interactive Terminal Shell (`gradle assembleDebug`, `git status`, `ls`, `grep`).
>   - Real-time Performance Profiler (ARM CPU %, RAM Heap MB, FPS meter).
>   - Git Version Control & Branch Manager.
>   - APK / AAB Decompiler & Asset Inspector.
>   - Build & Sign APK / AAB with JKS Keystores.

---

## 1. Overview & Core Concept

**Android Studio Mobile** delivers desktop-class mobile development capabilities while remaining blazing fast and smooth on budget and low-tier Android smartphones.

### Key Performance Pillars for ARM SoC
1. **Low Memory Footprint (<60 MB RAM)**: Efficient data structures for AST parsing, token caching, and project trees ensure seamless multitasking without Android OS OOM kills.
2. **ARM Vectorization & CPU Efficiency**: Lightweight regex tokenizer and AST interpreter optimized for ARM NEON SIMD execution without heavy native JNI dependencies.
3. **Smooth 60 FPS Compose Rendering**: Sub-millisecond recompositions, lazy list layouts, and minimal re-draws for low-frequency mobile GPUs (Adreno, Mali).

---

## 2. User Experience & Visual Design

### Clean Ergonomics & ARM Resource Monitor
- **Header Bar**: Project status pill (`✔ Ready` / `⚡ Building`), active file tab, Git branch indicator, and ARM SoC Resource Pill (`RAM: 42MB | CPU: 4%`).
- **Floating Action Speed Dial Wheel**: Fast 1-tap access to Run, Debug, Build APK/AAB, Terminal, Profiler, and Search.
- **Editor Canvas**: Monospace font with lazy-loaded line numbers, line highlight, and token coloring.

---

## 3. Technical Architecture & Component Structure

### Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│             ARM SoC Optimized Android Studio Mobile IDE                │
├────────────────────────────────────────────────────────────────────────┤
│ ┌──────────────────────────┐  ┌──────────────────────────────────────┐ │
│ │ Slide-Over Drawer        │  │ Header Bar (ARM RAM / CPU Meter)     │ │
│ │  - Collapsible File Tree │  ├──────────────────────────────────────┤ │
│ │  - Resource Inspector    │  │ Code Editor View (Compose)           │ │
│ │  - Git Manager           │  │  - Lazy Tokenizer (<60MB RAM)        │ │
│ └──────────────────────────┘  │  - Floating Action Speed Dial Wheel  │ │
│                               └──────────────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────────┤
│ Resizable Bottom Sheet (Logcat / Terminal / Profiler / Decompiler)     │
│  - Filterable Logcat Logs                                              │
│  - Interactive Terminal Shell ($ gradle assembleDebug, git status)     │
│  - ARM SoC Real-Time CPU & RAM Memory Heap Monitor                     │
│  - APK/AAB Decompiler Structure                                        │
│  - Gradle Dependency Analyzer                                          │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       IDE Core Engine & Services                       │
│ ┌────────────────────────┐ ┌───────────────────┐ ┌───────────────────┐ │
│ │ Project Repository     │ │ Lazy CodeLexer &  │ │ AstEngine &       │ │
│ │ & Git Manager          │ │ Autocomplete      │ │ Profiler Meter    │ │
│ └────────────────────────┘ └───────────────────┘ └───────────────────┘ │
│ ┌───────────────────────┐ ┌───────────────────┐ ┌───────────────────┐ │
│ │ KeystoreManager       │ │ TerminalEngine    │ │ ApkDecompiler     │ │
│ └───────────────────────┘ └───────────────────┘ └───────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Implementation Roadmap & Verification

1. **ARM SoC Performance Engine (`ProfilerEngine.kt`)**:
   - Real-time ARM CPU load %, RAM heap memory footprint, and rendering FPS monitor.
2. **Lazy Tokenizer & Autocomplete (`AutocompleteEngine.kt`)**:
   - Contextual IntelliSense suggestions with zero GC allocation overhead.
3. **Terminal Shell Simulator (`TerminalEngine.kt`)**:
   - Shell command runner (`gradle assembleDebug`, `git status`, `ls`, `grep`, `pwd`).
4. **Git Branch & Version Control Manager**:
   - Branch badge, commit dialog, and file diff viewer.
5. **APK Decompiler & Dependency Analyzer**:
   - Inspect APK zip structure and Gradle Version Catalog.
6. **Floating Action Speed Dial Wheel**:
   - Floating action wheel for instant touch access.
7. **Verification**:
   - Execute `compile_applet` to confirm zero build or runtime errors.
