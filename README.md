# DevPilot 🚀

> **AI-Powered Developer Productivity Assistant & Engineering Workflow Companion**

DevPilot is a modern Android application built with **Jetpack Compose** and powered by **Google Gemini AI**. It provides software engineers with an end-to-end workspace that streamlines the entire software development lifecycle into a unified 6-stage workflow: **Understand → Plan → Build → Debug → Review → Improve**.

---

## 🌟 Key Features

### 1. 🧭 The 6-Stage Developer Workflow
- **Understand**: Explore system architecture diagrams, service flows (API Gateway → Domain Services → Persistence → Integrations), technology stacks, and important entry points. Ask interactive architectural questions to DevPilot AI.
- **Plan**: Describe a high-level feature or goal and receive an AI-decomposed, step-by-step implementation plan with effort estimates (in minutes) and priority ratings. Convert all steps into actionable tasks with a single click.
- **Build**: Track task progress, update subtask checklists, and manage active execution with interactive completion statuses and focus timers.
- **Debug**: Paste runtime exceptions, error logs, or stack traces for instantaneous AI root-cause diagnosis, exact file pinpointing, step-by-step resolution plans, and corrected code snippets. Convert fixes directly into trackable tasks.
- **Review**: Execute AI-assisted pull request quality and security gates to detect potential vulnerabilities, performance bottlenecks, and test coverage gaps before merging.
- **Improve**: Monitor repository health scores and resolve prioritized technical debt, maintainability risks, and architectural recommendations

---

### 2. ⚡ Command Palette (`⌘K` / `Ctrl+K`)
- Instant global search across repositories, backlog tasks, and workflow stages.
- Keyboard-friendly navigation directly accessible from the top bar

---

### 3. 📊 Engineering Overview & Next Best Action
- Real-time productivity metrics: overall health score, active task counters, technical debt indicators, and weekly focus hours.
- **Next Best Action Engine**: Intelligently surfaces the most critical blocker or high-impact engineering task to tackle next.

---

### 4. 🎨 Developer-Centric Design & Theming
- Fully responsive **Material 3** interface with seamless **Dark Mode** and **Light Mode** support.
- Code blocks with syntax styling and one-touch clipboard copying.
- High-contrast developer accent colors: Electric Cyan, Indigo Violet, Emerald Success, Amber Warning, and Crimson Danger

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
- **Language**: [Kotlin](https://kotlinlang.org/) (100% modern coroutines & asynchronous Flow)
- **Architecture**: Clean MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with KSP (Kotlin Symbol Processing)
- **AI Reasoning**: Google Gemini API via official client SDK
- **Dependency Management**: Gradle Version Catalogs (`libs.versions.toml`)
- **Testing**: [Robolectric](https://robolectric.org/) (JVM unit testing) & [Roborazzi](https://github.com/takahirom/roborazzi) (Visual screenshot regression testing)

---

## 📂 Project Structure

```
DevPilot/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/          # Room DB, DAOs & TypeConverters
│   │   │   │   │   ├── model/          # Entities (Repository, Task, Risk, etc.)
│   │   │   │   │   └── repository/     # Data Repository & Gemini AI Engine
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/     # Reusable Cards, Buttons, TopBar, ⌘K Dialog
│   │   │   │   │   ├── screens/        # Home, Projects, Workspace, Auth, Settings
│   │   │   │   │   └── theme/          # Material 3 Color Schemes & Typography
│   │   │   │   ├── viewmodel/          # DevPilotViewModel & State Management
│   │   │   │   └── MainActivity.kt     # App Entrypoint & Screen Routing
│   │   │   └── res/                    # Drawables, Strings, Vector Assets
│   │   └── test/                       # Robolectric & Roborazzi Test Suites
│   └── build.gradle.kts                # App-level Build Configuration
├── gradle/
│   └── libs.versions.toml              # Version Catalog Dependencies
├── build.gradle.kts                    # Project-level Gradle Configuration
├── settings.gradle.kts                 # Settings & Plugin Repositories
└── metadata.json                       # AI Studio Platform Configuration
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer) / IntelliJ IDEA
- JDK 11 or higher
- Android SDK (API Level 36 / Android 15+, Min SDK 24 / Android 7.0+)

### Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Kavin-Palanisamy/devpilot.git
   cd devpilot
   ```

2. **Configure API Keys (Optional)**:
   Add your Gemini API key to your environment or `.env` file:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```

3. **Build the Application**:
   ```bash
   gradle assembleDebug
   ```

4. **Run Local Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🧪 Verification & Testing

DevPilot includes automated JVM tests using Robolectric and Roborazzi for critical flows:
- **Run Unit Tests**: `gradle :app:testDebugUnitTest`
- **Verify UI Screenshots**: `gradle :app:verifyRoborazziDebug`
- **Record Baseline Screenshots**: `gradle :app:recordRoborazziDebug`

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
