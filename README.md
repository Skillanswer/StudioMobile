# StudioMobile

<p align="center">
  <img src="public/favicon.ico" alt="StudioMobile Logo" width="96" height="96" style="border-radius: 20%;" />
</p>

<p align="center">
  <strong>On-Device Mobile IDE for Kotlin & Java</strong><br>
  Engineered specifically for low-resource ARM SoCs, Android phones, foldables, and tablets.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208.0+-3DDC84?logo=android&logoColor=white" alt="Android Support" />
  <img src="https://img.shields.io/badge/Language-Kotlin%202.1-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20M3-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Build-Gradle%209-02303A?logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/CI%2FCD-Automated%20Release%20APK-2088FF?logo=githubactions&logoColor=white" alt="GitHub Actions" />
</p>

---

## 🚀 Overview

**StudioMobile** transforms your Android device into a self-contained, powerful development workstation. Run, edit, debug, and manage Kotlin and Java projects directly on your device without requiring an active internet connection, external servers, or heavy desktop dependencies.

Optimized to run smoothly on budget ARM SoCs and devices with limited RAM, StudioMobile utilizes an ultra-lightweight Abstract Syntax Tree (AST) execution engine and custom lexer for instantaneous execution, syntax highlighting, and code analysis.

---

## ✨ Features

### 📁 Comprehensive Project & File Management
- **Top App Bar Project Selector**: Instantly switch between active projects from the top bar dropdown or drawer.
- **Full Project Lifecycle**: Create new templates (Console App, Compose UI, Clean Architecture), switch active workspaces, clone existing projects, rename, and delete projects.
- **Recursive Collapsible File Tree**: Explore folders, create/delete/rename files and directories, and view contextual file-type badges (`KT`, `JAVA`, `JSON`, `GRADLE`, `XML`, `TXT`).

### 📝 Mobile-First Code Editor
- **Multi-Tab File Switching**: Easily jump between open files with close tabs and unsaved change indicators (`●`).
- **Real-Time Syntax Highlighting**: Color-coded tokens for keywords, types, strings, numbers, comments, and annotations.
- **Smart Autocomplete**: Contextual code completion for language keywords, standard library calls, and variable identifiers.
- **Code Beautifier & Formatter**: Reindent and clean up source code with a single tap.
- **Interactive Line Numbers & Breakpoints**: Tap any line number to toggle visual debug breakpoints.
- **Quick Symbol Access Bar**: Dedicated mobile typing accessory row for characters like `{`, `}`, `(`, `)`, `;`, `->`, `=`, `[`, `]`, `"`, `:`, and `Tab`.
- **Global In-File Search & Replace**: Find matches and perform replacements directly inside the editor.

### ⚡ On-Device Execution & Sandboxing
- **Embedded AST Interpreter**: Pure Kotlin Abstract Syntax Tree execution engine capable of executing Kotlin/Java code offline. Supports variables (`val`/`var`), conditional branches (`if`/`else`), loops (`for`/`while`), arithmetic & logical operations, and standard functions.
- **Security Sandbox**: Blocks unauthorized reflection or prohibited runtime execution to protect device stability.
- **Interactive Terminal Shell**: Built-in console supporting commands like `run`, `ls`, `cat`, `echo`, `pwd`, `clear`, and `help`.
- **Virtual Logcat Console**: Real-time logging with log level filters (`VERBOSE`, `DEBUG`, `INFO`, `WARN`, `ERROR`), tag search, and log clearing.

### 🛠️ Developer Productivity Tools
- **Live Layout Split View**: Preview Compose visual layouts in real time side-by-side with your code.
- **Floating Speed Dial Action Wheel**: Access frequent actions (Run, Format, Search, Terminal, Logcat, Profiler) with one thumb without cluttering the screen.
- **Android Keystore Manager**: Inspect `debug.keystore`, view key aliases, SHA-1 and SHA-256 fingerprint hashes, validity, and certificate metadata.
- **Low-Overhead ARM Profiler**: Real-time CPU and Memory telemetry tracking execution spikes and memory footprint.
- **Resource & Vector Asset Inspector**: Inspect Android drawables, colors, and XML vector assets.
- **Gradle Dependency Analyzer**: Inspect build dependencies and library version configurations.

---

## 📥 Download & Releases

Every push commit to the repository automatically triggers GitHub Actions to build the latest APK and publish an official GitHub Release.


### Installation
1. Download the `.apk` on your Android device.
2. If prompted, enable **Allow from this source** in Android Settings for your browser or file manager.
3. Tap **Install** and launch StudioMobile!

---

## 💻 Building from Source

### Prerequisites
- **JDK 17** (Temurin or OpenJDK recommended)
- **Android SDK** with Android 15 (`API 35`) and build tools installed
- **Gradle 9+**

### Local Build Instructions

Clone the repository:
```bash
git clone https://github.com/${{ github.repository }}.git
cd StudioMobile
```

Generate local debug keystore if not present:
```bash
keytool -genkey -v -keystore debug.keystore -storepass android \
  -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 \
  -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
```

Compile and assemble the Debug APK:
```bash
gradle assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Run local JVM unit and Robolectric tests:
```bash
gradle :app:testDebugUnitTest
```

---


## 📄 License

StudioMobile is distributed under the [AGPL3](LICENSE).
