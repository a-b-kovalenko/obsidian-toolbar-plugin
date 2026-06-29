# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

IntelliJ IDEA plugin (Kotlin + Gradle) that adds a single action (displayed as a toolbar button) for opening Obsidian from within the IDE. Uses `org.jetbrains.intellij.platform` Gradle plugin v2.16.0, Kotlin 2.2.0, targets IntelliJ IDEA Ultimate 2026.1, JVM target 21. Requires JDK 21 on the machine (Gradle toolchain auto-detection); JDK 25/26 alone won't work.

## Commands

```bash
./gradlew runIde        # launch sandbox IDE with the plugin installed (main dev loop)
./gradlew buildPlugin   # produce distributable .zip in build/distributions/
./gradlew signPlugin    # produce signed .zip (needs SIGNING_KEY / SIGNING_CERT / SIGNING_KEY_PASSPHRASE env vars, or local private.pem + chain.crt files)
./gradlew build         # compile + check
./gradlew verifyPlugin  # validate plugin structure against IntelliJ requirements
```

**Versioning:** always bump `version` in **both** `build.gradle.kts` and `src/main/resources/META-INF/plugin.xml` before publishing to the Marketplace. Forgetting either will cause a publish error.

Install built plugin: Settings → Plugins → ⚙ → Install Plugin from Disk → pick the `.zip`.

## Architecture

Source files in `src/main/kotlin/com/akovalenko/obsidian/`:

- **`OpenObsidianNoteAction`** — the single `AnAction`. `update()` enables the button when the project root contains `.obsidian/` and a file is open in the editor. `actionPerformed()` calls `findNote()` (walks vault tree for a `.md` whose `nameWithoutExtension` matches the current file); opens `obsidian://open?vault=<name>&file=<relative>` if found, falls back to `obsidian://open?vault=<name>`. Internal helpers: `vaultRoot(e)`, `vaultRoot(dir)`, `findNote`, `openVault`, `openNote`, `openUri`, `encode`. Registered in `MainToolBar` + `NavBarToolBar` declaratively via `plugin.xml` (`add-to-group`); no programmatic toolbar injection is used (internal `CustomActionsSchema`/`ActionUrl` APIs were removed to satisfy Marketplace requirements).

URL encoding uses `URLEncoder.encode(..., UTF_8).replace("+", "%20")`. `obsidian://open?vault=&file=` (not `path=`) because `path=` only works for vaults already registered in Obsidian. For opening the URI, a custom `openUri` helper method uses `ProcessBuilder` with OS-specific commands (`open` on macOS, `cmd /c start` on Windows, `xdg-open` on Linux) because Java's `Desktop.browse()` behaves unreliably across operating systems for custom URI schemes.

Actions override `getActionUpdateThread() = ActionUpdateThread.BGT` (required in IntelliJ 2022.1+) and use `isEnabled` in `update()` — not `isEnabledAndVisible`, because hidden actions stop receiving `update()` calls.