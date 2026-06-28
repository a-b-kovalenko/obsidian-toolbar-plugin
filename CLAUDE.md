# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

IntelliJ IDEA plugin (Kotlin + Gradle) that adds one toolbar button and two actions for opening Obsidian from within the IDE. Uses `org.jetbrains.intellij.platform` Gradle plugin v2.16.0, Kotlin 2.2.0, targets IntelliJ IDEA Ultimate 2026.1, JVM target 21. Requires JDK 21 on the machine (Gradle toolchain auto-detection); JDK 25/26 alone won't work.

## Commands

```bash
./gradlew runIde        # launch sandbox IDE with the plugin installed (main dev loop)
./gradlew buildPlugin   # produce distributable .zip in build/distributions/
./gradlew build         # compile + check
./gradlew verifyPlugin  # validate plugin structure against IntelliJ requirements
```

Install built plugin: Settings → Plugins → ⚙ → Install Plugin from Disk → pick the `.zip`.

## Architecture

Source files in `src/main/kotlin/com/akovalenko/obsidian/`:

- **`OpenObsidianNoteAction`** — the single `AnAction`. `update()` enables the button when the project root contains `.obsidian/` and a file is open in the editor. `actionPerformed()` calls `findNote()` (walks vault tree for a `.md` whose `nameWithoutExtension` matches the current file); opens `obsidian://open?vault=<name>&file=<relative>` if found, falls back to `obsidian://open?vault=<name>`. Internal helpers: `vaultRoot(e)`, `vaultRoot(dir)`, `findNote`, `openVault`, `openNote`, `encode`. Registered in `MainToolBar` + `NavBarToolBar`.

- **`ObsidianStartupActivity`** (`ProjectActivity`) — runs on every project open. (1) If `~/bin/open-obsidian-vault` is absent, shows a balloon offering to install the bundled helper script. (2) Reads `customization.xml`; if `"OpenObsidianNote"` is not present, adds the action to `"root" → "Main Toolbar" → "Right"` via `CustomActionsSchema.addAction(ActionUrl(...))` and shows a "Restart IDE" balloon. Handles the case where the user customised the toolbar before installing — `add-to-group` in `plugin.xml` is ignored for such users.

- **`ScriptInstaller`** — copies `resources/scripts/open-obsidian-vault` to `~/bin/` and marks it executable.

URL encoding uses `URLEncoder.encode(..., UTF_8).replace("+", "%20")`. `obsidian://open?vault=&file=` (not `path=`) because `path=` only works for vaults already registered in Obsidian. `ProcessBuilder("open", url)` instead of `Desktop.browse()` — macOS does not forward custom URL schemes through `Desktop.browse()`.

Actions override `getActionUpdateThread() = ActionUpdateThread.BGT` (required in IntelliJ 2022.1+) and use `isEnabled` in `update()` — not `isEnabledAndVisible`, because hidden actions stop receiving `update()` calls.