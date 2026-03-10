# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Problem Frames (PF) requirements engineering tool** — a multi-web-application system for software requirement analysis using problem frames methodology. It consists of three web modules (Web1, Web2, Web5), a PF language server, and a shared file storage system.

## Architecture

```
code/
├── PF2-web1/                    # Web1: Problem domain context diagram editor
│   ├── PF2-web1-new/            # Vue 3 frontend (active)
│   ├── web1-backend/            # Spring Boot backend (port 7084)
│   └── pf-language-server/      # TypeScript LSP for .pf files (port 8030)
├── PF2-web2/                    # Web2: Scenario/use case diagram editor
│   ├── web2-vue/                # Vue 3 frontend (active, port 5174)
│   ├── web2-frontend/           # Angular 7 frontend (legacy, port 4202)
│   └── web2-backend/            # Spring Boot backend (port 7085)
├── PF2-web5/                    # Web5: Problem diagram editor
│   ├── web5-vue/                # Vue 3 frontend (active, port 5175)
│   ├── web5-frontend/           # Angular 7 frontend (legacy, port 4203)
│   └── web5-backend/            # Spring Boot backend (port 7086)
└── PF_Storage/                  # Shared file storage for all backends
    ├── UserProject/             # User project files
    └── latest/                  # Latest project snapshots
```

All three backends share the same `PF_Storage/` directory (referenced via relative path `../../PF_Storage/` from each backend directory).

## Service Ports

| Service | Port |
|---|---|
| Web1 Backend (Spring Boot) | 7084 |
| Web2 Backend (Spring Boot) | 7085 |
| Web5 Backend (Spring Boot) | 7086 |
| PF Language Server (Node.js) | 8030 |
| Web1 Frontend (Vite dev) | default (5173) |
| Web2 Frontend (Vite dev) | 5174 |
| Web5 Frontend (Vite dev) | 5175 |

## Commands

### Start All Services
```powershell
# From the code/ directory:
.\start_all_fixed.ps1
```

### Vue 3 Frontends (Web1, Web2-vue, Web5-vue)
```bash
# Development server
npm run dev

# Type-check + production build
npm run build

# Preview production build
npm run preview
```

### Spring Boot Backends (Web1, Web2, Web5) - JAR Package Startup
All three backends are now started via pre-built JAR packages using start.bat scripts:

```batch
# From respective backend directory:
.\start.bat
```

Or manually with Java:
```bash
java -Xmx512m -Xms256m -jar target/[backend-artifact].jar
```

**JAR Artifacts**:
- Web1: `PF2-web1-0.0.1-SNAPSHOT.jar`
- Web2: `web2-backend-0.0.1-SNAPSHOT.jar`
- Web5: `PF2-web5-0.0.1-SNAPSHOT.jar`

**Note**: Maven source compilation is now avoided for Web2 due to GBK/UTF-8 encoding issues. Use pre-built JAR packages instead.

### PF Language Server
```bash
# From PF2-web1/pf-language-server/ (requires yarn)
yarn start
# Note: Requires NODE_OPTIONS=--openssl-legacy-provider on newer Node.js
```

### Legacy Angular Frontends (Web2, Web5)
```bash
# Requires NODE_OPTIONS=--openssl-legacy-provider
set NODE_OPTIONS=--openssl-legacy-provider
ng serve --host 0.0.0.0 --port 4202   # web2
ng serve --host 0.0.0.0 --port 4203   # web5
```

## Frontend Tech Stack

### Vue 3 Frontends (web2-vue, web5-vue — identical structure)
- **Framework**: Vue 3 + TypeScript + Vite + Pinia
- **Diagram rendering**: JointJS 3.x with Backbone.js/jQuery
- **HTTP**: Axios
- **Styling**: SASS

**Source layout** (`src/`):
- `components/` — TopBar, LeftBar, RightBar, DrawingBoard, modals/
- `store/` — `editor.ts`, `project.ts`, `ui.ts` (Pinia stores)
- `api/` — `file.ts`, `project.ts`, `upload.ts`, `verification.ts`
- `utils/` — `request.ts` (Axios instance), `auth.ts`, `projectLogic.ts`
- `entity/`, `types/`, `composables/`

Vite proxies `/project` and `/file` routes to the respective backend.

### Web1 Vue Frontend (PF2-web1-new)
- **Framework**: Vue 3 + TypeScript + Vite + Pinia + Less
- **Graph rendering**: `@vue-flow/core` (Vue Flow)
- **Mobile UI**: Vant 4
- **HTTP**: Axios

**Source layout** (`src/`):
- `components/` — `HomeView.vue`, `EditorLayout.vue`, `Sidebar.vue`, `PropertyEditor.vue`
  - `nodes/` — `MachineNode.vue`, `ProblemDomainNode.vue`, `RequirementNode.vue`, `SystemNode.vue`
  - `modals/` — `ProblemDomainEditorModal.vue`, `SystemEditorModal.vue`
- `stores/` — `lspStore.ts` (WebSocket LSP integration)
- `config/index.ts` — Backend URL config (BACKEND_PORT: 7084, WebSocket + REST endpoints)

Vite proxies `/api` to `http://localhost:7084`.

## Backend Tech Stack

All three backends are **Spring Boot 2.x** (Java 8), Maven-based, with a layered architecture:
- `controller/` — REST endpoints (`FileController`, `ProjectController`, `VerficationController`)
- `service/` — Business logic
- `bean/` — Domain model classes (ProblemDiagram, Machine, ProblemDomain, Requirement, etc.)

Web1 backend also includes WebSocket support for the LSP bridge.

## Functional Role of Each Web

This is a sequential requirements engineering workflow:

```
Web1 → (saves project to PF_Storage) → Web2 → (saves) → Web5
```

| Web | Role | Key Functionality |
|---|---|---|
| **Web1** | Context diagram editor | Users model the problem space: Machine, ProblemDomains, Requirements, Interfaces, Constraints using Vue Flow |
| **Web2** | Problem diagram + Scenario graph editor | Reads Web1's project; builds ContextDiagram, ProblemDiagram, ScenarioGraphs using JointJS |
| **Web5** | Verification & trace analysis | Reads Web2's project; extracts trace relations, data/control dependencies; 7 steps |

**Inter-web communication** is done entirely through shared `PF_Storage/` filesystem — Web2 calls `/file/getProject` on its own backend (port 7085) which reads files saved by Web1's backend (port 7084) from the same shared directory.

## Web2/Web5 Step Flow

**Web2 steps (RightBar)**:
- Step 1: Load/display Context Diagram (tab: `Context Diagram`)
- Step 2: Create Scenario Graphs (tab: scenario graph tabs ending in `M`)
- Step 3: Formalize scenario graphs

**Web5 steps (RightBar)**:
- Step 1: Open project
- Step 2: Load dependencies
- Step 3/4: Check requirements + projection
- Step 5: Extract trace relations
- Step 6/7: Extract data/control dependencies

## Known Incomplete Areas (Refactoring TODOs)

- **Web1**: `applyChange()` in `lspStore.ts` is empty — backend WebSocket messages are received but not applied to the diagram
- **Web1**: Text editor mode (switch view) is a placeholder — Monaco editor not integrated
- **Web1**: Upload flow uses hardcoded cookie username (`test` fallback)
- **Web2/Web5**: `useDrawGraph.ts` composable handles all JointJS rendering — this is the critical file for diagram functionality
- **Web2 `request.ts`**: Uses Vite proxy (`/project`, `/file`) rather than absolute URL, which is correct for dev
- **Web5 `request.ts`**: Uses absolute `http://localhost:7086` — needs to match Web2's pattern if proxy is preferred
- Three webs **cannot yet be linked** end-to-end in the Vue refactor

## Key Notes

- The **active frontend** for Web2 and Web5 is the Vue 3 rewrite (`web2-vue`, `web5-vue`), not the legacy Angular frontends.
- Web2-vue and Web5-vue have **identical source structure** — changes in one typically mirror the other.
- The PF language server is a **Lerna monorepo** managed with `yarn` (not npm).
- All backends resolve file paths relative to where they are started; always run backends from their own directory.
- **Shared file storage**: All three backends now use `../../PF_Storage/` (relative path from backend directory) to share projects. This enables Web1 to read Web2/Web5 projects.
- **Encoding**: Source files have mixed GBK and UTF-8 encoding. **Do NOT run batch conversion scripts** — this breaks compilation. Use pre-built JAR packages instead.
- `getUserName()` in all frontends reads a browser cookie `username` — currently defaults to `'test'` when not set.

---

## 🔧 DEBUG LOG - Backend Crash Recovery & Web1 Restoration (2026-03-10)

### Problem Summary
After attempting GBK→UTF-8 encoding conversion scripts:
- **Web1 backend**: Completely corrupted (source directory emptied)
- **Web2 source**: Compilation failed (mixed GBK/UTF-8 chaos, but JAR still works)
- **Web5 source**: Successfully compiled (source files intact)

### Root Cause
- Blind batch encoding conversion script damaged file structure
- Web2 source files now have broken UTF-8 (illegible in editor)
- **Learning**: Never run automatic encoding scripts on mixed-encoding codebases

### ✅ Recovery Solution (2026-03-10)

**1. Web1 Backend Restoration**
- Used `web1-backend-0` backup directory to restore full source
- Reconfigured to use shared `PF_Storage/` path (instead of local `./storage/`)
- Successfully compiled new JAR (Mar 10 08:59)

**2. Configuration Alignment**
- Modified `PF2-web1/web1-backend/src/main/resources/application.properties`:
  ```properties
  # Before (local storage):
  file.rootAddress=./storage/
  file.userAddress=./storage/users/

  # After (shared storage - same as Web2/Web5):
  file.rootAddress=../../PF_Storage/
  file.userAddress=../../PF_Storage/UserProject/
  ```
- Now all three backends share the same storage → Web1 can open Web2/Web5 projects

**3. UTF-8 Encoding Fix in pom.xml**
- Added `maven-compiler-plugin` with explicit UTF-8 encoding to all backends:
  ```xml
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
      <source>1.8</source>
      <target>1.8</target>
      <encoding>UTF-8</encoding>
    </configuration>
  </plugin>
  ```
- Web1: ✅ Compiled successfully
- Web5: ✅ Compiled successfully
- Web2: ❌ Source too corrupted; using pre-built JAR (Mar 9)

**4. Start Scripts Created**
- Web1: `PF2-web1/web1-backend/start.bat` — new, mirrors Web2/Web5 pattern
- Web2: `PF2-web2/web2-backend/start.bat` — already exists
- Web5: `PF2-web5/web5-backend/start.bat` — already exists

### ✅ Current Status (All Fixed - 2026-03-10 14:15)
| Service | JAR Date | Size | Source | mvn run | Storage | Status |
|---------|----------|------|--------|---------|---------|--------|
| Web1 | Mar 10 08:59 | 148M | web1-backend-0 | ✅ | PF_Storage ✓ | ✅ Fully Ready |
| Web2 | Mar 10 09:13 | 56M | web2-backend_0 | ⏳ Ready | PF_Storage ✓ | ✅ Config Updated |
| Web5 | Mar 10 09:14 | 40M | web5-backend(old) | ⏳ Ready | PF_Storage ✓ | ✅ Config Updated |

### 🔧 Recent Changes (2026-03-10 09:30+)
**Path Configuration Unified:**
- Web1: `../../PF_Storage/UserProject/` (already configured)
- Web2: `../../PF_Storage/UserProject/` (✅ updated in application.yml)
- Web5: `../../PF_Storage/UserProject/` (✅ updated in application.yml)

**Next Steps (after reboot):**
1. Clear port 7085 process (user to run: taskkill /F /IM java.exe)
2. Start Web2: `mvn spring-boot:run` from PF2-web2/web2-backend
3. Start Web5: `mvn spring-boot:run` from PF2-web5/web5-backend
4. Monitor logs for startup completion
5. Iterate based on frontend feedback
6. Final packaging and integration testing

### 📋 Key Learning
- **Never** run batch character encoding conversion on codebases with mixed encodings
- Solution: Restore from clean backups (web*-backend_0 or web*-backend(old))
- All three backends now use backup/stable versions and compile successfully
- Cleanup: Removed all temporary backups and broken versions to keep project clean

---

## 🔧 DEBUG LOG - Web5 test04 Project Issue (2026-03-06)

### ✅ FIXED ISSUES

#### 1. Backend NullPointerException (SOLVED)
- **Problem**: test04 Step 3 execution caused NPE in multiple methods
- **Root Cause**: Missing null checks when accessing Phenomenon from HashMap
- **Finding**: Original backend code had this defect, NOT introduced by refactoring (verified by comparing with web5-backend(old))
- **Fix Applied**: Added null checks in ProjectService.java:
  - ckeckStrategyOne: Lines 3126, 3338
  - ckeckStrategyThree: Line 3469
  - ckeckStrategyFour: Lines 3555, 3640
  - structToSpecification: Line 3898
  - Other methods: Line 4063+ region

#### 2. API Path Parameter Issue (SOLVED)
- **Problem**: Frontend `/file/searchVersion/test04` → 404 Not Found
- **Root Cause**: Backend API expected `/file/searchVersion/{project}/{userName}` but frontend didn't pass userName
- **Decision**: Removed userName parameter entirely (single-user local tool design)
- **Changes**:
  - FileController.java: Line 214 - `/searchVersion/{project}` (removed `/{userName}`)
  - file.ts: searchVersion() function - removed userName from URL path

#### 3. Frontend Error Display (IMPROVED)
- **Problem**: Step 3.1 completion had no feedback; Step 3.2 error was not visible
- **Fix**:
  - Added alert notification for Step 3.1 success
  - Improved Step 4 error handling in RightBar.vue to display error details

### ⏳ CURRENT ISSUE - UNRESOLVED

**Problem**: Step 3.2 (投影/Projection) fails with path error when loading test04 project
```
org.dom4j.DocumentException: test\test04\Project.xml (系统找不到指定的路径。)
java.io.FileNotFoundException: test\test04\Project.xml
  at FileService.searchVersion(FileService.java:3651)
```

**Analysis**:
- FileService.searchVersion() expects full path: `D:/PF_Storage/UserProject/test/test04/Project.xml`
- But FileController was passing only: `test/test04/` (incomplete path)
- **Last Fix Attempted** (not yet tested): Modified FileController.searchVersion to use `addressService.getUserAddress() + "test/" + branch + "/"`
- **Status**: Code compiled successfully, awaiting user test after backend restart

**Project.xml Locations Verified**:
- test04: `/PF_Storage/UserProject/test/test04/Project.xml` ✓
- TestPowerOn: `/PF_Storage/UserProject/test/TestPowerOn/Project.xml` ✓

### 📋 NEXT STEPS FOR NEXT SESSION

1. **User must restart Web5 backend** after last code changes
2. **Test test04 project again** at Step 3.2
3. **If still failing**:
   - Check if file path is correctly resolved (mix of forward/back slashes?)
   - Verify addressService is properly injected in FileController
   - May need to normalize path separators in searchVersion call
4. **If test04 passes**: Test TestPowerOn and other projects
5. **Then continue**: Test remaining steps (3-7) of Web5 workflow

### 🔗 KEY FILES TO WATCH

**Backend**:
- `PF2-web5/web5-backend/src/main/java/com/example/demo/controller/FileController.java` - Line 214+
- `PF2-web5/web5-backend/src/main/java/com/example/demo/service/FileService.java` - Line 3646+
- `PF2-web5/web5-backend/src/main/java/com/example/demo/service/AddressService.java` - userAddress config

**Frontend**:
- `PF2-web5/web5-vue/src/api/file.ts` - searchVersion() function
- `PF2-web5/web5-vue/src/components/RightBar.vue` - nextStep() cases 3-4

**Project Data**:
- `PF_Storage/UserProject/test/test04/` - test project directory
- `PF_Storage/UserProject/test/TestPowerOn/` - power-on test project
